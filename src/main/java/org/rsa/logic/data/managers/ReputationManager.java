package org.rsa.logic.data.managers;

import org.rsa.aws.RequestsManager;
import org.rsa.logic.data.models.UserReputation;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Optional;

public final class ReputationManager {
    private final static String TABLE_NAME = "reputation_data";
    private final static RequestsManager<UserReputation> _requestsManager = new RequestsManager<>(TABLE_NAME, UserReputation.class);

    public UserReputation fetch(String guildId, String userId)
    {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(guildId)
                        .sortValue(userId)
                        .build());
        Optional<Page<UserReputation>> optionalUserReputationPage = _requestsManager.fetchSingleItem(queryConditional);

        if (optionalUserReputationPage.isEmpty() || optionalUserReputationPage.get().items().isEmpty())
            return new UserReputation(guildId, userId);
        return optionalUserReputationPage.get().items().get(0);
    }

    public void update(UserReputation userReputation)
    {
        _requestsManager.enqueueItemUpdate(userReputation);
    }
}
