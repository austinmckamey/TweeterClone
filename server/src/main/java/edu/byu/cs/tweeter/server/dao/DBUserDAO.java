package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DBUserDAO implements UserDAO {

    public static final String IndexName = "followee_handle-follower_handle-index";

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static final DynamoDbTable<UserBean> userTable = enhancedClient.table(UserTableName, TableSchema.fromBean(UserBean.class));

    @Override
    public UserBean getUser(String key) {
        Key userKey = Key.builder()
                .partitionValue(key)
                .build();
        return userTable.getItem(userKey);
    }

    @Override
    public void putUser(UserBean user) {
        userTable.putItem(user);
    }

    @Override
    public void updateUser(UserBean user, String key) {

    }

    @Override
    public void deleteUser(String key) {
        Key userKey = Key.builder()
                .partitionValue(key)
                .build();
        userTable.deleteItem(userKey);
    }
}
