package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateCaptchaForm {
    String personId;
    String captcha;
}
