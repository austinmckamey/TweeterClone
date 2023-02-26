package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

public class StatusService {

    private final DAOFactory factory;

    final int TenMinutes = 600000;
    final int TwoMinutes = 120000;

    public StatusService(DAOFactory factory) {
        this.factory = factory;
    }

    public FeedResponse getFeed(FeedRequest request) {
        List<Status> feed;
        boolean hasMorePages;
        try {
            if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            } else if (request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            List<FeedBean> statuses = factory.getFeedDAO().getFeed(request.getUserAlias(), request.getLastStatus());
            feed = new ArrayList<>();
            for (FeedBean status : statuses) {
                UserBean user = factory.getUserDAO().getUser(status.getSender_alias());
                feed.add(new Status(status.getPost_body(), new User(user.getFirstName(), user.getLastName(),
                        user.getUseralias(), user.getImageURL()), status.getTimestamp(), status.getUrls(),
                        status.getMentions()));
            }
            hasMorePages = factory.getFeedDAO().hasMorePages();
        } catch(Exception e) {
            return new FeedResponse(e.getMessage());
        }
        return new FeedResponse(feed, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            if (request.getStatus() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a status");
            } else if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            // Add to user's story
            StoryBean story = new StoryBean();
            story.setSender_alias(request.getStatus().getUser().getAlias());
            story.setTimestamp(request.getStatus().getDate());
            story.setPost_body(request.getStatus().getPost());
            story.setUrls(request.getStatus().getUrls());
            story.setMentions(request.getStatus().getMentions());
            factory.getStoryDAO().putStory(story);

            /*// Add to user's followers' feeds
            FeedBean feed = new FeedBean();
            feed.setSender_alias(request.getStatus().getUser().getAlias());
            feed.setTimestamp(request.getStatus().getDate());
            feed.setPost_body(request.getStatus().getPost());
            feed.setUrls(request.getStatus().getUrls());
            feed.setMentions(request.getStatus().getMentions());

            SdkIterable<Page<FollowBean>> results = factory.getFollowDAO().getFollowersForPost(request.getStatus().getUser().getAlias());
            results.forEach(page -> {
                List<FollowBean> followers = page.items();
                for (FollowBean follower : followers) {
                    feed.setReceiver_alias(follower.getFollowee_handle());
                    factory.getFeedDAO().putFeed(feed);
                }
            });*/
        } catch(Exception e) {
            return new PostStatusResponse(e.getMessage());
        }
        return new PostStatusResponse();
    }

    public StoryResponse getStory(StoryRequest request) {
        List<Status> story;
        boolean hasMorePages;
        try {
            if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            } else if (request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            List<StoryBean> statuses = factory.getStoryDAO().getStory(request.getUserAlias(), request.getLastStatus());
            story = new ArrayList<>();
            for (StoryBean status : statuses) {
                UserBean user = factory.getUserDAO().getUser(status.getSender_alias());
                story.add(new Status(status.getPost_body(), new User(user.getFirstName(), user.getLastName(),
                        user.getUseralias(), user.getImageURL()), status.getTimestamp(), status.getUrls(),
                        status.getMentions()));
            }
            hasMorePages = factory.getStoryDAO().hasMorePages();
        } catch(Exception e) {
            return new StoryResponse(e.getMessage());
        }
        return new StoryResponse(story, hasMorePages);
    }

    private long getDateTime() {
        Date date = new Date();
        return date.getTime();
    }

    private boolean checkAndUpdateAuthtoken(String token) {
        AuthtokenBean authtoken = factory.getAuthtokenDAO().getAuthtoken(token);
        if (authtoken == null) {
            return false;
        } else if (getDateTime() - authtoken.getDateTime() > TwoMinutes) {
            return false;
        }
        authtoken.setDateTime(getDateTime());
        factory.getAuthtokenDAO().putAuthtoken(authtoken);
        return true;
    }

    public void batchWriteFeeds(List<String> followers, String sender, String timestamp, String post_body, List<String> urls, List<String> mentions) {
        factory.getFeedDAO().addFeedBatch(followers, sender, timestamp, post_body, urls, mentions);
    }
}
