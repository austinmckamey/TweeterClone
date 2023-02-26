package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryDB;
import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private final DAOFactory factory;

    final int TenMinutes = 600000;
    final int TwoMinutes = 120000;

    public FollowService(DAOFactory factory) {
        this.factory = factory;
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
        } catch(Exception e) {
            return new FollowersCountResponse(e.getMessage());
        }
        return new FollowersCountResponse(factory.getFollowDAO().getFollowersCount(request.getUserAlias()), "followers");
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        List<User> users;
        try {
            if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            } else if (request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            List<FollowBean> followers = factory.getFollowDAO().getFollowers(request.getFolloweeAlias(), request.getLastFollowerAlias(), request.getLimit());
            users = new ArrayList<>();
            for (FollowBean curr : followers) {
                UserBean user = factory.getUserDAO().getUser(curr.getFollowee_handle());
                users.add(new User(user.getFirstName(), user.getLastName(),
                        user.getUseralias(), user.getImageURL()));
            }
        } catch(Exception e) {
            return new FollowersResponse(e.getMessage());
        }
        return new FollowersResponse(users, factory.getFollowDAO().hasMorePages());
    }

    public FollowersResponse getFollowersWithoutAuthentication(FollowersRequest request) {
        List<User> users;
        try {
            if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            } else if (request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
            }
            List<FollowBean> followers = factory.getFollowDAO().getFollowers(request.getFolloweeAlias(), request.getLastFollowerAlias(), request.getLimit());
            users = new ArrayList<>();
            for (FollowBean curr : followers) {
                UserBean user = factory.getUserDAO().getUser(curr.getFollowee_handle());
                users.add(new User(user.getFirstName(), user.getLastName(),
                        user.getUseralias(), user.getImageURL()));
            }
        } catch(Exception e) {
            return new FollowersResponse(e.getMessage());
        }
        return new FollowersResponse(users, factory.getFollowDAO().hasMorePages());
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
        } catch(Exception e) {
            return new FollowingCountResponse(e.getMessage());
        }
        return new FollowingCountResponse(factory.getFollowDAO().getFollowingCount(request.getUserAlias()), "followees");
    }

    public FollowingResponse getFollowees(FollowingRequest request) {
        List<User> users;
        try {
            if (request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if (request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            List<FollowBean> followees = factory.getFollowDAO().getFollowees(request.getFollowerAlias(), request.getLastFolloweeAlias(), request.getLimit());
            users = new ArrayList<>();
            for (FollowBean curr : followees) {
                UserBean user = factory.getUserDAO().getUser(curr.getFollower_handle());
                users.add(new User(user.getFirstName(), user.getLastName(),
                        user.getUseralias(), user.getImageURL()));
            }
        } catch(Exception e) {
            return new FollowingResponse(e.getMessage());
        }
        return new FollowingResponse(users, factory.getFollowDAO().hasMorePages());
    }

    public FollowResponse follow(FollowRequest request) {
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
            } else if (request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if (request.getFolloweeName() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a followee name");
            } else if (request.getFollowerName() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower name");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            FollowBean followBean = new FollowBean();
            followBean.setFollowee_handle(request.getFollowerAlias());
            followBean.setFollower_handle(request.getFolloweeAlias());
            followBean.setFollowee_name(request.getFollowerName());
            followBean.setFollower_name(request.getFolloweeName());
            factory.getFollowDAO().putFollow(followBean);
        } catch(Exception e) {
            return new FollowResponse(e.getMessage());
        }
        return new FollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        FollowBean follow;
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if (request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            follow = factory.getFollowDAO().getFollowSort(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch(Exception e) {
            return new IsFollowerResponse(e.getMessage());
        }
        if (follow == null) {
            return new IsFollowerResponse(false);
        }
        return new IsFollowerResponse(true);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            } else if (request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            factory.getFollowDAO().deleteFollow(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch(Exception e) {
            return new UnfollowResponse(e.getMessage());
        }
        return new UnfollowResponse();
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
}
