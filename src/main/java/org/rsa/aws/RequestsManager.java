package org.rsa.aws;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;


public class RequestsManager<T> {
    private final Queue<UpdateItemEnhancedRequest<T>> updateRequestsQueue = new LinkedList<>();
    private final DynamoDbTable<T> table;
    private final Class<T> classType;
    private boolean processingUpdateRequests = false;

    public RequestsManager(String tableName, Class<T> type)
    {
        this.table = DynamoDB.getDynamoTable(tableName, TableSchema.fromBean(type));
        this.classType = type;
    }

    private void processUpdateRequests()
    {
        if (updateRequestsQueue.isEmpty()) return;

        processingUpdateRequests = true;
        UpdateItemEnhancedRequest<T> request = updateRequestsQueue.remove();
        table.updateItem(request);

        processingUpdateRequests = false;
        processUpdateRequests();
    }

    public void enqueueItemUpdate(T item)
    {
        UpdateItemEnhancedRequest<T> request = UpdateItemEnhancedRequest.builder(classType)
                .item(item)
                .build();
        enqueueUpdateRequest(request);
    }

    public void enqueueUpdateRequest(UpdateItemEnhancedRequest<T> request)
    {
        updateRequestsQueue.add(request);
        if (processingUpdateRequests) return;
        processUpdateRequests();
    }

    public Optional<Page<T>> fetchSingleItem(QueryConditional queryConditional)
    {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(1)
            .scanIndexForward(false)
            .build();
        PageIterable<T> pages = table.query(request);
        return pages.stream().findFirst();
    }
}
