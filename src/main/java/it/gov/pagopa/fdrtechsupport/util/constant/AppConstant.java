package it.gov.pagopa.fdrtechsupport.util.constant;

public class AppConstant {

  public static final String SERVICE_CODE_APP = "FDR";

  public static final String PSP_ID = "pspId";
  public static final String EC_ID = "ecId";
  public static final String FLOW_NAME = "flowName";
  public static final String INDEXES = "indexes";
  public static final String INTERNAL_READ = "internalRead";

  public static final String HISTORICAL_FDR3_FILENAME_TEMPLATE = "%s_%s_%s.json.zip";

  private AppConstant() {
    throw new IllegalStateException("Constants class");
  }
}
