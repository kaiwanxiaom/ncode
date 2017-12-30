package com.ncode.controller;

import com.ncode.model.*;
import com.ncode.service.CommentService;
import com.ncode.service.QuestionService;
import com.ncode.service.UserService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/question/{qid}", method = RequestMethod.GET)
    public String detailQuestion(Model model, @PathVariable("qid") int qid) {
        try {
            Question question = questionService.getQuestionById(qid);
            model.addAttribute("question", question);

            List<Comment> comments =
                    commentService.selectCommentByEntity(qid, EntityType.ENTITY_QUESTION , 0, 10);
            List<ViewObject> vos = new ArrayList<>();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                User user = userService.getUserById(comment.getUserId());
                vo.set("user", user);
                vos.add(vo);
            }
            model.addAttribute("comments", vos);
        }catch (Exception e) {
            logger.error("显示问题出错" + e.getMessage());
            return "redirect:/";
        }
        return "detail";
    }

    @RequestMapping(value = "/question/add", method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() != null) {
                question.setUserId(hostHolder.getUser().getId());
            } else {
                // question.setUserId(DiscussUtil.ANONYMOUS_USERID);
                return DiscussUtil.getJSONString(999);
            }
            if (questionService.addQuestion(question) > 0) {
                return DiscussUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("添加问题失败" + e.getMessage());
        }
        return DiscussUtil.getJSONString(1, "错误");
    }
}
