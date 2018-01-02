package com.ncode.async.handler;

import com.ncode.async.EventHandler;
import com.ncode.async.EventModel;
import com.ncode.async.EventType;
import com.ncode.model.Message;
import com.ncode.service.MessageService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class CommentAddHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommentAddHandler.class);

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        try {
            Message message = new Message();
            message.setToId(eventModel.getEntityOwnerId());
            message.setFromId(DiscussUtil.MASTER_USERID);
            message.setContent("你的问题" + eventModel.getExte("questionTitle") +
                    "，被用户 " + eventModel.getExte("username") + " 评论了。");
            message.setCreatedDate(new Date());
            message.setHasRead(0);
            messageService.addMessage(message);
        } catch (Exception e) {
            logger.error("评论通知消息发送失败" + e.getMessage());
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
