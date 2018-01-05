package com.ncode.controller;

import com.ncode.async.EventModel;
import com.ncode.async.EventProducer;
import com.ncode.async.EventType;
import com.ncode.model.*;
import com.ncode.service.CommentService;
import com.ncode.service.FollowService;
import com.ncode.service.QuestionService;
import com.ncode.service.UserService;
import com.ncode.util.DiscussUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {
    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/followUser", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId ) {
        if (hostHolder.getUser() == null) {
            return DiscussUtil.getJSONString(999);
        }

        boolean ret = followService.followEntity(EntityType.ENTITY_USER, userId, hostHolder.getUser().getId());

        if (ret) {
            eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                    .setExte("username", hostHolder.getUser().getName())
                    .setEntityType(EntityType.ENTITY_USER)
                    .setEntityOwnerId(userId));
        }

        Long count = followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId());
        return DiscussUtil.getJSONString(ret ? 0:1, String.valueOf(count));
    }

    @RequestMapping(value = "/unfollowUser", method = {RequestMethod.POST})
    @ResponseBody
    public String unFollowUser(@RequestParam("userId") int userId ) {
        if (hostHolder.getUser() == null) {
            return DiscussUtil.getJSONString(999);
        }

        boolean ret = followService.unFollowEntity(EntityType.ENTITY_USER, userId, hostHolder.getUser().getId());
        Long count = followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId());
        return DiscussUtil.getJSONString(ret ? 0:1, String.valueOf(count));
    }

    @RequestMapping(value = "/followQuestion", method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId ) {
        if (hostHolder.getUser() == null) {
            return DiscussUtil.getJSONString(999);
        }

        boolean ret = followService.followEntity(EntityType.ENTITY_QUESTION, questionId, hostHolder.getUser().getId());

        Question question = questionService.getQuestionById(questionId);
        if (ret) {
            eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                    .setExte("username", hostHolder.getUser().getName())
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setEntityOwnerId(question.getUserId())
                    .setExte("questionTitle", question.getTitle())
                    .setEntityId(questionId));
        }

        Map<String, Object> info = new HashMap<>();
        info.put("name", hostHolder.getUser().getName());
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return DiscussUtil.getJSONString(ret ? 0:1, info);
    }

    @RequestMapping(value = "/unfollowQuestion", method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId ) {
        if (hostHolder.getUser() == null) {
            return DiscussUtil.getJSONString(999);
        }

        boolean ret = followService.unFollowEntity(EntityType.ENTITY_QUESTION, questionId, hostHolder.getUser().getId());
        Map<String, Object> info = new HashMap<>();
        info.put("name", hostHolder.getUser().getName());
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return DiscussUtil.getJSONString(ret ? 0:1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        Set<String> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId,  10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUserById(userId));
        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        Set<String> followeeIds = followService.getFollowees(EntityType.ENTITY_USER, userId, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount( EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUserById(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, Set<String> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (String suid : userIds) {
            int uid = Integer.parseInt(suid);
            User user = userService.getUserById(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getCommentCountByUserId(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollowed(EntityType.ENTITY_USER, uid, localUserId));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
