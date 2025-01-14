variable "product" {
  type    = "string"
}

variable "component" {
  type = "string"
}

variable "location_app" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "ilbIp" {}

variable "subscription" {}

variable "capacity" {
  default = "1"
}

variable "common_tags" {
  type = "map"
}

variable "s2s_name" {
  default = "bulk_scan_ccd_sample_app"
}

variable "test_s2s_name" {
  default = "bulk_scan_sample_app_tests"
}
