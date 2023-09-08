package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.item.id IN ?1 " +
            "ORDER BY c.id")
    List<Comment> findAllByItemsId(List<Long> itemsId);

}