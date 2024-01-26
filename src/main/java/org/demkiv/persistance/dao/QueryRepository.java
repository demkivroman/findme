package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.response.PersonDetailModel;
import org.demkiv.persistance.model.response.SearchPersonsModel;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonResponseModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;


    public List<?> findPersonsAndPhoto(String fullName, String description) {
        final String query = "select person.id as person_id, person.FULLNAME as person_fullname, person.BIRTHDAY, person.DESCRIPTION, person.TIME,"  +
                "photo.id as photo_id, photo.URL from person\n" +
                "left join photo on person.ID = photo.PERSON_ID\n" +
                "where person.FULLNAME like '%" + fullName + "%'" + " or " + "person.DESCRIPTION like '%" + description + "%'";

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    public PersonResponseModel<PersonDetailModel> getDetailedPersonInfoFromDB(String personId) {
        final String personInfoQuery = "select person.id as person_id, person.FULLNAME as person_fullname, person.BIRTHDAY, person.DESCRIPTION, person.TIME,\n" +
                "finder.id as finder_id, finder.FULLNAME as finder_fullname, finder.PHONE, finder.EMAIL, finder.INFORMATION,\n" +
                "photo.id as photo_id, photo.URL\n" +
                "from person\n" +
                "left join finder on person.FINDER_ID = finder.ID\n" +
                "left join photo on person.ID = photo.PERSON_ID\n" +
                "where person.id = %s";

        final String postsTotalQuery = "select count(posts.POST) as totalPosts from posts where posts.PERSON_ID = %s";

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(String.format(personInfoQuery, personId));
        if (queryResult.isEmpty()) {
            throw new FindMeServiceException(String.format("Person with id - %s is not present in DB.", personId));
        }
        String postsCount = jdbcTemplate.queryForObject(String.format(postsTotalQuery, personId), String.class);
        return convertQueryResultsToPersonDetailedModel(queryResult, postsCount);
    }

    public List<?> getPersonPosts(String personId) {
        final String query = "select posts.ID, posts.POST, posts.TIME from posts where PERSON_ID = %s";
        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(String.format(query, personId));
        return queryResult.stream()
                .map(converter::convertQueryRowToPostDTO)
                .collect(Collectors.toList());
    }

    private PersonResponseModel<PersonDetailModel> convertQueryResultsToPersonDetailedModel(
            List<Map<String, Object>> queryResult,
            String postsCount) {
        Set<PersonDTO> personSet = new LinkedHashSet<>();
        Set<FinderDTO> finderSet = new LinkedHashSet<>();
        Set<PhotoDTO> photoSet = new LinkedHashSet<>();

        queryResult
                .forEach(rowMap -> {
                    personSet.add(converter.convertQueryRowToPersonDTO(rowMap));
                    finderSet.add(converter.convertQueryRowToFinderDTO(rowMap));
                    photoSet.add(converter.convertQueryRowToPhotoDTO(rowMap));
                });

        PersonDetailModel detail = PersonDetailModel.builder()
                .person(personSet.iterator().next())
                .finder(finderSet.iterator().next())
                .photos(photoSet)
                .totalPosts(postsCount)
                .build();

        return PersonResponseModel.<PersonDetailModel>builder()
                .person(detail)
                .build();
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
        SearchPersonsModel searchModel = SearchPersonsModel.builder()
                .person(personDTO)
                .photo(photoDTO)
                .build();
        persons.putIfAbsent(personDTO.getId(), searchModel);
    }
}
