package com.ncode.controller;

import com.ncode.model.*;
import com.ncode.service.*;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @RequestMapping(value = "/question/{qid}", method = RequestMethod.GET)
    public String detailQuestion(Model model, @PathVariable("qid") int qid) {
        try {
            Question question = questionService.getQuestionById(qid);
            model.addAttribute("question", question);
            if (hostHolder.getUser() != null) {
                model.addAttribute("followed",
                        followService.isFollowed(EntityType.ENTITY_QUESTION, qid, hostHolder.getUser().getId()));
            }

            List<ViewObject> vos = new ArrayList<>();
            Set<String> userIds = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 10);
            for (String userId : userIds) {
                ViewObject vo = new ViewObject();
                User user = userService.getUserById(Integer.parseInt(userId));
                vo.set("name", user.getName());
                vo.set("headUrl", user.getHeadUrl());
                vo.set("id", user.getId());
                vos.add(vo);
            }

            model.addAttribute("followUsers", vos);

            List<Comment> comments =
                    commentService.selectCommentByEntity(qid, EntityType.ENTITY_QUESTION , 0, 10);
            vos = new ArrayList<>();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                User user = userService.getUserById(comment.getUserId());
                vo.set("user", user);
                vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
                if (hostHolder.getUser() != null) {
                    vo.set("liked", likeService.isLiked(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
                    vo.set("disLiked", likeService.isDisLiked(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
                } else {
                    vo.set("liked", false);
                    vo.set("disLiked", false);
                }

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
