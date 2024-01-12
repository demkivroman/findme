package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.FinderModel;
import org.demkiv.web.model.PersonModel;
import org.demkiv.web.model.PhotoModel;
import org.demkiv.web.model.PostModel;
import org.springframework.jdbc.core.JdbcTemplate;;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Collections.*;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;


    public List<PersonModel> findPersons(String fullName, String description) {
        String query = "select person.id as person_id, person.FULLNAME as person_fullname, person.BIRTHDAY, person.DESCRIPTION," +
                "finder.id as finder_id, finder.FULLNAME as finder_fullname, finder.PHONE, finder.EMAIL, finder.INFORMATION," +
                "photo.id as photo_id, photo.URL, posts.ID as post_id, posts.POST, posts.TIME from person\n" +
                "left join finder on person.FINDER_ID = finder.ID\n" +
                "left join photo on person.ID = photo.PERSON_ID\n" +
                "left join posts on person.ID = posts.PERSON_ID\n" +
                "where person.FULLNAME like '%" + fullName + "%'" + " or " + "person.DESCRIPTION like '%" + description + "%'";

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    private List<PersonModel> convertQueryResults(List<Map<String, Object>> queryResult) {
        Map<String, PersonModel> persons = new HashMap<>();
        queryResult.forEach(line -> {
            collectPerson(persons, line);
        });

        return new ArrayList<>(persons.values());
    }

    private void collectPerson(Map<String, PersonModel> persons, Map<String, Object> queryRow) {
        PersonModel currentPerson = converter.convertToPersonModel(queryRow);
        FinderModel finder = converter.convertToFinderModel(queryRow);
        PhotoModel photo = converter.convertToPhotoModel(queryRow);
        PostModel post = converter.convertToPostModel(queryRow);
        currentPerson.setFinder(finder);
        currentPerson.setUrls(Set.of(photo));
        currentPerson.setPosts((post != null) ? Set.of(post) : EMPTY_SET);
        PersonModel foundPerson = persons.get(currentPerson.getId());

        if (foundPerson == null) {
            persons.put(currentPerson.getId(), currentPerson);
        } else {
            PersonModel personModelNew = combinePersons(foundPerson, currentPerson);
            persons.put(personModelNew.getId(), personModelNew);
        }
    }

    private PersonModel combinePersons(PersonModel oldPerson, PersonModel newPerson) {
        Set<PhotoModel> tempPhotos = new LinkedHashSet<>();
        Set<PostModel> tempPosts = new LinkedHashSet<>();
        tempPhotos.addAll(oldPerson.getUrls());
        tempPhotos.addAll(newPerson.getUrls());
        tempPosts.addAll(oldPerson.getPosts());
        tempPosts.addAll(newPerson.getPosts());
        newPerson.setUrls(tempPhotos);
        newPerson.setPosts(tempPosts);
        return newPerson;
    }

}
