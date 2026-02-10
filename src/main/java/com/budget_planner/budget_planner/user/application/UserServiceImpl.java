package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.domain.User;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;

    public UserServiceImpl (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw userNotFound();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(this::userNotFound);
    }

    @Override
    public User updateUser(UUID id, User user) {
        var userToBeUpdated = userRepository.findById(id)
                .orElseThrow(this::userNotFound);

        if (user.getName() != null && !user.getName().isBlank())
            userToBeUpdated.setName(user.getName());

        if (user.getEmail() != null && !user.getEmail().isBlank())
            userToBeUpdated.setEmail(user.getEmail());

        return userToBeUpdated;
    }

    private EntityNotFoundException userNotFound () {
        return new EntityNotFoundException(USER_NOT_FOUND);
    }
}
