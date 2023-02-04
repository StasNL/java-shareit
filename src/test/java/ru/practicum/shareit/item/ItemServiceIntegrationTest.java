package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
public class ItemServiceIntegrationTest extends CreatingModels {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void getAllItemsByUserIdTest() {
// Создание пользователя.
        User user = createDefaultUser();
        user.setId(null);
        UserResponse userResponse = userService.createUser(user);

        long userId = userResponse.getId();
// Создание первого предмета.
        Item item1 = createDefaultItem();
        item1.setId(null);

        ItemDtoForCreate itemDto1 = ItemMapper.itemToNewItemDto(item1);
        ItemResponse itemResponse1 = itemService.createItem(itemDto1, userId);

// Создание второго предмета.
        Item item2 = createDefaultItem();
        item2.setId(null);

        ItemDtoForCreate itemDto2 = ItemMapper.itemToNewItemDto(item2);
        ItemResponse itemResponse2 = itemService.createItem(itemDto2, userId);

// Запрос в БД.
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner.id = ?1", Item.class);
        query.setParameter(1, userId);
// Проверка.
        List<Item> items = query.getResultList();
        List<ItemResponse> itemResponses = ItemMapper.itemToItemResponse(items);
        ItemResponse item1FromDb = itemResponses.get(0);
        ItemResponse item2FromDb = itemResponses.get(1);

        assertEquals(itemResponse1, item1FromDb);
        assertEquals(itemResponse2, item2FromDb);
    }
}