package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.CreatingModels;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerWebTest extends CreatingModels {
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController requestController;

    private MockMvc mvc;
    private ObjectMapper mapper;
    private ItemRequest itemRequest;
    private ItemRequestResponse requestResponse;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setup() {
        String identificationHeader = "X-Sharer-User-Id";
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .defaultRequest(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(identificationHeader, 1))
                .alwaysExpect(status().isOk())
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        itemRequest = createDefaultItemRequest();
        requestResponse = ItemRequestMapper.itemRequestToItemRequestResponse(itemRequest);
    }

    @Test
    void createRequestTest() throws Exception {
        when(requestService.createRequest(any(), anyLong()))
                .thenReturn(requestResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest)))

                .andExpect(jsonPath("$.id", is(requestResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.created").value(requestResponse.getCreated().format(formatter)));
    }

    @Test
    void getAllRequestsByAuthorIdTest() throws Exception {
        when(requestService.getAllRequestsByAuthorId(anyLong()))
                .thenReturn(List.of(requestResponse));

        mvc.perform(get("/requests"))

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created").value(requestResponse.getCreated().format(formatter)));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(requestService.getAllRequestsExceptAuthor(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestResponse));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "100"))

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestResponse.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created").value(requestResponse.getCreated().format(formatter)));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestResponse);

        mvc.perform(get("/requests/{requestId}", 1L))

                .andExpect(jsonPath("$.id", is(requestResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.created").value(requestResponse.getCreated().format(formatter)));
    }
}