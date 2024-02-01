package it.gov.pagopa.fdrtechsupport.repository.model;

import lombok.Data;

@Data
public class FdrHistoryBlobRefEntity {
  public static String STORAGE_ACCOUNT = "jsonref_storage_account";
  public static String CONTAINER_NAME = "jsonref_container_name";
  public static String FILE_NAME = "jsonref_file_name";
  public static String FILE_LENGTH = "jsonref_file_length";
  public static String JSON_SCHEMA_VERSION = "jsonref_json_schema_version";
  public static String PSP = "sender_psp_id";
  public static String ORGANIZATION = "receiver_organization_id";
  public static String NAME = "fdr";
  public static String REVISION = "revision";
  public static String PARTITIONKEY = "PartitionKey";

  private String storageAccount;
  private String containerName;
  private String fileName;
  private Long fileLength;
  private String jsonSchemaVersion;

  private static String dateFilter = " '%s': { '$gte': :from , '$lt': :to } ";
}
