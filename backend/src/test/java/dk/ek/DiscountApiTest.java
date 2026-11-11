package dk.ek;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DiscountApiTest {
    private final ObjectMapper json = new ObjectMapper();

    private HttpResponse<String> request(boolean enabled, String path, String body) throws Exception {
        var app = App.create(new FeatureFlags(enabled)).start(0);
        try {
            var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + path));
            if (body != null)
                builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body));
            return HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } finally {
            app.stop();
        }
    }

    @Test
    void configurationDefaultsToOff() {
        assertFalse(FeatureFlags.fromEnvironment(Map.of()).discountCodes());
        assertFalse(FeatureFlags.fromEnvironment(Map.of("FEATURE_DISCOUNT_CODES", "nonsense")).discountCodes());
        assertTrue(FeatureFlags.fromEnvironment(Map.of("FEATURE_DISCOUNT_CODES", "true")).discountCodes());
    }

    @Test
    void exposesBothFlagStates() throws Exception {
        for (boolean enabled : new boolean[]{false, true}) {
            var response = request(enabled, "/api/features", null);
            assertEquals(200, response.statusCode());
            assertEquals(enabled, json.readTree(response.body()).get("discountCodes").asBoolean());
        }
    }

    @Test
    void disabledRejectsDirectCall() throws Exception {
        assertEquals(403, request(false, "/api/orders/discount-preview", "{\"code\":\"STUDENT10\"}").statusCode());
    }

    @Test
    void validCodeCalculatesDiscount() throws Exception {
        var response = request(true, "/api/orders/discount-preview", "{\"code\":\"STUDENT10\"}");
        assertEquals(200, response.statusCode());
        var result = json.readTree(response.body());
        assertEquals(200, result.get("originalTotal").intValue());
        assertEquals(20, result.get("discount").intValue());
        assertEquals(180, result.get("total").intValue());
    }

    @Test
    void invalidMissingAndMalformedCodesAreRejected() throws Exception {
        for (String body : new String[]{"{\"code\":\"WRONG\"}", "{}", "not-json"})
            assertEquals(400, request(true, "/api/orders/discount-preview", body).statusCode());
    }
}
