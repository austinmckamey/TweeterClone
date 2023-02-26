package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.SQS.FeedUpdater;
import edu.byu.cs.tweeter.server.SQS.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactoryDB;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {

    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        String lastFollowerAlias = null;
        boolean hasMorePages = true;
        FollowService followService = new FollowService(new DAOFactoryDB());
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Status status = JsonSerializer.deserialize(msg.getBody(), Status.class);

            while(hasMorePages) {
                FollowersResponse response = followService.getFollowersWithoutAuthentication(new FollowersRequest(null,
                        status.getUser().getAlias(), 25, lastFollowerAlias));
                hasMorePages = response.getHasMorePages();
                List<User> followers = response.getFollowers();
                List<String> aliases = followers.stream().map(User::getAlias).collect(Collectors.toList());
                lastFollowerAlias = followers.isEmpty() ? null : followers.get(followers.size() - 1).getAlias();

                String messageBody = JsonSerializer.serialize(new FeedUpdater(aliases, status));
                String queueUrl = "https://sqs.us-west-2.amazonaws.com/696439776447/UpdateFeedQueue";

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(messageBody);

                sqs.sendMessage(send_msg_request);
            }
        }
        return null;
    }
}
