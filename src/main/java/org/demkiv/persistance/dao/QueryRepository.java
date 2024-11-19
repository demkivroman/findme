package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.configuration.SqlQueriesProvider;
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
    private final SqlQueriesProvider sqlQueriesProvider;


    public List<?> findPersonsAndPhoto(String item) {
        char charValue = '%';
        String query = String.format(sqlQueriesProvider.getFindPersonInformation(), charValue, item, charValue, charValue, item, charValue);

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    public PersonResponseModel<PersonDetailModel> getDetailedPersonInfoFromDB(String personId) {
        final String personInfoQuery = String.format(sqlQueriesProvider.getPersonDetailedInformation(), personId);
        final String postsTotalQuery = String.format(sqlQueriesProvider.getPostsTotalQuery(), personId);

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(String.format(personInfoQuery, personId));
        if (queryResult.isEmpty()) {
            throw new FindMeServiceException(String.format("Person with id - %s is not present in DB.", personId));
        }
        String postsCount = jdbcTemplate.queryForObject(String.format(postsTotalQuery, personId), String.class);
        return convertQueryResultsToPersonDetailedModel(queryResult, postsCount);
    }

    public List<?> getPersonPosts(String personId) {
        final String query = String.format(sqlQueriesProvider.getPersonPosts(), personId);
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
