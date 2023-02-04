package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.CreatingModels;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceIntegrationTest extends CreatingModels {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService requestService;

    @Test
    void getAllRequestsExceptAuthor() {
//     Создание автора запроса.
        User author = createDefaultUser();
        author.setId(null);
        author.setEmail("author@mail.ru");
        UserResponse userResponse1 = userService.createUser(author);
        long authorId = userResponse1.getId();
        author.setId(authorId);

//     Создание пользователя.
        User user = createDefaultUser();
        user.setId(null);
        UserResponse userResponse2 = userService.createUser(user);
        long userId = userResponse2.getId();
        user.setId(userId);

//     Создание запроса вещей.
        ItemRequest itemRequest1 = createDefaultItemRequest();
        itemRequest1.setId(null);
        ItemRequestResponse requestResponse1 = requestService.createRequest(itemRequest1, author.getId());
        long itemRequest1Id = requestResponse1.getId();
        itemRequest1.setId(itemRequest1Id);

        ItemRequest itemRequest2 = createDefaultItemRequest();
        itemRequest2.setId(null);
        ItemRequestResponse requestResponse2 = requestService.createRequest(itemRequest2, author.getId());
        long itemRequest2Id = requestResponse2.getId();
        itemRequest2.setId(itemRequest2Id);

//     Запрос в БД.
        TypedQuery<ItemRequest> query =
                em.createQuery("select ir from ItemRequest ir where ir.author.id = ?1", ItemRequest.class);
        query.setParameter(1, authorId);
        List<ItemRequest> itemRequests = query.getResultList();
//     Проверка
        ItemRequest itemRequest1FromDb = itemRequests.get(0);
        ItemRequest itemRequest2FromDb = itemRequests.get(1);

        assertEquals(itemRequest1, itemRequest1FromDb);
        assertEquals(itemRequest2, itemRequest2FromDb);
    }
}