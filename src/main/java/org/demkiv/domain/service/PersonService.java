package org.demkiv.domain.service;

import jakarta.servlet.http.HttpServletRequest;
import org.demkiv.web.model.PersonResponseModel;

import java.util.List;

public interface PersonService {
    PersonResponseModel<?> getDetailedPersonInfo(String personId);
    List<?> getRandomPersons(int count);
    boolean generateCapchaAndPushToSessionAndSendEmail(Long personId, HttpServletRequest request);
}
