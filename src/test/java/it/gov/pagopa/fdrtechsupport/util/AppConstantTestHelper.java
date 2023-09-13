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
  public static final String outcomeOK = "OK";
  public static final String outcomeKO = "KO";

  public static final Header HEADER = new Header("Content-Type", "application/json");

  public static final TableEntity newFdr(LocalDate date,String pa,String flowName,int revision,boolean isnew) {
    TableEntity entity =
        new TableEntity(
            Util.format(date),
                flowName);
    entity.addProperty("organizationId", pa);
    entity.addProperty("flowName", flowName);
    entity.addProperty("revision", revision);
    entity.addProperty("serviceIdentifier", isnew?"FDR003":"FDR001");
    return entity;
  }



}
