package org.demkiv.domain.service;

import org.demkiv.web.model.PersonDetailedModel;

public interface PersonService {
    PersonDetailedModel getDetailedPersonInfo(String personId);
}
