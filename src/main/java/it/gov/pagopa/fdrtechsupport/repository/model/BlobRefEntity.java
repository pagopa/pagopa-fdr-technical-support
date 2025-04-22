package it.gov.pagopa.fdrtechsupport.repository.model;

import lombok.Data;

@Data
public class BlobRefEntity {

  private String storageAccount;

  private String containerName;

  private String fileName;

  private Long fileLength;
}
