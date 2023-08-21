package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(
            long bookerId);

    @Query(
            "select b " +
                    "from Booking b " +
                    "where b.booker.id = ?1 and CURRENT_TIME between b.start and b.end " +
                    "order by b.start"
    )
    List<Booking> findAllCurrentByItemBookerId(long bookerId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            long bookerId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(
            long ownerId);

    @Query(
            "select b " +
                    "from Booking b " +
                    "where b.item.owner.id = ?1 and CURRENT_TIME between b.start and b.end " +
                    "order by b.start"
    )
    List<Booking> findAllCurrentByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            long ownerId, Status status);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            long itemId, Status status, LocalDateTime dateTime);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            long itemId, Status status, LocalDateTime dateTime);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
            long itemId, long bookerId, Status status, LocalDateTime dateTime);
}