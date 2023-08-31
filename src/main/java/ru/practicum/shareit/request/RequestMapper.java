package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {

    public static Request toRequest(RequestDto requestDto, User user) {
        Request request = Request.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }
}
