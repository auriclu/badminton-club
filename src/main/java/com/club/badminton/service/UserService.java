package com.club.badminton.service;

import com.club.badminton.dao.UserDao;
import com.club.badminton.model.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAllActiveMembers() {
        return userDao.findAllActiveMembers();
    }

    public Optional<User> findById(int id) {
        return userDao.findById(id);
    }
}