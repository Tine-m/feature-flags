package dk.ek;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;

import java.util.Map;

public class App {
    public static Javalin create() {
        var app = Javalin.create();
        app.get("/api/health", ctx -> ctx.json(Map.of("status", "UP")));
        app.get("/api/orders/preview", ctx -> ctx.json(Map.of("total", 200)));
        var discounts = new DiscountService();
        app.post("/api/orders/discount-preview", ctx -> {
            DiscountRequest request;
            try {
                request = ctx.bodyAsClass(DiscountRequest.class);
            } catch (Exception exception) {
                throw new BadRequestResponse("Request skal indeholde gyldig JSON.");
            }
            if (request == null) throw new BadRequestResponse("Rabatkode mangler.");
            ctx.json(discounts.preview(request.code()));
        });
        return app;
    }

    public record DiscountRequest(String code) {
    }
}
