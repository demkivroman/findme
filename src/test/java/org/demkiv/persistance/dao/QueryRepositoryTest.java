package org.demkiv.persistance.dao;

import org.demkiv.domain.configuration.SqlQueriesProvider;
import org.demkiv.persistance.model.dto.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
public class QueryRepositoryTest {

    @Mock
    JdbcTemplate jdbcTemplate;
    @Autowired
    ConverterService converter;
    @Autowired
    SqlQueriesProvider sqlQueriesProvider;

    QueryRepository queryRepository;

    @Captor
    ArgumentCaptor<String> queryCaptor;


    @BeforeEach
    void setup() {
        queryRepository = new QueryRepository(jdbcTemplate, converter, sqlQueriesProvider);
    }

//    @Test
    void shouldProperlyConstructQuerySelectPersonsAndThumbnailsByIds() {
        // given
        String givenQuery = "select person.id as person_id, person.FULLNAME as person_fullname, person.BIRTHDAY, person.DESCRIPTION, person.TIME, thumbnail.id as photo_id, " +
                "thumbnail.URL as photo_url, thumbnail.person_id from person left join thumbnail on " +
                "(person.id=1 and thumbnail.person_id=1) or " +
                "(person.id=2 and thumbnail.person_id=2) or " +
                "(person.id=3 and thumbnail.person_id=3)" +
                " where person.id=1 or person.id=2 or person.id=3";
        Set<Long> givenIds = new TreeSet<>();
        givenIds.add(3L);
        givenIds.add(2L);
        givenIds.add(1L);
        when(jdbcTemplate.queryForList(queryCaptor.capture())).thenReturn(List.of());

        // when
        List<?> results = queryRepository.getPersonsDataAndThumbnails(givenIds);

        // then
        assertThat(givenQuery).isEqualTo(queryCaptor.getValue());
    }

//    @Test
    void whenCheckPostsOrder_shouldReturnReverse() {
        // given
        List<Map<String, Object>> givenList = List.of(
                Map.of(
                        "id", 1 ,
                        "author", "testAuthor",
                        "post", "some post",
                        "time", "2024-12-07 10:42:26"
                ),
                Map.of(
                        "id", 2,
                        "author", "testAuthor",
                        "post", "some post",
                        "time", "2024-12-07 10:42:27"
                ),
                Map.of(
                        "id", 3,
                        "author", "testAuthor",
                        "post", "some post",
                        "time", "2024-12-08 10:42:26"
                )
        );

        when(jdbcTemplate.queryForList(anyString())).thenReturn(givenList);

        // when
        List<?> posts = queryRepository.getPersonPosts("testID");

        // then
        assertThat(posts).hasSize(3);
        assertThat(((PostDTO) posts.get(0)).getId())
                .isEqualTo("3");

        assertThat(((PostDTO) posts.get(1)).getId())
                .isEqualTo("2");

        assertThat(((PostDTO) posts.get(2)).getId())
                .isEqualTo("1");
    }
}
