package edu.byu.cs.tweeter.server.dao.beans;

import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class UserBean {
    private String useralias;
    private String firstName;
    private String lastName;
    private String imageURL;
    private String password;

    public UserBean() {}

    public UserBean(User u) {
        // only set the properties we know
        this.setFirstName(u.getFirstName());
        this.setLastName(u.getLastName());
        this.setUseralias(u.getAlias());
        this.setImageURL(u.getImageUrl());
    }

    @DynamoDbPartitionKey
    public String getUseralias() {
        return useralias;
    }

    public void setUseralias(String useralias) {
        this.useralias = useralias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
