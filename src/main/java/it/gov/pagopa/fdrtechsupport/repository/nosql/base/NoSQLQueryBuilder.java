package it.gov.pagopa.fdrtechsupport.repository.nosql.base;

import io.quarkus.panache.common.Parameters;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import joptsimple.internal.Strings;
import lombok.Getter;

public class NoSQLQueryBuilder {

  private static final String BETWEEN_DATE_CLAUSE_TEMPLATE = "'%s': {'$gte' : :%s, '$lt' : :%s}";

  private static final String EQUALS_CLAUSE_TEMPLATE = "'%s': :%s";

  private static final String NOT_NULL_CLAUSE_TEMPLATE = "'%s': {'$ne' : null}";

  private static final String IN_CLAUSE_TEMPLATE = "'%s': {'$in' : [:%s]}";

  private List<String> queryClauses;

  @Getter private Parameters parameters;

  public static NoSQLQueryBuilder startQuery() {

    NoSQLQueryBuilder builder = new NoSQLQueryBuilder();
    builder.parameters = new Parameters();
    builder.queryClauses = new LinkedList<>();
    return builder;
  }

  public NoSQLQueryBuilder andInDateRange(
      String entityFieldName,
      String fromFieldName,
      LocalDate from,
      String toFieldName,
      LocalDate to) {

    this.queryClauses.add(
        String.format(BETWEEN_DATE_CLAUSE_TEMPLATE, entityFieldName, fromFieldName, toFieldName));
    this.parameters.and(fromFieldName, DateTimeFormatter.ISO_DATE.format(from));
    this.parameters.and(toFieldName, DateTimeFormatter.ISO_DATE.format(to));
    return this;
  }

  public NoSQLQueryBuilder andEquals(String entityFieldName, String paramName, String value) {

    this.queryClauses.add(String.format(EQUALS_CLAUSE_TEMPLATE, entityFieldName, paramName));
    this.parameters.and(paramName, value);
    return this;
  }

  public NoSQLQueryBuilder andNotNull(String entityFieldName) {

    this.queryClauses.add(String.format(NOT_NULL_CLAUSE_TEMPLATE, entityFieldName));
    return this;
  }

  public NoSQLQueryBuilder andIn(String entityFieldName, String paramName, Collection<?> value) {

    this.queryClauses.add(String.format(IN_CLAUSE_TEMPLATE, entityFieldName, paramName));
    this.parameters.and(paramName, value);
    return this;
  }

  public String getQuery() {

    String concatenatedClauses = Strings.join(this.queryClauses, ", ");
    return "{ " + concatenatedClauses + " }";
  }
}
