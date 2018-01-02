package com.ncode.async;

import java.util.HashMap;
import java.util.Map;


/**
 * 异步等待处理的队列内通用时间模型
 */
public class EventModel {
    private int id;
    private EventType type;
    private int entityType;
    private int entityId;
    private int entityOwnerId;

    private Map<String, String> extes = new HashMap<>();

    public EventModel() {

    }

    public EventModel(EventType eventType) {
        this.type = eventType;
    }

    public int getId() {
        return id;
    }

    public EventModel setId(int id) {
        this.id = id;
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public String getExte(String key) {
        return extes.get(key);
    }

    public EventModel setExte(String key, String value) {
        extes.put(key, value);
        return this;
    }

    public Map<String, String> getExtes() {
        return extes;
    }

    public void setExtes(Map<String, String> extes) {
        this.extes = extes;
    }
}
