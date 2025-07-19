package org.demkiv.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.ValidateCaptchaForm;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
public class PersonServiceTest {

    @Mock
    SaveUpdateService<PersonForm, Optional<?>> service;
    @Mock
    QueryRepository queryRepository;
    @Mock
    EntitySender<Boolean, EmailModel> emailSenderMock;

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

    @Test
    void whenCheckCaptchaIsGenerated_shouldHaveLengthTen() {
        // given
        long givenId = 1L;
        ArgumentCaptor<Map<String, Object>> sessionMapCaptor = ArgumentCaptor.forClass(Map.class);
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        doNothing().when(session).setAttribute(eq("captcha"), sessionMapCaptor.capture());
        when(emailSenderMock.send(any())).thenReturn(true);

        // when
        boolean result = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);
        boolean result2 = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);
        boolean result3 = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);
        boolean result4 = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);
        boolean result5 = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);

        // then
        Set<Map<String, Object>> givenSet = new HashSet<>(sessionMapCaptor.getAllValues());
        assertThat(givenSet).size().isEqualTo(5);
        givenSet.forEach(entry -> {
            assertThat(entry).isNotNull();
            assertThat(entry.get("1").toString().length()).isEqualTo(10);
        });
    }

    @Test
    @Ignore
    void whenCheckSetCaptchaToHttpSessionAsMap() {
        // given
        long givenId = 1L;
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        ArgumentCaptor<String> captchaCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> sessionMapCaptor = ArgumentCaptor.forClass(Map.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        doNothing().when(session).setAttribute(captchaCaptor.capture(), sessionMapCaptor.capture());
        when(emailSenderMock.send(any())).thenReturn(true);

        // when
        boolean result = personService.generateCaptchaAndPushToSessionAndSendEmail(givenId, httpServletRequestMock);

        // then
        Map<String, Object> captchaMapCaptured = sessionMapCaptor.getValue();
        assertThat(result).isTrue();
        assertThat(captchaCaptor.getValue()).isEqualTo("captcha");
        assertThat(captchaMapCaptured).hasSize(1);
        assertThat(captchaMapCaptured.get("1")).isNotNull();
    }

    @Test
    @Ignore
    void whenCheckCaptchaMapIsAlreadyInHttpSession_shouldPopulateMapWithNewIdAndCaptcha() {
        // given
        Map<String, Object> givenCaptchaMap = Map.of("1", "0123456789");
        ArgumentCaptor<Map<String, Object>> sessionMapCaptor = ArgumentCaptor.forClass(Map.class);
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        when(session.getAttribute("captcha")).thenReturn(givenCaptchaMap);
        doNothing().when(session).setAttribute(eq("captcha"), sessionMapCaptor.capture());
        when(emailSenderMock.send(any())).thenReturn(true);

        // when
        boolean result = personService.generateCaptchaAndPushToSessionAndSendEmail(2L, httpServletRequestMock);

        // then
        Map<String, Object> captchaMap = sessionMapCaptor.getValue();
        assertThat(result).isTrue();
        assertThat(captchaMap).hasSize(2);
        assertThat(captchaMap.get("1")).isEqualTo("0123456789");
        assertThat(captchaMap.get("2").toString().length()).isEqualTo(10);
    }

    @Test
    @Ignore
    void whenCheckCaptchaInSessionWithSameId_shouldReplaceCaptchaMessage() {
        // given
        Map<String, Object> givenCaptchaMap = Map.of("1", "0123456789");
        ArgumentCaptor<Map<String, Object>> sessionMapCaptor = ArgumentCaptor.forClass(Map.class);
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        when(session.getAttribute("captcha")).thenReturn(givenCaptchaMap);
        doNothing().when(session).setAttribute(eq("captcha"), sessionMapCaptor.capture());
        when(emailSenderMock.send(any())).thenReturn(true);

        // when
        boolean result = personService.generateCaptchaAndPushToSessionAndSendEmail(1L, httpServletRequestMock);

        // then
        Map<String, Object> captchaMap = sessionMapCaptor.getValue();
        assertThat(result).isTrue();
        assertThat(captchaMap).hasSize(1);
        assertThat(captchaMap.get("1")).isNotEqualTo("0123456789");
        assertThat(captchaMap.get("1").toString().length()).isEqualTo(10);
    }

    @Test
    void whenCheckCaptchaValidation_shouldBeEqual() {
        // given
        ValidateCaptchaForm givenCaptchaForm = ValidateCaptchaForm.builder()
                .personId("1")
                .captcha("0123456789")
                .build();
        Map<String, Object> givenCaptchaMap = Map.of("1", "0123456789");
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        when(session.getAttribute("captcha")).thenReturn(givenCaptchaMap);

        // when
        boolean result = personService.getCaptchaFromSessionAndValidate(givenCaptchaForm, httpServletRequestMock);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void whenCheckCaptchaValidation_shouldBeNotEqual() {
        // given
        ValidateCaptchaForm givenCaptchaForm = ValidateCaptchaForm.builder()
                .personId("1")
                .captcha("0123456766")
                .build();
        Map<String, Object> givenCaptchaMap = Map.of("1", "0123456789");
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        when(session.getAttribute("captcha")).thenReturn(givenCaptchaMap);

        // when
        boolean result = personService.getCaptchaFromSessionAndValidate(givenCaptchaForm, httpServletRequestMock);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void whenCheckSessionDoesNotContainCaptcha_shouldBeFalse() {
        // given
        ValidateCaptchaForm givenCaptchaForm = ValidateCaptchaForm.builder()
                .personId("1")
                .captcha("0123456789")
                .build();
        HttpServletRequest httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(httpServletRequestMock.getSession()).thenReturn(session);
        when(session.getAttribute("captcha")).thenReturn(null);

        // when
        boolean result = personService.getCaptchaFromSessionAndValidate(givenCaptchaForm, httpServletRequestMock);

        // then
        assertThat(result).isFalse();
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
