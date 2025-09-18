package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail())) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }

        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        return existingUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
