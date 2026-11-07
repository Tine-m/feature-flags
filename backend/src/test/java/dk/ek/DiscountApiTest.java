package dk.ek;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

class DiscountApiTest {
    private final ObjectMapper json = new ObjectMapper();
    private HttpResponse<String> request(String path, String body) throws Exception {
        var app = App.create().start(0);
        try {
            var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + path));
            if (body != null) builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body));
            return HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } finally { app.stop(); }
    }
    @Test void validCodeCalculatesDiscount() throws Exception {
        var response = request("/api/orders/discount-preview", "{\"code\":\"STUDENT10\"}");
        assertEquals(200, response.statusCode());
        var result = json.readTree(response.body());
        assertEquals(200, result.get("originalTotal").intValue());
        assertEquals(20, result.get("discount").intValue());
        assertEquals(180, result.get("total").intValue());
    }
    @Test void invalidMissingAndMalformedCodesAreRejected() throws Exception {
        for (String body : new String[]{"{\"code\":\"WRONG\"}", "{}", "not-json"})
            assertEquals(400, request("/api/orders/discount-preview", body).statusCode());
    }
}
