package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.server.SQS.FeedUpdater;
import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
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

public class DBFeedDAO implements FeedDAO {

    private static final String FeedAttr = "receiver_alias";
    private static final String TimeStampAttr = "timestamp";

    private boolean hasMorePages;

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private final DynamoDbTable<FeedBean> feedTable = enhancedClient.table(FeedTableName, TableSchema.fromBean(FeedBean.class));

    @Override
    public List<FeedBean> getFeed(String key, String last) {
        Key feedKey = Key.builder()
                .partitionValue(key)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(feedKey))
                .limit(10).scanIndexForward(false);

        if(isNonEmptyString(last)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(TimeStampAttr, AttributeValue.builder().s(last).build());
            startKey.put(FeedAttr, AttributeValue.builder().s(key).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        hasMorePages = false;

        List<FeedBean> feed = new ArrayList<>();
        SdkIterable<Page<FeedBean>> results2 = feedTable.query(queryRequest);
        PageIterable<FeedBean> pages = PageIterable.create(results2);
        pages.stream()
                .limit(1)
                .forEach(visitsPage -> {
                    feed.addAll(visitsPage.items());
                    if (visitsPage.lastEvaluatedKey() != null) {
                        hasMorePages = true;
                    }
                });
        return feed;
    }

    @Override
    public FeedBean getStatus(String key) {
        Key feedKey = Key.builder()
                .partitionValue(key)
                .build();
        return feedTable.getItem(feedKey);
    }

    @Override
    public void putFeed(FeedBean feed) {
        feedTable.putItem(feed);
    }

    @Override
    public void updateFeed(FeedBean feed, String key) {

    }

    @Override
    public void deleteFeed(String key) {
        Key feedKey = Key.builder()
                .partitionValue(key)
                .build();
        feedTable.deleteItem(feedKey);
    }

    public boolean hasMorePages() {
        return this.hasMorePages;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public void addFeedBatch(List<String> followers, String sender, String timestamp, String post_body, List<String> urls, List<String> mentions) {
        List<FeedBean> batchToWrite = new ArrayList<>();
        for (String u : followers) {
            FeedBean dto = new FeedBean(u, sender, timestamp, post_body, urls, mentions);
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFeedDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFeedDTOs(batchToWrite);
        }
    }
    private void writeChunkOfFeedDTOs(List<FeedBean> feedDTOs) {
        if(feedDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<FeedBean> table = enhancedClient.table(FeedTableName, TableSchema.fromBean(FeedBean.class));
        WriteBatch.Builder<FeedBean> writeBuilder = WriteBatch.builder(FeedBean.class).mappedTableResource(table);
        for (FeedBean item : feedDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFeedDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
