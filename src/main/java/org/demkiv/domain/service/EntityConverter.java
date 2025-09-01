package org.demkiv.domain.service;

import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;

import java.util.List;
import java.util.Set;

public interface EntityConverter {

    PersonDTO convertToPersonDTO(Person person);
    FinderDTO convertToFinderDTO(Finder finder, Subscriptions emailStatus);
    List<PhotoDTO> convertToPhotoDTO(Set<Photo> photos);
}
