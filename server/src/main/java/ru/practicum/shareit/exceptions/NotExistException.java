package ru.practicum.shareit.exceptions;

public class NotExistException extends RuntimeException {

    public NotExistException(Class<?> entityClass, String s) {
        super("Сущность " + entityClass.getSimpleName() + " не найдена. " + s);
    }

    public NotExistException(String s) {
        super(s);
    }
}