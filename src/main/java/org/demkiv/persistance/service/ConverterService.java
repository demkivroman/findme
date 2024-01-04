package org.demkiv.persistance.service;

import org.demkiv.web.model.PersonFoundForm;

import java.util.Map;

public interface ConverterService {
    PersonFoundForm convertPersonToPersonFoundForm(Map<String, Object> value);
}
