package ru.sberbank.services;

import ru.sberbank.model.User;

import java.util.List;

/**
 * Created by salexandrov on 09.05.2016.
 */
public interface UserService {
    Iterable<User> findUsersByExample (User user);

    void addUser (User user);
    void deleteUser (Long userId);
    User getUser(Long id);
    User getUser(String username);
    Long countAdminUsers();
    Iterable<User> getAllUser();

}
