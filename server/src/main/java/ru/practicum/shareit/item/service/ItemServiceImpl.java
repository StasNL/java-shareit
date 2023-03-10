package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.notfound.BadRequestException;
import ru.practicum.shareit.exceptions.notfound.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.notfound.ErrorType.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl extends CommonService implements ItemService {

    @Autowired
    public ItemServiceImpl(UserRepository userRepository,
                           ItemRepository itemRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository requestRepository) {
        super(userRepository, itemRepository, requestRepository, bookingRepository, commentRepository);
    }

    @Transactional
    public ItemResponse createItem(ItemDtoForCreate itemDto, Long ownerId) {
        User owner = getUserWithCheck(ownerId);
        ItemRequest itemRequest = null;
        Long requestId = itemDto.getRequestId();
        if (requestId != null)
            itemRequest = getItemRequestWithCheck(requestId);

        Item item = ItemMapper.itemDtoToItem(itemDto, itemRequest, owner);
        item = itemRepository.save(item);

        log.info("item ?????????????? ????????????.");
        return ItemMapper.itemToItemResponse(item);
    }

    @Transactional
    public ItemResponse updateItem(Item item, long ownerId, Long itemId) {
        getUserWithCheck(ownerId);
        Item updatedItem = getItemWithCheck(itemId);

        if (ownerId != updatedItem.getOwner().getId())
            throw new NotFoundException(useType(OWNER));

        if (item.getName() != null)
            updatedItem.setName(item.getName());

        if (item.getDescription() != null)
            updatedItem.setDescription(item.getDescription());

        if (item.getAvailable() != null)
            updatedItem.setAvailable(item.getAvailable());

        updatedItem = itemRepository.save(updatedItem);

        log.info("item c id = " + itemId + " ?????????????? ????????????????????????????.");
        return ItemMapper.itemToItemResponse(updatedItem);
    }

    public ItemResponse getItemById(Long itemId, Long userId) {
        getUserWithCheck(userId);
        Item item = getItemWithCheck(itemId);

        log.info("item ?? id = " + itemId + " ?????????????? ????????????.");
        if (item.getOwner().getId().equals(userId))
            return ItemMapper.itemToItemResponse(item,
                    getLastBookingByItem(itemId),
                    getNextBookingByItem(itemId),
                    getComments(itemId));
        else
            return ItemMapper.itemToItemResponseWithComments(item, getComments(itemId));
    }

    public List<ItemResponse> getAllItemsByUserId(Long userId, Integer index, Integer size) {
        getUserWithCheck(userId);

        List<Integer> params = makePaginationParams(index, size);

        List<ItemResponse> items = itemRepository.findAllByOwner_Id(userId,
                        PageRequest.of(params.get(0), params.get(1)))
                .stream()
                .map(item -> {
                    if (item.getOwner().getId().equals(userId))
                        return ItemMapper.itemToItemResponse(item,
                                getLastBookingByItem(item.getId()),
                                getNextBookingByItem(item.getId()),
                                getComments(item.getId()));
                    else
                        return ItemMapper.itemToItemResponseWithComments(item, getComments(item.getId()));
                })
                .sorted(Comparator.comparing(ItemResponse::getId))
                .collect(Collectors.toList());

        log.info("???????????? item-???? ???????????????????????? ?? id = " + userId + " ?????????????? ????????????.");
        return items;
    }

    public List<ItemResponse> searchByName(String text, Long userId, Integer index, Integer size) {
        getUserWithCheck(userId);

        if (text.isEmpty())
            return List.of();

        List<Integer> params = makePaginationParams(index, size);
        List<ItemResponse> items = itemRepository.findAllByPartOfName(text, PageRequest.of(params.get(0), params.get(1)))
                .stream()
                .map(ItemMapper::itemToItemResponse)
                .collect(Collectors.toList());
        log.info("???????????? item-????, ?????? ?? ???????????????? ?????? ???????????????? ?????????????????????? " + text + " ??????????????????.");
        return items;
    }

    @Override
    public CommentResponse createComment(Comment comment, Long itemId, Long userId) {
        User user = getUserWithCheck(userId);
        Item item = getItemWithCheck(itemId);
        LocalDateTime time = LocalDateTime.now();
        List<Booking> booking = bookingRepository.findBookingsByBooker_IdAndStartBefore(userId, time);
        if (booking.size() == 0)
            throw new BadRequestException("???????????? ???????????????????????? ???? ?????????????????????? ??????????????????.");
        comment.setAuthorName(user.getName());
        comment.setCreated(time);
        comment.setItem(item);
        comment = commentRepository.save(comment);

        return ItemMapper.commentToCommentResponse(comment);
    }

    private Booking getLastBookingByItem(Long itemId) {
        return bookingRepository
                .findBookingsByItem_IdAndStatusOrderByEndDesc(itemId, Status.APPROVED)
                .stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBookingByItem(Long itemId) {
        return bookingRepository
                .findBookingsByItem_IdAndStatusOrderByStart(itemId, Status.APPROVED)
                .stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private List<CommentResponse> getComments(Long itemId) {
        return ItemMapper.commentsToCommentsResponse(commentRepository.findAllByItem_Id(itemId));
    }
}