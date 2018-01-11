package com.ncode.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.ncode.async.EventHandler;
import com.ncode.async.EventModel;
import com.ncode.async.EventType;
import com.ncode.model.EntityType;
import com.ncode.model.Feed;
import com.ncode.model.Question;
import com.ncode.model.User;
import com.ncode.service.FeedService;
import com.ncode.service.FollowService;
import com.ncode.service.QuestionService;
import com.ncode.service.UserService;
import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import com.sun.mail.util.QEncoderStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(FeedHandler.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FollowService followService;

    private String buildFeedContent(EventModel eventModel) {
        Map<String, String> map = new HashMap<>();

        User user = userService.getUserById(eventModel.getId());
        map.put("userName", user.getName());
        map.put("userId", String.valueOf(user.getId()));
        map.put("userHead", user.getHeadUrl());

        Question question = questionService.getQuestionById(eventModel.getEntityId());
        map.put("questionTitle", question.getTitle());
        map.put("questionId", String.valueOf(question.getId()));

        return JSONObject.toJSONString(map);
    }

    @Override
    public void doHandle(EventModel eventModel) {
        try {
            Feed feed = new Feed();
            feed.setType(eventModel.getType().getValue());
            feed.setUserId(eventModel.getId());
            feed.setContent(buildFeedContent(eventModel));
            feed.setCreatedDate(new Date());

            feedService.addFeed(feed);

            // 推模式
            Set<String> followers = followService.getFollowers(
                    EntityType.ENTITY_USER, eventModel.getId(), Integer.MAX_VALUE);
            for(String follower : followers) {
                String key = JedisUtil.getTimeline(Integer.parseInt(follower));
                jedisAdapter.lpush(key, String.valueOf(feed.getId()));
            }

        } catch (Exception e) {
            logger.error("添加动态失败" + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW, EventType.COMMENT);
    }
}
