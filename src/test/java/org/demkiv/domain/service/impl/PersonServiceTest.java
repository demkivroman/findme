package org.demkiv.domain.service.impl;

import org.demkiv.persistance.dao.QueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
public class PersonServiceTest {
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

    @Test
    void whenCheckCaptchaCodeGeneration() {
        // given
        int length = 10;

        // when
        String captchaCode_1 = personService.generateCaptcha();
        String captchaCode_2 = personService.generateCaptcha();
        String captchaCode_3 = personService.generateCaptcha();
        String captchaCode_4 = personService.generateCaptcha();
        String captchaCode_5 = personService.generateCaptcha();

        // then
        Set<String> captchaCodes = Set.of(captchaCode_1,  captchaCode_2, captchaCode_3, captchaCode_4, captchaCode_5);
        assertThat(captchaCodes).hasSize(5);
        assertThat(captchaCode_1.length()).isEqualTo(length);
        assertThat(captchaCode_2.length()).isEqualTo(length);
        assertThat(captchaCode_3.length()).isEqualTo(length);
        assertThat(captchaCode_4.length()).isEqualTo(length);
        assertThat(captchaCode_5.length()).isEqualTo(length);
    }
}
