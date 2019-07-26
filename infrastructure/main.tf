provider "azurerm" {}

locals {
  ase_name            = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  is_preview          = "${(var.env == "preview" || var.env == "spreview")}"
  local_env           = "${local.is_preview ? "aat" : var.env}"
  s2s_rg              = "rpe-service-auth-provider-${local.local_env}"
  s2s_url             = "http://${local.s2s_rg}.service.core-compute-${local.local_env}.internal"
  ccd_api_url         = "http://ccd-data-store-api-${local.local_env}.service.core-compute-${local.local_env}.internal"

  vaultName           = "bulk-scan-${var.env}"
}

module "bulk-scan-ccd-event-handler-sample-app" {
  source              = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product             = "${var.product}-${var.component}"
  location            = "${var.location_app}"
  env                 = "${var.env}"
  ilbIp               = "${var.ilbIp}"
  subscription        = "${var.subscription}"
  capacity            = "${var.capacity}"
  common_tags         = "${var.common_tags}"

  app_settings = {
    S2S_URL                 = "${local.s2s_url}"
    S2S_NAME                = "${var.s2s_name}"
    S2S_SECRET              = "${data.azurerm_key_vault_secret.s2s_secret.value}"
    CORE_CASE_DATA_API_URL  = "${local.ccd_api_url}"
  }
}

data "azurerm_key_vault" "key_vault" {
  name                = "${local.vaultName}"
  resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${local.local_env}"
  resource_group_name = "${local.s2s_rg}"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.s2s_key_vault.id}"
  name         = "microservicekey-bulk-scan-ccd-sample-app"
}
