package com.ncode.service;

import com.ncode.dao.MessageDAO;
import com.ncode.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int countUnReadMessages(String conversationId, int userId) {
        return messageDAO.selectCountUnreadByConversationIdUserId(conversationId, userId);
    }

    public List<Message> getMessagesByConversationId(String conversationId, int offset, int limit) {
        return messageDAO.selectMessageByConversationId(conversationId, offset, limit);
    }

    public List<Message> getLatestMessagesByUserId(int userId, int offset, int limit) {
        return messageDAO.selectMessageByUserId(userId, offset, limit);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));

        return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
    }

    public int updateReadByMessageToId(int status, String conversationId, int id) {
        return messageDAO.updateHasReadByToId(status, conversationId, id);
    }
}
