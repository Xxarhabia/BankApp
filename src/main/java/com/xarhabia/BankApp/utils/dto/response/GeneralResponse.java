package com.xarhabia.BankApp.utils.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"rsp_code", "rsp_msg", "status"})
public record GeneralResponse(String rsp_code, String rsp_msg, boolean status, Object rsp_data) {
}
