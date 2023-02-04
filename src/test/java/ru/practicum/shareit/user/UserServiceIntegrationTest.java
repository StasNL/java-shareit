package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.CreatingModels;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest extends CreatingModels {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void updateUserTest() {
        User userToSave = createDefaultUser();
        userToSave.setId(null);
        UserResponse userResponse1 = userService.createUser(userToSave);
        long userToSaveId = userResponse1.getId();
        userToSave.setId(userToSaveId);

        User userToUpdate = createDefaultUser();
        userToUpdate.setId(userToSaveId);
        userToUpdate.setName("updateName");
        userService.updateUser(userToUpdate, userToUpdate.getId());

        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.id = ?1", User.class);

        User user = query.setParameter(1, userToSaveId)
                .getSingleResult();

        assertEquals(user.getId(), userToUpdate.getId());
        assertEquals(user.getName(), userToUpdate.getName());
        assertEquals(user.getEmail(), userToUpdate.getEmail());
    }
}