package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepoTest {
    @Autowired
    UserRepo userRepo;
    @Autowired
    ItemRepo itemRepo;

    Item item1;
    Item item2;
    User user;

    @BeforeEach
    void beforeEach() {

        user = userRepo.save(User.builder()
                .id(1L)
                .name("userName")
                .email("u@ya.ru")
                .build());

        item1 = itemRepo.save(Item.builder()
                .name("itemName1")
                .description("item descr1")
                .available(true)
                .owner(user)
                .build());

        item2 = itemRepo.save(Item.builder()
                .name("itemName2")
                .description("item descr2")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void findAllByText() {
        List<Item> items = itemRepo.search("descr2", PageRequest.of(0, 1));
        assertEquals(1, items.size());
    }

}