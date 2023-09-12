package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Util;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepo requestRepo;
    private final ItemRepo itemRepo;
    private final Util util;

    @Transactional
    public RequestDto save(RequestDto requestDto, long userId) {
        User user = util.getUserIfExist(userId);
        Request request = RequestMapper.toRequest(requestDto, user);
        request = requestRepo.save(request);
        return RequestMapper.toRequestDto(request);
    }

    @Transactional
    public RequestDto findByRequestId(long userId, long requestId) {
        util.getUserIfExist(userId);
        Request request = util.getRequestIfExist(requestId);

        RequestDto requestDto = RequestMapper.toRequestDto(request);
        requestDto.setItems(itemRepo.findByRequestId(requestDto.getId()).stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return requestDto;
    }

    @Transactional
    public List<RequestDto> findAllByRequesterId(long userId) {
        util.getUserIfExist(userId);

        List<Request> requests = requestRepo.findByRequesterId(userId);
        List<RequestDto> requestDtos = requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());

        List<Item> items = itemRepo.findAllByRequestIds(requests.stream().map(Request::getId).collect(Collectors.toList()));
        List<ItemDto> itemsDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        for (RequestDto requestDto : requestDtos) {
            requestDto.setItems(itemsDtos.stream().filter(i -> i.getRequestId().equals(requestDto.getId())).collect(Collectors.toList()));
        }

        return requestDtos;
    }

    @Transactional
    public List<RequestDto> findAllAlien(long userId, int from, int size) {
        PageRequest page = util.getPageIfExist(from, size);
        util.getUserIfExist(userId);

        List<Request> requests = requestRepo.findAllByRequesterIdNot(userId, page);
        List<RequestDto> requestDtos = requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());

        List<Item> items = itemRepo.findAllByRequestIds(requests.stream().map(Request::getId).collect(Collectors.toList()));
        List<ItemDto> itemsDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        for (RequestDto requestDto : requestDtos) {
            requestDto.setItems(itemsDtos.stream().filter(i -> i.getRequestId().equals(requestDto.getId())).collect(Collectors.toList()));
        }

        return requestDtos;
    }
}
