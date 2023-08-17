package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final BookingRepo bookingRepo;

    @Transactional
    public CommentDto save(long userId, long itemId, CommentDto commentDto) {

        User user = userRepo.findById(userId).orElseThrow(() -> new NotExistException("Пользователь - не найден"));
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotExistException("Вещь - не найдена"));

        if (bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now()) == null) {
            throw new ValidationException("Пользователь с " + userId + " не бронировал вещь с " + itemId);
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user, LocalDateTime.now());

        commentRepo.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}
