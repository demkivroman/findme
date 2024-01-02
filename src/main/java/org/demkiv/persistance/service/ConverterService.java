package org.demkiv.persistance.service;

import org.demkiv.persistance.entity.Person;
import org.demkiv.web.model.PersonFoundForm;

public interface ConverterService {
    PersonFoundForm convertPersonToPersonFoundForm(Person value);
}
