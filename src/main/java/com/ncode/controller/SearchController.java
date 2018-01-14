package com.ncode.controller;

import com.ncode.async.EventModel;
import com.ncode.async.EventProducer;
import com.ncode.async.EventType;
import com.ncode.model.*;
import com.ncode.service.*;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    QuestionService questionService;


    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count,
                    "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.getQuestionById(question.getId());
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                ViewObject vo = new ViewObject();
                int len = Math.min(q.getContent().length(), 100);
                q.setContent(q.getContent().substring(0, len));
                vo.set("question", q);
                vo.set("user", userService.getUserById(q.getUserId()));
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, q.getId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
        } catch (Exception e) {
            logger.error("搜索错误" + e.getMessage());
        }

        return "result";
    }


}
