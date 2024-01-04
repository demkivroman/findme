package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ConverterServiceImpl implements ConverterService {
    @Override
    public PersonFoundForm convertPersonToPersonFoundForm(Map<String, Object> value) {
        return PersonFoundForm.builder()
                .id(Objects.toString(value.get("id")))
                .personFullName(Objects.toString(value.get("person_fullname")))
                .personBirthday(Objects.toString(value.get("BIRTHDAY")))
                .personDescription(Objects.toString(value.get("DESCRIPTION")))
                .finderFullName(Objects.toString(value.get("finder_fullname")))
                .finderPhone(Objects.toString(value.get("PHONE")))
                .finderEmail(Objects.toString(value.get("EMAIL")))
                .finderInformation(Objects.toString(value.get("INFORMATION")))
                .urls(List.of(Objects.toString(value.get("url"))))
                .build();
    }
}
