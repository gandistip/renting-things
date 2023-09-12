package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepo extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(long userId);

    List<Request> findAllByRequesterIdNot(long userId, Pageable page);
}
