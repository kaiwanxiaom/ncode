package com.ncode.controller;

import com.ncode.model.Question;
import com.ncode.model.ViewObject;
import com.ncode.service.QuestionService;
import com.ncode.service.UserService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(DiscussUtil.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;


    @RequestMapping(value = "/user/{userId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getViewQuestions(userId, 0, 10));
        return "index";
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String home(Model model) {
        model.addAttribute("vos", getViewQuestions(0, 0, 10));
        return "index";
    }

    private List<ViewObject> getViewQuestions(int userId, int offset, int limit) {
        List<Question> questions = questionService.getLastestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questions) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUserById(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}