package com.ncode.controller;

import com.ncode.model.HostHolder;
import com.ncode.model.Message;
import com.ncode.model.ViewObject;
import com.ncode.service.MessageService;
import com.ncode.service.UserService;
import com.ncode.util.DiscussUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @RequestMapping(value = {"/msg/detail"}, method = RequestMethod.GET)
    public String listMassage(Model model,
                              @RequestParam("conversationId") String conversationId) {
        List<Message> messages = messageService.getMessagesByConversationId(conversationId, 0, 10);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = new ViewObject();
            vo.set("message", message);
            vo.set("headUrl", userService.getUserById(message.getFromId()).getHeadUrl());
            vos.add(vo);
        }
        messageService.updateReadByMessageToId(1, conversationId, hostHolder.getUser().getId());
        model.addAttribute("messages", vos);
        return "letterDetail";
    }

    @RequestMapping(value = {"/msg/list"}, method = RequestMethod.GET)
    public String listMassage(Model model) {
        List<Message> messages = messageService.getLatestMessagesByUserId(hostHolder.getUser().getId(), 0, 10);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = new ViewObject();
            vo.set("conversation", message);
            int covUid = message.getFromId();
            if (covUid == hostHolder.getUser().getId()) {
                covUid = message.getToId();
            }
            vo.set("user", userService.getUserById(covUid));
            vo.set("unread", messageService.countUnReadMessages(message.getConversationId(), hostHolder.getUser().getId()));
            vos.add(vo);
        }

        model.addAttribute("conversations", vos);
        return "letter";
    }

    @RequestMapping(value = {"/msg/addMessage"}, method = RequestMethod.POST)
    @ResponseBody
    public String addMassage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(userService.getUserByName(toName).getId());
            message.setContent(content);
            message.setHasRead(0);
            messageService.addMessage(message);

            return DiscussUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("增加消息出错" + e.getMessage());
        }

        return DiscussUtil.getJSONString(1, "错误");
    }
}
