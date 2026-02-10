package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    User createUser(User user);
    void deleteUser(UUID id);
    Page<User> getAllUsers(Pageable pageable);
    User getUserById(UUID id);
    User updateUser(UUID id, User user);
}
