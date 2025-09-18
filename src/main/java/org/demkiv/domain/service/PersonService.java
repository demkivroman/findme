package org.demkiv.domain.service;

import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.form.ValidateCaptchaForm;

import java.util.List;

public interface PersonService {
    PersonResponseModel<?> getDetailedPersonInfo(String personId);
    List<?> getRandomPersons(int count);
    boolean checkCaptcha(ValidateCaptchaForm captchaForm);
    String generateSessionId();
    boolean deletePhotoAndThumbnailFromDB(String id, String url);
    List<?> getPhotoUrlsFromDBForPerson(String id);
    boolean markPersonAsFound(long personId);
    String processCaptchaCreation(long personId);
}
