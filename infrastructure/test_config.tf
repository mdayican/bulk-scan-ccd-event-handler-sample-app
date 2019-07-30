
data "azurerm_key_vault_secret" "source_test_s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.s2s_key_vault.id}"
  name         = "microservicekey-bulk-scan-sample-app-tests"
}

resource "azurerm_key_vault_secret" "bulk_scan_sample_app_tests_s2s_secret" {
  key_vault_id = "${data.azurerm_key_vault.key_vault.id}"
  name         = "bulk-scan-sample-app-tests-s2s-secret"
  value        = "${data.azurerm_key_vault_secret.source_test_s2s_secret.value}"
}
