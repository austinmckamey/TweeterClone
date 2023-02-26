package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.beans.UserBean;

public interface UserDAO extends DAO {
    UserBean getUser(String key);
    void putUser(UserBean user);
    void updateUser(UserBean user, String key);
    void deleteUser(String key);
}
