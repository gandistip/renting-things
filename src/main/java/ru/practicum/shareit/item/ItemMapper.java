package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
        return item;
    }
}