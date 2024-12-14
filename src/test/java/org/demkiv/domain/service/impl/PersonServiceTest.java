package org.demkiv.domain.service.impl;

import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.form.PersonForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
public class PersonServiceTest {
    @Mock
    SaveUpdateService<PersonForm, Optional<?>> service;
    @Mock
    QueryRepository queryRepository;
    @InjectMocks
    PersonServiceImpl personService;
    @Captor
    ArgumentCaptor<Set<Long>> randomIdsCaptor;

    @ParameterizedTest
    @MethodSource("parametersForInputArray")
    public void checkServiceGenerateRandomPersons(List<Long> givenIds) {
        // given
        int randomCount = 5;
        when(queryRepository.getPersonIds()).thenReturn(givenIds);
        when(queryRepository.getPersonsDataAndThumbnails(randomIdsCaptor.capture())).thenReturn(null);

        // when
        List<?> foundRecords = personService.getRandomPersons(randomCount);

        // then
        assertThat(randomIdsCaptor.getValue()).hasSize(randomCount);
    }

    @Test
    public void shouldReturnLessSizeWhenDBIdsLessThenCount() {
        // given
        List<Long> givenIds = Arrays.asList(1L, 2L, 3L);
        int randomCount = 5;
        when(queryRepository.getPersonIds()).thenReturn(givenIds);
        when(queryRepository.getPersonsDataAndThumbnails(randomIdsCaptor.capture())).thenReturn(null);

        // when
        List<?> foundRecords = personService.getRandomPersons(randomCount);

        // then
        assertThat(randomIdsCaptor.getValue()).hasSize(givenIds.size());
    }

    @Test
    void shouldReturnDifferentIdsSet() {
        // given
        List<Long> givenIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L,13L,14L,15L,16L,17L,18L,19L,20L);
        int randomCount = 5;
        when(queryRepository.getPersonIds()).thenReturn(givenIds);
        when(queryRepository.getPersonsDataAndThumbnails(randomIdsCaptor.capture())).thenReturn(null);

        // when
        List<?> foundRecordsFirst = personService.getRandomPersons(randomCount);
        Set<Long> idsFirst = randomIdsCaptor.getValue();
        List<?> foundRecordsSecond = personService.getRandomPersons(randomCount);
        Set<Long> idsSecond = randomIdsCaptor.getValue();

        // then
        assertThat(idsFirst).isNotEqualTo(idsSecond);
    }

    private static Stream<Arguments> parametersForInputArray() {
        return Stream.of(
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L))
        );
    }
}
