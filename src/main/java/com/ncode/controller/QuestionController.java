package com.ncode.controller;

import com.ncode.model.HostHolder;
import com.ncode.model.Question;
import com.ncode.service.QuestionService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(DiscussUtil.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = "/question/{qid}", method = RequestMethod.GET)
    public String addQuestion(Model model, @PathVariable("qid") int qid) {
        Question question = questionService.getQuestionById(qid);
        model.addAttribute("question", question);
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
            logger.error("添加用户失败" + e.getMessage());
        }
        return DiscussUtil.getJSONString(1, "错误");
    }
}
