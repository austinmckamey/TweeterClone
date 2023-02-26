package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DBStoryDAO implements StoryDAO {

    private static final String StoryAttr = "sender_alias";
    private static final String TimeStampAttr = "timestamp";

    private boolean hasMorePages;

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    DynamoDbTable<StoryBean> storyTable = enhancedClient.table(StoryTableName, TableSchema.fromBean(StoryBean.class));

    @Override
    public List<StoryBean> getStory(String key, String last) {
        Key storyKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(storyKey))
                .limit(10).scanIndexForward(false);

        if(isNonEmptyString(last)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(TimeStampAttr, AttributeValue.builder().s(last).build());
            startKey.put(StoryAttr, AttributeValue.builder().s(key).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        hasMorePages = false;

        List<StoryBean> story = new ArrayList<>();
        SdkIterable<Page<StoryBean>> results2 = storyTable.query(queryRequest);
        PageIterable<StoryBean> pages = PageIterable.create(results2);
        pages.stream()
                .limit(1)
                .forEach(visitsPage -> {
                    story.addAll(visitsPage.items());
                    if (visitsPage.lastEvaluatedKey() != null) {
                        hasMorePages = true;
                    }
                });
        return story;
    }

    @Override
    public StoryBean getStatus(String key) {
        Key storyKey = Key.builder()
                .partitionValue(key)
                .build();
        return storyTable.getItem(storyKey);
    }

    @Override
    public void putStory(StoryBean story) {
        storyTable.putItem(story);
    }

    @Override
    public void updateStory(StoryBean story, String key) {

    }

    @Override
    public void deleteStory(String key) {
        Key storyKey = Key.builder()
                .partitionValue(key)
                .build();
        storyTable.deleteItem(storyKey);
    }

    public boolean hasMorePages() {
        return this.hasMorePages;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }
}
