package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    @Query( "SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.status = ?2 AND b.start > CURRENT_TIME " +
            "ORDER BY b.start" )
    List<Booking> findNextBooking(List<Long> itemsId, Status status, Pageable page);

    @Query( "SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 AND b.status = ?2 AND b.start < CURRENT_TIME " +
            "ORDER BY b.start DESC" )
    List<Booking> findLastBooking(List<Long> itemsId, Status status, Pageable page);

    @Query( "SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND CURRENT_TIME BETWEEN b.start AND b.end " +
            "ORDER BY b.start" )
    Page<Booking> findAllCurrentByItemBookerId(long bookerId, Pageable page);

    @Query( "SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND CURRENT_TIME BETWEEN b.start AND b.end " +
            "ORDER BY b.start" )
    Page<Booking> findAllCurrentByItemOwnerId(long ownerId, Pageable page);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(long itemId, long bookerId, Status status, LocalDateTime dateTime);

    Page<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status, Pageable page);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime dateTime, Pageable page);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status status, Pageable page);

}