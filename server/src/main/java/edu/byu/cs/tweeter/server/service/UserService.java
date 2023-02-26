package edu.byu.cs.tweeter.server.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.beans.AuthtokenBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;

public class UserService {

    private final DAOFactory factory;

    final int TenMinutes = 600000;
    final int TwoMinutes = 120000;

    public UserService(DAOFactory factory) {
        this.factory = factory;
    }

    public LoginResponse login(LoginRequest request) {
        User user1;
        AuthToken token;
        try {
            if (request.getUsername() == null) {
                throw new RuntimeException("[Bad Request] Missing a username");
            } else if (request.getPassword() == null) {
                throw new RuntimeException("[Bad Request] Missing a password");
            }
            UserBean user = factory.getUserDAO().getUser(request.getUsername());
            if (user == null) {
                throw new RuntimeException("[Bad Request] Username not found");
            }
            if (!user.getPassword().equals(getSecurePassword(request.getPassword(), getSalt()))) {
                throw new RuntimeException("[Bad Request] Username and password do not match");
            }
            user1 = new User(user.getFirstName(), user.getLastName(), user.getUseralias(), user.getImageURL());

            token = new AuthToken();
            AuthtokenBean tokenBean = new AuthtokenBean();
            tokenBean.setAuthtoken(token.getToken());
            tokenBean.setDateTime(token.getDatetime());
            factory.getAuthtokenDAO().putAuthtoken(tokenBean);
        } catch(Exception e) {
            return new LoginResponse(e.getMessage());
        }
        return new LoginResponse(user1, token);
    }

    public LogoutResponse logout(LogoutRequest request) {
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            }
            factory.getAuthtokenDAO().deleteAuthtoken(request.getAuthToken().getToken());
        } catch(Exception e) {
            return new LogoutResponse(e.getMessage());
        }
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        AuthToken token;
        String url;
        try {
            if (request.getUsername() == null) {
                throw new RuntimeException("[Bad Request] Missing a username");
            } else if (request.getPassword() == null) {
                throw new RuntimeException("[Bad Request] Missing a password");
            } else if (request.getFirstName() == null) {
                throw new RuntimeException("[Bad Request] Missing a first name");
            } else if (request.getLastName() == null) {
                throw new RuntimeException("[Bad Request] Missing a last name");
            } else if (request.getImage() == null) {
                throw new RuntimeException("[Bad Request] Missing an image");
            }
            String keyName = request.getUsername();
            byte[] image = Base64.getDecoder().decode(request.getImage());
            InputStream stream = new ByteArrayInputStream(image);
            url = uploadFile(keyName, stream, image.length);

            UserBean userBean = new UserBean();
            userBean.setUseralias(request.getUsername());
            userBean.setFirstName(request.getFirstName());
            userBean.setLastName(request.getLastName());
            userBean.setImageURL(url);
            userBean.setPassword(getSecurePassword(request.getPassword(), getSalt()));
            factory.getUserDAO().putUser(userBean);

            token = new AuthToken();
            AuthtokenBean tokenBean = new AuthtokenBean();
            tokenBean.setAuthtoken(token.getToken());
            tokenBean.setDateTime(token.getDatetime());
            factory.getAuthtokenDAO().putAuthtoken(tokenBean);
        } catch(Exception e) {
            return new RegisterResponse(e.getMessage());
        }
        return new RegisterResponse(new User(request.getFirstName(),
                request.getLastName(), request.getUsername(), url), token);
    }

    public UserResponse getUser(UserRequest request) {
        UserBean user;
        try {
            if (request.getAuthToken() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have an auth token");
            } else if (request.getUserAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a user alias");
            }
            if (!checkAndUpdateAuthtoken(request.getAuthToken().getToken())) {
                throw new RuntimeException("[Bad Request] Authtoken is invalid");
            }
            user = factory.getUserDAO().getUser(request.getUserAlias());
        } catch(Exception e) {
            return new UserResponse(e.getMessage());
        }
        return new UserResponse(new User(user.getFirstName(), user.getLastName(),
                user.getUseralias(), user.getImageURL()));
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

    private String uploadFile(String keyName, InputStream content, long contentLength) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType("image/jpeg");

            s3.putObject(new PutObjectRequest("cs340mckameybucket", keyName, content, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return s3.getUrl("cs340mckameybucket", keyName).toString();
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getSecurePassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }
}
