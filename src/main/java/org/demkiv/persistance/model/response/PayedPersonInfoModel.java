package org.demkiv.persistance.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayedPersonInfoModel {

    private long id;
    private String fullName;
    private LocalDate birthDay;
    private int age;
    String thumbnail;
}
