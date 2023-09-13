package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final BookingRepo bookingRepo;
    private final CommentRepo commentRepo;
    private final Util util;

    @Transactional
    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        User user = util.getUserIfExist(userId);
        Item item = ItemMapper.toItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            item.setRequest(util.getRequestIfExist(itemDto.getRequestId()));
        }

        item = itemRepo.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {

        User user = util.getUserIfExist(userId);
        Item item = util.getItemIfExist(itemId);

        if (bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now()) == null) {
            throw new ValidationException("Пользователь с " + userId + " не бронировал вещь с " + itemId);
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user, LocalDateTime.now());

        commentRepo.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {

        User user = util.getUserIfExist(userId);
        util.getItemIfExist(itemId);

        Item item = ItemMapper.toItem(itemDto, user);
        item.setId(itemId);

        if (!itemRepo.findByOwnerId(userId).contains(item)) {
            throw new NotExistException(Item.class, "Вещь не принадлежит пользователю с id=" + userId);
        }

        Item updateItem = itemRepo.findById(itemId).get();

        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }

        itemRepo.save(updateItem);
        return ItemMapper.toItemDto(updateItem);
    }

    @Transactional
    @Override
    public List<ItemDto> findAllByText(String text, int from, int size) {
        //PageRequest page = util.getPageIfExist(from, size);
        PageRequest page = PageRequest.of(from / size, size);

        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepo.search(text, page).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public ItemDto findByItemId(long itemId, long userId) {

        Item item = util.getItemIfExist(itemId);
        util.getUserIfExist(userId);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (item.getOwner().getId() == userId) {

            List<Booking> lastBooking = bookingRepo.findLastBooking(List.of(itemId), Status.APPROVED, PageRequest.of(0, 1));
            if (lastBooking.isEmpty()) {
                itemDto.setLastBooking(null);
            } else {
                itemDto.setLastBooking(ItemDto.BookingDtoForItemDto.builder().id(lastBooking.get(0).getId())
                        .bookerId(lastBooking.get(0).getBooker().getId()).build());
            }

            List<Booking> nextBooking = bookingRepo.findNextBooking(List.of(itemId), Status.APPROVED, PageRequest.of(0, 1));
            if (nextBooking.isEmpty()) {
                itemDto.setNextBooking(null);
            } else {
                itemDto.setNextBooking(ItemDto.BookingDtoForItemDto.builder().id(nextBooking.get(0).getId())
                        .bookerId(nextBooking.get(0).getBooker().getId()).build());
            }
        }

        List<Comment> comments = commentRepo.findByItemId(itemId);
        if (comments.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }

        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> findAllByOwnerId(long userId, int from, int size) {

        //PageRequest page = util.getPageIfExist(from, size);
        PageRequest page = PageRequest.of(from / size, size);

        util.getUserIfExist(userId);

        List<Item> items = itemRepo.findByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<ItemDto> itemsDtoOut = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        List<Booking> lastBookings = bookingRepo.findLastBooking(itemsId, Status.APPROVED, PageRequest.of(0, 99));
        List<Booking> nextBookings = bookingRepo.findNextBooking(itemsId, Status.APPROVED, PageRequest.of(0, 99));
        List<Comment> comments = commentRepo.findAllByItemsId(itemsId);

        Map<Long, ItemDto> itemsDtoOutMap = new HashMap<>();
        Map<Long, ItemDto> result = new HashMap<>();

        for (ItemDto itemDto : itemsDtoOut) {
            itemsDtoOutMap.put(itemDto.getId(), itemDto);
        }

        for (Long itemId : itemsId) {

            ItemDto itemDto = itemsDtoOutMap.get(itemId);

            if (lastBookings.isEmpty()) {
                itemDto.setLastBooking(null);
            } else if (lastBookings.stream().noneMatch(b -> b.getItem().getId().equals(itemId))) {
                itemDto.setLastBooking(null);
            } else {
                Booking lastBooking = lastBookings.stream().filter(b -> b.getItem().getId().equals(itemId))
                        .collect(Collectors.toList()).get(0);
                itemDto.setLastBooking(ItemDto.BookingDtoForItemDto.builder().id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId()).build());
            }

            if (nextBookings.isEmpty()) {
                itemDto.setNextBooking(null);
            } else if (nextBookings.stream().noneMatch(b -> b.getItem().getId().equals(itemId))) {
                itemDto.setNextBooking(null);
            } else {
                Booking nextBooking = nextBookings.stream().filter(b -> b.getItem().getId().equals(itemId))
                        .collect(Collectors.toList()).get(0);
                itemDto.setNextBooking(ItemDto.BookingDtoForItemDto.builder().id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId()).build());
            }

            if (comments.isEmpty()) {
                itemDto.setComments(Collections.emptyList());
            } else if (comments.stream().noneMatch(c -> c.getItem().getId().equals(itemId))) {
                itemDto.setComments(Collections.emptyList());
            } else {
                List<Comment> commentsItem = comments.stream().filter(b -> b.getItem().getId().equals(itemId))
                        .collect(Collectors.toList());
                itemDto.setComments(commentsItem.stream().map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
            }

            result.put(itemId, itemDto);
        }

        return result.values().stream().skip(page.getPageNumber()).limit(page.getPageSize())
                .collect(Collectors.toList());
    }
}