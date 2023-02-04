package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.CreatingModels;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerWebTest extends CreatingModels {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .defaultRequest(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .alwaysExpect(status().isOk())
                .build();

        User user = createDefaultUser();
        userResponse = UserMapper.userToUserResponse(user);
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResponse))
                )
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail()), String.class));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(userResponse);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userResponse)))
                .andExpect(jsonPath("id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("name", is(userResponse.getName()), String.class))
                .andExpect(jsonPath("email", is(userResponse.getEmail()), String.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{userId}", "1"));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(any()))
                .thenReturn(userResponse);

        mvc.perform(get("/users/{userId}", 1))
                .andExpect(jsonPath("id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("name", is(userResponse.getName()), String.class))
                .andExpect(jsonPath("email", is(userResponse.getEmail()), String.class));
    }

    @Test
    void getAllUsersTest() throws Exception {
        List<UserResponse> userResponses = new ArrayList<>();
        userResponses.add(userResponse);
        when(userService.getAllUsers())
                .thenReturn(userResponses);

        mvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userResponse.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userResponse.getEmail()), String.class));
    }
}