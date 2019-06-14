provider "azurerm" {}

locals {
  ase_name               = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
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
  }
}
