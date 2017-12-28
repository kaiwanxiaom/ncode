package com.ncode.service;

import com.ncode.dao.UserDAO;
import com.ncode.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }
}
