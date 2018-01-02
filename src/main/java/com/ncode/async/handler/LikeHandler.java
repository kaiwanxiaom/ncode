package com.ncode.async.handler;

import com.ncode.async.EventHandler;
import com.ncode.async.EventModel;
import com.ncode.async.EventType;
import com.ncode.model.Message;
import com.ncode.model.User;
import com.ncode.service.MessageService;
import com.ncode.service.UserService;
import com.ncode.util.DiscussUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    private List<EventType> eventTypes = new ArrayList<>();

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    public LikeHandler() {
        eventTypes.add(EventType.LIKE);
    }

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setFromId(DiscussUtil.MASTER_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setHasRead(0);
        User user = userService.getUserById(eventModel.getId());
        message.setContent("你的评论受到了用户" + user.getName() +
                "的赞！地址：http://localhost:8080/question/" + eventModel.getExte("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return eventTypes;
    }
}
