package it.gov.pagopa.fdrtechsupport.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.bson.Document;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

public class MongoTestResource implements QuarkusTestResourceLifecycleManager {

  private MongoDBContainer mongo;

  @Override
  public Map<String, String> start() {
    mongo = new MongoDBContainer("mongo:7.0");
    mongo.start();

    MongoClient client = MongoClients.create(mongo.getReplicaSetUrl());

    setupCollection(
        client,
        "fdr1-metadata",
        """
                  {
                    "psp": "88888888888",
                    "PartitionKey":"2026-01-11",
                    "brokerPsp": "88888888888",
                    "channel": "88888888888_01",
                    "creditorInstitution": "77777777777",
                    "flowId": "2026-01-1188888888888-664371785",
                    "flowDate": "2026-01-11T07:27:00.454Z",
                    "blobBodyRef": {
                      "storageAccount": "devstoreaccount1",
                      "containerName": "fdr1-flows",
                      "fileName": "2026-01-1188888888888-664371785_cf48692b-5269-4cf5-a6ed-8de6c83c6097.xml.zip",
                      "fileLength": 1354
                    },
                    "pspCreditorInstitution": "8888888888877777777777"
                  }
            """);

    setupCollection(
        client,
        "events",
        """
                {
                  "PartitionKey": "2026-01-17",
                  "serviceIdentifier": "FDR001",
                  "uniqueId": "2026-01-17_-1655454224340461571",
                  "created": "2026-01-17T09:26:13.951677",
                  "sessionId": "d29215bd-ab7b-45fd-a653-9e9d2dae8977",
                  "eventType": "INTERNAL",
                  "fdrStatus": "PUBLISHED",
                  "fdr": "2026-01-1788888888888-421875850",
                  "pspId": "88888888888",
                  "organizationId": "77777777777",
                  "fdrAction": "nodoInviaFlussoRendicontazione",
                  "httpType": "INTERN",
                  "httpMethod": "POST",
                  "httpUrl": "http://weuuat.fdr.internal.uat.platform.pagopa.it/webservices/input/",
                  "blobBodyRef": null,
                }
            """);

    client.close();

    return ImmutableMap.of("quarkus.mongodb.connection-string", mongo.getReplicaSetUrl());
  }

  @Override
  public void stop() {
    if (mongo != null) {
      mongo.stop();
    }
  }

  private void setupCollection(MongoClient client, String collectionName, String jsonDocument) {
    MongoCollection<Document> collection =
            client.getDatabase("fdr-re").getCollection(collectionName);

    collection.createIndex(new Document("PartitionKey", 1), new IndexOptions().unique(true));
    collection.insertOne(Document.parse(jsonDocument));
  }
}
