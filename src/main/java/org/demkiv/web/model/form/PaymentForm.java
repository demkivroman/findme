package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentForm {

    private long personId;
    private int days;
    private float costPerDay;
}
