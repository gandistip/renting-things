package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;
    private final CommentRepo commentRepo;

    @Transactional
    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        Item item = ItemMapper.toItem(itemDto, user);
        itemRepo.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {

        User user = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        itemRepo.findById(itemId).orElseThrow(() -> new NotExistException("Вещь - не найдена"));

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
    public ItemDto findByItemId(long itemId, long userId) {

        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotExistException("Вещь - не найдена"));
        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (item.getOwner().getId() == userId) {

            Booking lastBooking = bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, Status.APPROVED, LocalDateTime.now());
            if (lastBooking == null) {
                itemDto.setLastBooking(null);
            } else {
                itemDto.setLastBooking(BookingMapper.toBookingDtoItem(lastBooking));
            }

            Booking nextBooking = bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, Status.APPROVED, LocalDateTime.now());
            if (nextBooking == null) {
                itemDto.setNextBooking(null);
            } else {
                itemDto.setNextBooking(BookingMapper.toBookingDtoItem(nextBooking));
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
    public List<ItemDto> findAllByOwnerId(long userId) {
        userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));

        List<ItemDto> itemsDto = new ArrayList<>();

        for (ItemDto itemDto : itemRepo.findByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList())) {

            Booking lastBooking = bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            if (lastBooking == null) {
                itemDto.setLastBooking(null);
            } else {
                itemDto.setLastBooking(BookingMapper.toBookingDtoItem(lastBooking));
            }

            Booking nextBooking = bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            if (nextBooking == null) {
                itemDto.setNextBooking(null);
            } else {
                itemDto.setNextBooking(BookingMapper.toBookingDtoItem(nextBooking));
            }

            itemsDto.add(itemDto);
        }

        for (ItemDto itemDto : itemsDto) {
            List<Comment> comments = commentRepo.findByItemId(itemDto.getId());
            if (comments.isEmpty()) {
                itemDto.setComments(Collections.emptyList());
            } else {
                itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
            }
        }

        return itemsDto;
    }

    @Transactional
    @Override
    public List<ItemDto> findAllByText(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepo.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
    }
}