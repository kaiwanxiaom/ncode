package com.ncode.async.handler;

import com.ncode.async.EventHandler;
import com.ncode.async.EventModel;
import com.ncode.async.EventType;
import com.ncode.model.Question;
import com.ncode.service.QuestionService;
import com.ncode.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;

    @Autowired
    QuestionService questionService;

    @Override
    public void doHandle(EventModel eventModel) {

        try {
            boolean ans = searchService.indexQuestion(eventModel.getEntityId(),
                    eventModel.getExte("title"), eventModel.getExte("content"));
        } catch (Exception e) {
            logger.error("AddQuestionHandler error" + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
