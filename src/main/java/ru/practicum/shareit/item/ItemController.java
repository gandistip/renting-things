package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item create(@Valid @RequestBody Item item,
                       @RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.create(item, sharerId);
    }

    @PatchMapping("/{id}")
    public Item create(@RequestBody Item item,
                       @RequestHeader("X-Sharer-User-Id") Long sharerId,
                       @PathVariable Long id) {
        return itemService.update(item, id, sharerId);
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.getAll(sharerId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(required = false, name = "text") String text) {
        return itemService.search(text);
    }
}
