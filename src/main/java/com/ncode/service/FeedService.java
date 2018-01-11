package com.ncode.service;

import com.ncode.dao.FeedDAO;
import com.ncode.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    public boolean addFeed(Feed feed) {
        return feedDAO.insertFeed(feed) > 0;
    }

    public List<Feed> getUserFeeds(List<Integer> followees, int offset, int limit) {
        return feedDAO.selectUsersFeed(offset, followees, limit);
    }

    public Feed getFeedById(int id) {
        return feedDAO.selectById(id);
    }
}
