package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

@Service
@RequiredArgsConstructor
public class Util {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;
    private final RequestRepo requestRepo;

    public Item getItemIfExist(long itemId) {
        if (!itemRepo.existsById(itemId)) {
            throw new NotExistException("Вещь - не найдена");
        }
        Item item = itemRepo.findById(itemId).orElse(null);
        return item;
    }

    public User getUserIfExist(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotExistException("Пользователь - не найден");
        }
        User user = userRepo.findById(userId).orElse(null);
        return user;
    }

    public Booking getBookingIfExist(long bookingId) {
        if (!bookingRepo.existsById(bookingId)) {
            throw new NotExistException("Бронирование - не найдено");
        }
        Booking booking = bookingRepo.findById(bookingId).orElse(null);
        return booking;
    }

    public Request getRequestIfExist(long requestId) {
        if (!requestRepo.existsById(requestId)) {
            throw new NotExistException("Запрос - не найден");
        }
        Request request = requestRepo.findById(requestId).orElse(null);
        return request;
    }

    public PageRequest getPageIfExist(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры пагинации - некорректные");
        }
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}