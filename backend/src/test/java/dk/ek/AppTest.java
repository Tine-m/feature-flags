package dk.ek;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void existingOrderWorks() throws Exception {
        var app = App.create().start(0);
        try {
            var response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + "/api/orders/preview")).build(),
                HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("200"));
        } finally { app.stop(); }
    }
}
