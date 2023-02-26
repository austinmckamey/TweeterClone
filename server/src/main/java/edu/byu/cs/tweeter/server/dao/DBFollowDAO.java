package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class DBFollowDAO implements FollowDAO {

    public static final String IndexName = "followee_handle-follower_handle-index";

    private static final String FolloweeAttr = "followee_handle";
    private static final String FollowerAttr = "follower_handle";

    private boolean hasMorePages;

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private final DynamoDbTable<FollowBean> followTable = enhancedClient.table(FollowTableName, TableSchema.fromBean(FollowBean.class));
    private final DynamoDbIndex<FollowBean> index = enhancedClient.table(FollowTableName, TableSchema.fromBean(FollowBean.class)).index(IndexName);

    @Override
    public SdkIterable<Page<FollowBean>> getFollowersForPost(String key) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(followKey))
                .scanIndexForward(false);

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        return index.query(queryRequest);
    }

    @Override
    public List<FollowBean> getFollowers(String key, String last, int limit) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(followKey))
                .limit(limit).scanIndexForward(false);

        if(isNonEmptyString(last)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(last).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(key).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        hasMorePages = false;

        List<FollowBean> followers = new ArrayList<>();
        SdkIterable<Page<FollowBean>> results2 = index.query(queryRequest);
        PageIterable<FollowBean> pages = PageIterable.create(results2);
        pages.stream()
                .limit(1)
                .forEach(visitsPage -> {
                    followers.addAll(visitsPage.items());
                    if (visitsPage.lastEvaluatedKey() != null) {
                        hasMorePages = true;
                    }
                });
        return followers;
    }

    @Override
    public List<FollowBean> getFollowees(String key, String last, int limit) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(followKey))
                .scanIndexForward(true);

        if(isNonEmptyString(last)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(last).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(key).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        return followTable.query(request)
                .items()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public int getFollowersCount(String key) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(followKey))
                .scanIndexForward(false);

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        List<FollowBean> followers = new ArrayList<>();
        SdkIterable<Page<FollowBean>> results2 = index.query(queryRequest);
        PageIterable<FollowBean> pages = PageIterable.create(results2);
        pages.stream()
                .limit(1)
                .forEach(visitsPage -> followers.addAll(visitsPage.items()));
        return 10000;
    }

    @Override
    public int getFollowingCount(String key) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(followKey))
                .scanIndexForward(true);

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        List<FollowBean> followees = followTable.query(queryRequest)
                .items()
                .stream()
                .collect(Collectors.toList());
        return followees.size();
    }

    @Override
    public FollowBean getFollow(String key) {
        Key followKey = Key.builder()
                .partitionValue(key)
                .build();
        return followTable.getItem(followKey);
    }

    @Override
    public FollowBean getFollowSort(String key, String sort) {
        Key followKey = Key.builder()
                .partitionValue(key).sortValue(sort)
                .build();
        return followTable.getItem(followKey);
    }

    @Override
    public void putFollow(FollowBean follow) {
        followTable.putItem(follow);
    }

    @Override
    public void updateFollow(FollowBean follow, String key) {

    }

    @Override
    public void deleteFollow(String key, String sort) {
        Key followKey = Key.builder()
                .partitionValue(key).sortValue(sort)
                .build();
        followTable.deleteItem(followKey);
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }
}
