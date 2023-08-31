package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long userId);

    List<Item> findByRequestId(long requestId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.request.id IN ?1 " +
            "ORDER BY i.id")
    List<Item> findAllByRequestIds(List<Long> requestIds);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE " +
            "upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', ?1, '%')) " +
            "AND i.available = true")
    List<Item> search(String text, Pageable page);
}