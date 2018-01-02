package com.ncode.async;

import com.alibaba.fastjson.JSONObject;
import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(JedisUtil.getEventsQueue(), json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
