package com.ncode.controller;

import com.ncode.async.EventModel;
import com.ncode.async.EventProducer;
import com.ncode.async.EventType;
import com.ncode.model.Comment;
import com.ncode.model.EntityType;
import com.ncode.model.HostHolder;
import com.ncode.model.Question;
import com.ncode.service.CommentService;
import com.ncode.service.QuestionService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = {"/addComment"}, method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(DiscussUtil.ANONYMOUS_USERID);
            }
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setContent(content);
            comment.setStatus(0);

            commentService.addComment(comment);

            // 评论更新通知
            Question question = questionService.getQuestionById(questionId);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT)
                    .setExte("username", hostHolder.getUser().getName())
                    .setEntityOwnerId(question.getUserId())
                    .setExte("questionTitle", question.getTitle()));

            int count = commentService.getCommentCountByEntity(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(count, questionId);

        } catch (Exception e) {
            logger.error("添加评论错误" + e.getMessage());
        }

        return "redirect:/question/" + questionId;
    }
}
