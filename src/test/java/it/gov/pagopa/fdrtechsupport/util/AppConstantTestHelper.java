package it.gov.pagopa.fdrtechsupport.util;

import com.azure.data.tables.models.TableEntity;
import io.restassured.http.Header;
import java.time.LocalDate;

public class AppConstantTestHelper {

  public static final String SP03_NN = "/organizations/%s/noticeNumber/%s";
  public static final String SP03_IUV = "/organizations/%s/iuv/%s";

  public static final String SP04_NN = "/organizations/%s/iuv/%s/paymentToken/%s";
  public static final String SP04_IUV = "/organizations/%s/iuv/%s/ccp/%s";

  public static final String PA_CODE = "12345678900";
  public static final String PSP_CODE = "PSP";
  public static final String outcomeOK = "OK";
  public static final String outcomeKO = "KO";

  public static final Header HEADER = new Header("Content-Type", "application/json");

  public static final TableEntity newTableFdr(
      LocalDate date, String pa, String psp, String flowName, int revision, boolean isnew) {
    TableEntity entity = new TableEntity(Util.format(date), flowName);
    entity.addProperty("organizationId", pa);
    entity.addProperty("pspId", psp);
    entity.addProperty("fdr", flowName);
    entity.addProperty("revision", revision);
    entity.addProperty("eventType", "INTERNAL");
    entity.addProperty("fdrStatus", "PUBLISHED");
    entity.addProperty("fdrAction", "PUBLISH");
    entity.addProperty("serviceIdentifier", isnew ? "FDR003" : "FDR001");
    return entity;
  }

  public static final TableEntity newTableFdrPaymentPublish(
      LocalDate date, String psp, String orgId, String flowName, String iur) {
    TableEntity entity = new TableEntity(Util.format(date), flowName);
    entity.addProperty("ref_fdr_sender_psp_id", psp);
    entity.addProperty("ref_fdr", flowName);
    entity.addProperty("created", date.atStartOfDay());
    entity.addProperty("ref_fdr_receiver_organization_id", orgId);
    entity.addProperty("iur", iur);
    entity.addProperty("iuv", iur);
    entity.addProperty("eventType", "INTERNAL");
    entity.addProperty("fdrStatus", "PUBLISHED");
    entity.addProperty("fdrAction", "PUBLISH");
    return entity;
  }

  public static final TableEntity newTableFdrPublish(
      LocalDate date,
      String orgId,
      String psp,
      String flowName,
      Integer revision,
      String containerName,
      String fileName) {
    TableEntity entity = new TableEntity(Util.format(date), flowName);
    entity.addProperty("sender_psp_id", psp);
    entity.addProperty("fdr", flowName);
    entity.addProperty("revision", revision);
    entity.addProperty("created", date.atStartOfDay());
    entity.addProperty("receiver_organization_id", orgId);
    entity.addProperty("jsonref_container_name", containerName);
    entity.addProperty("jsonref_file_name", fileName);
    return entity;
  }
}
