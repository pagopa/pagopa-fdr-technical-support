package it.gov.pagopa.fdrtechsupport.util;

import com.azure.data.tables.models.TableEntity;
import io.restassured.http.Header;

import java.time.LocalDate;
import java.util.Optional;

public class AppConstantTestHelper {

  public static final String SP03_NN = "/organizations/%s/noticeNumber/%s";
  public static final String SP03_IUV = "/organizations/%s/iuv/%s";

  public static final String SP04_NN = "/organizations/%s/iuv/%s/paymentToken/%s";
  public static final String SP04_IUV = "/organizations/%s/iuv/%s/ccp/%s";

  public static final String PA_CODE = "12345678900";
  public static final String outcomeOK = "OK";
  public static final String outcomeKO = "KO";

  public static final Header HEADER = new Header("Content-Type", "application/json");

  public static final TableEntity newRe(String pa, String noticeNumber, String iuv) {
    TableEntity entity =
        new TableEntity(
            Util.format(LocalDate.now()),
            String.valueOf(Optional.ofNullable(noticeNumber).orElse(iuv)));
    entity.addProperty("idDominio", pa);
    entity.addProperty("noticeNumber", noticeNumber);
    entity.addProperty("iuv", iuv);
    entity.addProperty("esito", "CAMBIO_STATO");
    if(noticeNumber!=null)
      entity.addProperty("paymentToken", "pt_" + noticeNumber);
    if(iuv!=null)
      entity.addProperty("ccp", "ccp_" + iuv);
    entity.addProperty("stazione", "77777777777_01");
    entity.addProperty("psp", "pspTest");
    entity.addProperty("canale", "canaleTest");
    entity.addProperty("status", "PAID");
    return entity;
  }



}
