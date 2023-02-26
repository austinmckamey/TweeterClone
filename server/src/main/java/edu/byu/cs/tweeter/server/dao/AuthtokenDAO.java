package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;

public interface AuthtokenDAO extends DAO {
    AuthtokenBean getAuthtoken(String key);
    void putAuthtoken(AuthtokenBean authtoken);
    void updateAuthtoken(AuthtokenBean authtoken, String key);
    void deleteAuthtoken(String key);
}
