package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
        userService.createUser(user);

        long userId = user.getId();
// Создание первого предмета.
        Item item1 = createDefaultItem();

        ItemDtoForCreate itemDto1 = ItemMapper.itemToNewItemDto(item1);
        itemService.createItem(itemDto1, userId);
// Создание второго предмета.
        Item item2 = createDefaultItem();
        item2.setId(2L);

        ItemDtoForCreate itemDto2 = ItemMapper.itemToNewItemDto(item2);
        itemService.createItem(itemDto2, userId);
// Запрос в БД.
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner.id = ?1", Item.class);
        query.setParameter(1, userId);
// Проверка.
        List<Item> items = query.getResultList();
        Item item1FromDb = items.get(0);
        Item item2FromDb = items.get(1);

        assertEquals(item1, item1FromDb);
        assertEquals(item2, item2FromDb);
    }
}
