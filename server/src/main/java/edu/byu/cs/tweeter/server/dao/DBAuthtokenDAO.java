package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DBAuthtokenDAO implements AuthtokenDAO {

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static final DynamoDbTable<AuthtokenBean> authTable = enhancedClient.table(AuthTableName, TableSchema.fromBean(AuthtokenBean.class));

    @Override
    public AuthtokenBean getAuthtoken(String key) {
        Key authKey = Key.builder()
                .partitionValue(key)
                .build();
        return authTable.getItem(authKey);
    }

    @Override
    public void putAuthtoken(AuthtokenBean authtoken) {
        authTable.putItem(authtoken);
    }

    @Override
    public void updateAuthtoken(AuthtokenBean authtoken, String key) {

    }

    @Override
    public void deleteAuthtoken(String key) {
        Key authKey = Key.builder()
                .partitionValue(key)
                .build();
        authTable.deleteItem(authKey);
    }
}
