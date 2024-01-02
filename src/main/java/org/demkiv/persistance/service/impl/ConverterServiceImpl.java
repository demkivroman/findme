package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.stereotype.Service;

@Service
public class ConverterServiceImpl implements ConverterService {
    @Override
    public PersonFoundForm convertPersonToPersonFoundForm(Person value) {
        return null;
    }
}
