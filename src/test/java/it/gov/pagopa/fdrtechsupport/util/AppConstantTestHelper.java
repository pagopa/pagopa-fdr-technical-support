package it.gov.pagopa.fdrtechsupport.util;

import com.azure.data.tables.models.TableEntity;
import io.restassured.http.Header;
import it.gov.pagopa.fdrtechsupport.repository.model.FdrEventEntity;
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
    entity.addProperty("flowName", flowName);
    entity.addProperty("revision", revision);
    entity.addProperty("serviceIdentifier", isnew ? "FDR003" : "FDR001");
    return entity;
  }

  public static final FdrEventEntity newMongoEntity(
      LocalDate date, String pa, String psp, String flowName, int revision, boolean isnew) {
    FdrEventEntity entity = new FdrEventEntity();
    entity.setCreated(Util.format(date));
    entity.setOrganizationId(pa);
    entity.setPspId(psp);
    entity.setFdr(flowName);
    entity.setRevision(revision);
    entity.setServiceIdentifier(isnew ? "FDR003" : "FDR001");
    return entity;
  }
}
