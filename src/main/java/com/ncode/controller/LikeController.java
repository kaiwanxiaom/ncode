package com.ncode.controller;

import com.ncode.async.EventModel;
import com.ncode.async.EventProducer;
import com.ncode.async.EventType;
import com.ncode.model.Comment;
import com.ncode.model.EntityType;
import com.ncode.model.HostHolder;
import com.ncode.service.CommentService;
import com.ncode.service.LikeService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String likeComment(@RequestParam("commentId") int commentId) {
        try {
            if (hostHolder.getUser() == null) {
                return DiscussUtil.getJSONString(999);
            }
            long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);

            // 异步发送站内信
            Comment comment = commentService.getCommentById(commentId);
            if (hostHolder.getUser().getId() != comment.getUserId()) {
                eventProducer.fireEvent(new EventModel(EventType.LIKE).setEntityId(commentId)
                        .setEntityOwnerId(comment.getUserId())
                        .setEntityType(EntityType.ENTITY_COMMENT)
                        .setId(hostHolder.getUser().getId())
                        .setType(EventType.LIKE).setExte("questionId", String.valueOf(comment.getEntityId())));
            }

            return DiscussUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("喜欢评论失败" + e.getMessage());
        }
        return DiscussUtil.getJSONString(1, "错误");
    }

    @RequestMapping(value = "/dislike", method = RequestMethod.POST)
    @ResponseBody
    public String disLikeComment(@RequestParam("commentId") int commentId) {
        try {
            if (hostHolder.getUser() == null) {
                return DiscussUtil.getJSONString(999);
            }
            long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
            return DiscussUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            logger.error("不喜欢评论失败" + e.getMessage());
        }
        return DiscussUtil.getJSONString(1, "错误");
    }
}
