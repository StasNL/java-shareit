package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.service.ItemRequestService;
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
        author.setEmail("author@mail.ru");
        userService.createUser(author);
//     Создание пользователя.
        User user = createDefaultUser();
        user.setId(2L);
        userService.createUser(user);
//     Создание запроса вещей.
        ItemRequest itemRequest1 = createDefaultItemRequest();
        requestService.createRequest(itemRequest1, author.getId());
        ItemRequest itemRequest2 = createDefaultItemRequest();
        itemRequest2.setId(2L);
        requestService.createRequest(itemRequest2, author.getId());

//     Запрос в БД.
        TypedQuery<ItemRequest> query =
                em.createQuery("select ir from ItemRequest ir where ir.author.id = ?1", ItemRequest.class);
        query.setParameter(1, author.getId());
        List<ItemRequest> itemRequests = query.getResultList();
//     Проверка
        ItemRequest itemRequest1FromDb = itemRequests.get(0);
        ItemRequest itemRequest2FromDb = itemRequests.get(1);

        assertEquals(itemRequest1, itemRequest1FromDb);
        assertEquals(itemRequest2, itemRequest2FromDb);
    }
}
