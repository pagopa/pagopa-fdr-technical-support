locals {
  repo_name = "pagopa-fdr-technical-support"

  display_name          = "FdR Technical Support"
  description           = "API Assistenza dei Flussi di Rendicontazione"
  path                  = "technical-support/fdr/api"

  subscription_required = true
  service_url           = null
}

resource "azurerm_api_management_api_version_set" "api_version_set" {
  name                = format("%s-technical-support-api", local.project)
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  versioning_scheme   = "Segment"
}

module "api_v1" {
  source = "./.terraform/modules/__v3__/api_management_api"

  name                  = format("%s-technical-support-api", local.project)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id]
  subscription_required = local.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_version_set.id
  api_version    = "v1"

  description  = local.description
  display_name = local.display_name
  path         = local.path
  protocols    = ["https"]

  service_url = local.service_url

  content_format = "openapi"
  content_value  = templatefile("../openapi/openapi_infra.json", {
    host = local.apim_hostname,
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = var.hostname
  })
}
