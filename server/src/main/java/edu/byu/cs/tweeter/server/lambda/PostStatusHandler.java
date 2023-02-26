package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.SQS.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactoryDB;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    private final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService userService = new StatusService(new DAOFactoryDB());
        String messageBody = JsonSerializer.serialize(request.getStatus());
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/696439776447/PostStatusQueue";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        sqs.sendMessage(send_msg_request);
        return userService.postStatus(request);
    }
}
