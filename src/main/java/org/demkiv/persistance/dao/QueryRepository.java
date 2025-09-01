package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.configuration.SqlQueriesProvider;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.response.SearchPersonsModel;
import org.demkiv.persistance.service.ConverterService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;
    private final SqlQueriesProvider sqlQueriesProvider;

    private final static char CHAR_VALUE = '%';


    public List<?> findPersonsAndPhoto(String item) {
        String query = String.format(sqlQueriesProvider.getFindPersonInformation(), CHAR_VALUE, item, CHAR_VALUE, CHAR_VALUE, item, CHAR_VALUE);

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    public List<?> getPersonsDataAndThumbnails(Set<Long> personIds) {
        return personIds.stream()
                        .map(id -> {
                            String query = String.format(sqlQueriesProvider.getSelectPersonsAndPhotosByIds(), id);
                            List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
                            return convertQueryResults(queryResult);
                        })
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
    }

    public boolean deletePhotoByIdFromDB(String photoId) {
        final String query = String.format(sqlQueriesProvider.getDeletePhotoById(), photoId);
        jdbcTemplate.execute(query);
        return true;
    }

    public List<String> findThumbnailIdByName(String thumbnailName) {
       final String query = String.format(sqlQueriesProvider.getFindThumbnailIdByName(), CHAR_VALUE, thumbnailName);
        return jdbcTemplate.queryForList(query, String.class);
    }

    public boolean deleteThumbnailByIdFromDB(String id) {
        final String query = String.format(sqlQueriesProvider.getDeleteThumbnailById(), id);
        jdbcTemplate.execute(query);
        return true;
    }

    public List<Long> getPersonIds() {
        return jdbcTemplate.queryForList(sqlQueriesProvider.getPersonIds(), Long.class);
    }

    private List<?> convertQueryResults(List<Map<String, Object>> queryResult) {
        Map<String, SearchPersonsModel> persons = new HashMap<>();
        queryResult.forEach(row -> {
            collectPerson(persons, row);
        });

        return new ArrayList<>(persons.values());
    }

    private void collectPerson(Map<String, SearchPersonsModel> persons, Map<String, Object> queryRow) {
        PersonDTO personDTO = converter.convertQueryRowToPersonDTO(queryRow);
        PhotoDTO photoDTO = converter.convertQueryRowToPhotoDTO(queryRow);
        List<PhotoDTO> photoDTOS = new ArrayList<>();

        if (persons.containsKey(personDTO.getId())) {
            List<PhotoDTO> existedPhotos = persons.get(personDTO.getId()).getPhoto();
            photoDTOS.addAll(existedPhotos);
            photoDTOS.add(photoDTO);
            persons.get(personDTO.getId()).setPhoto(photoDTOS);
        } else {
            SearchPersonsModel searchModel = SearchPersonsModel.builder()
                    .person(personDTO)
                    .photo((photoDTO == null) ? List.of() : List.of(photoDTO))
                    .build();
            persons.putIfAbsent(String.valueOf(personDTO.getId()), searchModel);
        }
    }
}
