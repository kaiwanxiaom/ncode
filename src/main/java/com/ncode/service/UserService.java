package com.ncode.service;

import com.ncode.dao.LoginTicketDAO;
import com.ncode.dao.UserDAO;
import com.ncode.model.LoginTicket;
import com.ncode.model.User;
import com.ncode.util.DiscussUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();

        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }
        if (!DiscussUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码错误");
            return map;
        }
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "已存在用户名");
            return map;
        }
        if(!Pattern.matches(".+@.+\\..+", username) && !Pattern.matches("\\d{11}+", username)) {
            map.put("msg", "请用邮箱或手机作为用户名");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(DiscussUtil.MD5(password + user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));

        userDAO.addUser(user);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        Date date = new Date();
        date.setTime(3600*24*1000*100L+date.getTime());
        loginTicket.setExpired(date);
        loginTicketDAO.addLoginTicket(loginTicket);

        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.setStatusByTicket(1, ticket);
    }

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }
}
