package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_Id(Long ownerId);

    /**
     * Поиск всех предметов, в имени или описании которых встречается указанная строка.
     */

    @Query(" select item from Item item " +
            " where upper(item.name) like upper(concat('%', ?1, '%')) " +
            " or upper(item.description) like upper(concat('%', ?1, '%')) " +
            " and item.available = true")
    List<Item> findAllByPartOfName(String text);
}
