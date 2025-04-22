locals {
  product = "${var.prefix}-${var.env_short}"
  project = "${var.prefix}-${var.env_short}-${var.location_short}-${var.domain}"

  apim = {
    name           = "${local.product}-apim"
    rg             = "${local.product}-api-rg"
    product_id = "technical_support_api"
  }

  apim_hostname = "api.${var.apim_dns_zone_prefix}.${var.external_domain}"
  hostname      = var.env == "prod" ? "weuprod.fdr.internal.platform.pagopa.it" : "weu${var.env}.fdr.internal.${var.env}.platform.pagopa.it"
}
