package com.example.electricitybackend.commons.data.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.example.electricitybackend.commons.data.constant.RegexConstant.PASSWORD_PATTERN;
import static com.example.electricitybackend.commons.data.constant.RegexConstant.PHONE_NUMBER_PATTERN;

@Data
@Accessors(chain = true)
public class HouseholdRequest {
    @NotBlank(message = "username không được để trống")
    private String username;
    @NotBlank(message = "password không được để trống")
    @Pattern(regexp = PASSWORD_PATTERN, message = "Mật khẩu phải lớn hơn 6 kí tự,bao gồm một kí tự số, có một kí tự viết hoa, có một kí tự viết thường")
    private String password;
    private String confirmPassword;
    @NotBlank(message = "tên hộ gia đình không được để trống")
    private String householdName;
    @NotBlank(message = "địa chỉ gia đình không được để trống")
    private String address;
    @Pattern(regexp = PHONE_NUMBER_PATTERN,message = "số điện thoại không đúng định dạng")
    private String phoneNumber;
}
