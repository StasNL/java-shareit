package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.CreatingModels;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerWebTest extends CreatingModels {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;
    private ObjectMapper mapper;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setup() {
        String identificationHeader = "X-Sharer-User-Id";
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .defaultRequest(get("/bookings")
                        .header(identificationHeader, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalDateTime time = LocalDateTime.now().plusDays(2L);
        Booking booking = createDefaultBooking();
        booking.setStart(time);
        bookingRequest = BookingMapper.bookingToBookingRequest(booking);
        bookingResponse = BookingMapper.bookingToBookingResponse(booking);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createNewBooking(any(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(bookingRequest.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(formatter)));
    }

    @Test
    void changeBookingStatusTest() throws Exception {
        when(bookingService.changeBookingStatus(anyBoolean(), anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .param("approved", "true"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(bookingRequest.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(formatter)));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .content(mapper.writeValueAsString(bookingRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start").value(bookingRequest.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(formatter)));
    }

    @Test
    void getBookingByBookerTest() throws Exception {
        when(bookingService.getBookingsByBooker(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "100"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").value(bookingRequest.getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(bookingResponse.getEnd().format(formatter)));
    }

    @Test
    void getBookingByOwnerTest() throws Exception {
        when(bookingService.getBookingsByOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "100"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").value(bookingRequest.getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(bookingResponse.getEnd().format(formatter)));
    }
}