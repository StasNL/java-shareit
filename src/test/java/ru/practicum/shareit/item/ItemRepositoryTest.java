package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.CreatingModels;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AllArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@Sql("classpath:schemaTest.sql")
public class ItemRepositoryTest extends CreatingModels {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Test
    void findAllByPartOfNameTest() {
        User owner = createDefaultUser();
        owner.setId(null);
        owner = userRepository.save(owner);

        Item item1 = createDefaultItem();
        item1.setOwner(owner);

        Item item2 = createDefaultItem();
        item2.setOwner(owner);

        item1.setId(null);
        item1.setName("Предмет");
        item1.setDescription("Отличная вещь");

        item2.setId(null);
        item2.setName("Вещь");
        item2.setDescription("Отличный предмет");


        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        String text = "вещь";

        PageRequest request = PageRequest.of(0, 100);

        List<Item> checkList = itemRepository.findAllByPartOfName(text, request).toList();

        assertEquals(item1, checkList.get(0));
        assertEquals(item2, checkList.get(1));
    }
}
