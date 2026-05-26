package com.wms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserAgentParser 测试")
class UserAgentParserTest {

    @Test
    @DisplayName("应解析 Chrome on Windows")
    void shouldParseChromeOnWindows() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

        UserAgentParser.ParsedUserAgent result = UserAgentParser.parse(userAgent);

        assertEquals("Chrome 124", result.browser());
        assertEquals("Windows 10", result.os());
    }

    @Test
    @DisplayName("应解析 Safari on iPhone")
    void shouldParseSafariOnIPhone() {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4 like Mac OS X) AppleWebKit/605.1.15 "
                + "(KHTML, like Gecko) Version/17.4 Mobile/15E148 Safari/604.1";

        UserAgentParser.ParsedUserAgent result = UserAgentParser.parse(userAgent);

        assertEquals("Safari 17", result.browser());
        assertEquals("iOS 17", result.os());
    }

    @Test
    @DisplayName("空 UA 应返回 Unknown")
    void shouldReturnUnknownWhenUserAgentIsBlank() {
        UserAgentParser.ParsedUserAgent result = UserAgentParser.parse("  ");

        assertEquals("Unknown", result.browser());
        assertEquals("Unknown", result.os());
    }
}
