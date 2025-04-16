package it.gov.pagopa.fdrtechsupport.util.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppConstant {

  public static final String SERVICE_CODE_APP = "FDR";

  public static final String HISTORICAL_FDR3_FILENAME_TEMPLATE = "%s_%s_%s.json.zip";

  public static final List<String> RE_EVENTS_DOWNLOAD_ACTIONS;

  public static final List<String> RE_EVENTS_UPLOAD_ACTIONS;

  static {
    List<String> downloadActions =
        Arrays.asList("nodoChiediFlussoRendicontazione", "GET_FDR", "GET_FDR_PAYMENT");
    RE_EVENTS_DOWNLOAD_ACTIONS = Collections.unmodifiableList(downloadActions);

    List<String> uploadActions =
        Arrays.asList(
            "nodoInviaFlussoRendicontazione",
            "PUBLISH",
            "ADD_PAYMENT",
            "CREATE_FLOW",
            "DELETE_FLOW",
            "DELETE_PAYMENT");
    RE_EVENTS_UPLOAD_ACTIONS = Collections.unmodifiableList(uploadActions);
  }

  private AppConstant() {
    throw new IllegalStateException("Constants class");
  }
}
