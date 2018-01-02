package com.ncode.async.handler;

import com.ncode.async.EventHandler;
import com.ncode.async.EventModel;
import com.ncode.async.EventType;
import com.ncode.util.MailSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSenderUtil mailSenderUtil;

    @Override
    public void doHandle(EventModel eventModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.getExte("username"));
        mailSenderUtil.sendWithHTMLTemplate(eventModel.getExte("email"), "登陆异常", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
