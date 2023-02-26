package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.SQS.FeedUpdater;
import edu.byu.cs.tweeter.server.SQS.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactoryDB;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        StatusService statusService = new StatusService(new DAOFactoryDB());
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            FeedUpdater updater = JsonSerializer.deserialize(msg.getBody(), FeedUpdater.class);

            Status status = updater.getStatus();
            statusService.batchWriteFeeds(updater.getFollowers(), status.getUser().getAlias(), status.getDate(), status.getPost(), status.getUrls(), status.getMentions());
        }
        return null;
    }
}
