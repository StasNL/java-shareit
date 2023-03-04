package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.requestDto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String IDENTIFICATION_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid RequestDto request,
                                        @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long authorId) {
        return requestClient.createRequest(request, authorId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByAuthorId(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long authorId) {
        return requestClient.getAllRequestsByAuthorId(authorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") @PositiveOrZero Integer index,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Positive Integer size
    ) {
        return requestClient.getAllRequestsExceptAuthor(userId, index, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(IDENTIFICATION_HEADER) @NotNull Long userId,
                                              @PathVariable @NotNull Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}