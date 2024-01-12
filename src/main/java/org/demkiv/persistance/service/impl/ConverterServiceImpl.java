package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.FinderModel;
import org.demkiv.web.model.PersonModel;
import org.demkiv.web.model.PhotoModel;
import org.demkiv.web.model.PostModel;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

@Service
public class ConverterServiceImpl implements ConverterService {
    @Override
    public PersonModel convertToPersonModel(Map<String, Object> value) {
        return PersonModel.builder()
                .id(Objects.toString(value.get("person_id")))
                .fullName(Objects.toString(value.get("person_fullname")))
                .birthday(Objects.toString(value.get("BIRTHDAY")))
                .description(Objects.toString(value.get("DESCRIPTION")))
                .build();
    }

    @Override
    public FinderModel convertToFinderModel(Map<String, Object> value) {
        return FinderModel.builder()
                .id(Objects.toString(value.get("finder_id")))
                .email(Objects.toString(value.get("email")))
                .fullName(Objects.toString(value.get("finder_fullname")))
                .phone(Objects.toString(value.get("phone")))
                .information(Objects.toString(value.get("information")))
                .build();
    }

    @Override
    public PhotoModel convertToPhotoModel(Map<String, Object> value) {
        return PhotoModel.builder()
                .id(Objects.toString(value.get("photo_id")))
                .url(Objects.toString(value.get("url")))
                .build();
    }

    @Override
    public PostModel convertToPostModel(Map<String, Object> value) {
        return PostModel.builder()
                .id(Objects.toString(value.get("post_id")))
                .post(Objects.toString(value.get("post")))
                .timestamp(Objects.toString(value.get("time")))
                .build();
    }
}
