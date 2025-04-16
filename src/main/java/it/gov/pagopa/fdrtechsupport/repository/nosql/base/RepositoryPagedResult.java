package it.gov.pagopa.fdrtechsupport.repository.nosql.base;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RepositoryPagedResult<T> {

  private List<T> data;

  private long totalElements;

  private int totalPages;
}