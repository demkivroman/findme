package org.demkiv.domain.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class})
public class CaptchaCacheTest {

    private static final long ONE_MINUTE_IN_MILLISECONDS = 60 * 1000L;
    private CaptchaCache captchaCache;

    @BeforeEach
    void setUp() {
        captchaCache = CaptchaCache.getInstance();
    }

    @Test
    void whenCheckValidCaptcha() {
        // given
        String captchaKey_1 = "captchaKey_1";
        String captcha_1 = "captcha_1";
        String captchaKey_2 = "captchaKey_2";
        String captcha_2 = "captcha_2";
        String captchaKey_3 = "captchaKey_3";
        String captcha_3 = "captcha_3";

        // when
        captchaCache.put(captchaKey_1, captcha_1, ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.put(captchaKey_2, captcha_2, ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.put(captchaKey_3, captcha_3, ONE_MINUTE_IN_MILLISECONDS);

        // then
        assertEquals(captcha_1, captchaCache.get(captchaKey_1));
        assertEquals(captcha_2, captchaCache.get(captchaKey_2));
        assertEquals(captcha_3, captchaCache.get(captchaKey_3));
    }

    @Test
    void whenCheckNotGetExpiredCaptcha() {
        // given
        String captchaKey_1 = "captchaKey_1";
        String captcha_1 = "captcha_1";
        String captchaKey_2 = "captchaKey_2";
        String captcha_2 = "captcha_2";
        String captchaKey_3 = "captchaKey_3";
        String captcha_3 = "captcha_3";

        // when
        captchaCache.put(captchaKey_2, captcha_2, -ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.put(captchaKey_3, captcha_3, -ONE_MINUTE_IN_MILLISECONDS);

        // then
        assertEquals(captcha_1, captchaCache.get(captchaKey_1));
        assertNull(captchaCache.get(captchaKey_2));
        assertNull(captchaCache.get(captchaKey_3));
    }

    @Test
    void whenCheckClearExpiredCaptcha() {
        // given
        String captchaKey_1 = "captchaKey_1";
        String captcha_1 = "captcha_1";
        String captchaKey_2 = "captchaKey_2";
        String captcha_2 = "captcha_2";
        String captchaKey_3 = "captchaKey_3";
        String captcha_3 = "captcha_3";

        // when
        captchaCache.put(captchaKey_1, captcha_1, ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.put(captchaKey_2, captcha_2, -ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.put(captchaKey_3, captcha_3, -ONE_MINUTE_IN_MILLISECONDS);
        captchaCache.clearExpired();

        // then
        assertEquals(captcha_1, captchaCache.get(captchaKey_1));
        assertNull(captchaCache.get(captchaKey_2));
        assertNull(captchaCache.get(captchaKey_3));
    }
}
