package com.ncode.async;

import com.alibaba.fastjson.JSONObject;
import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventCustomer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventCustomer.class);

    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType eventType : eventTypes) {
                    if (!config.containsKey(eventType)) {
                        config.put(eventType, new ArrayList<>());
                    }
                    config.get(eventType).add(entry.getValue());
                }


            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    String key = JedisUtil.getEventsQueue();
                    List<String> event = jedisAdapter.brpop(0, key);

                    for(String e : event) {
                        if (e.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSONObject.parseObject(e, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件类型");
                            continue;
                        }
                        for (EventHandler eventHandler : config.get(eventModel.getType())) {
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();

    }
}
