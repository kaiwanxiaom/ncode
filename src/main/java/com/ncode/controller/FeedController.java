package com.ncode.controller;

import com.ncode.model.EntityType;
import com.ncode.model.Feed;
import com.ncode.model.HostHolder;
import com.ncode.service.FeedService;
import com.ncode.service.FollowService;
import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FollowService followService;

    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = "/pushfeeds", method = {RequestMethod.GET, RequestMethod.POST})
    public String pushfeeds(Model model) {
        int localId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        String key = JedisUtil.getTimeline(localId);
        List<String> feedIds = jedisAdapter.lrange(key, 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            feeds.add(feedService.getFeedById(Integer.parseInt(feedId)));
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = "/pullfeeds", method = {RequestMethod.GET, RequestMethod.POST})
    public String pullfeeds(Model model) {
        int localId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = getIdsFromSet(followService.getFollowees(EntityType.ENTITY_USER, localId, Integer.MAX_VALUE));
        List<Feed> feeds = feedService.getUserFeeds(followees, 0, Integer.MAX_VALUE);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    private List<Integer> getIdsFromSet(Set<String> set) {
        List<Integer> list = new ArrayList<>();
        for (String s : set) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }
}
