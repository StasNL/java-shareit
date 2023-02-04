package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.CreatingModels;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerWebTest extends CreatingModels {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;
    private ObjectMapper mapper;

    private ItemResponse itemResponse;
    private ItemDtoForCreate itemToCreate;
    private Comment comment;
    private CommentResponse commentResponse;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setup() {
        String identificationHeader = "X-Sharer-User-Id";
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .defaultRequest(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(identificationHeader, 1))
                .alwaysExpect(status().isOk())
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Item item = createDefaultItem();
        itemToCreate = ItemMapper.itemToNewItemDto(item);
        itemResponse = ItemMapper.itemToItemResponse(item);
        comment = createDefaultComment();
        commentResponse = ItemMapper.commentToCommentResponse(comment);
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemToCreate)))
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable()), Boolean.class));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(Item.class), anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemToCreate)))

                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable()), Boolean.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(get("/items/{itemId}", 1L))

                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable()), Boolean.class));
    }

    @Test
    void getAllItemsByIdTest() throws Exception {
        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "100"))

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponse.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponse.getAvailable()), Boolean.class));
    }

    @Test
    void searchByNameTest() throws Exception {
        when(itemService.searchByName(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(get("/items/search")
                        .param("text", "Вещь")
                        .param("from", "0")
                        .param("size", "100"))

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponse.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponse.getAvailable()), Boolean.class));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment)))

                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created").value(commentResponse.getCreated().format(formatter)));
    }
}