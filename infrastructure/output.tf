output "microserviceName" {
  value = "${var.component}"
}

output "TEST_S2S_URL" {
  value = "${local.s2s_url}"
}

output "TEST_S2S_NAME" {
  sensitive = true
  value     = "${var.test_s2s_name}"
}
