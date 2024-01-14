package org.demkiv.domain.service;

import org.demkiv.web.model.PersonResponseModel;

public interface PersonService {
    PersonResponseModel getDetailedPersonInfo(String personId);
}
