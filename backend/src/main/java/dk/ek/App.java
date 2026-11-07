package dk.ek;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import java.util.Map;

public class App {
    public static Javalin create() {
        return create(new FeatureFlags(false));
    }

    public static Javalin create(FeatureFlags flags) {
        var app = Javalin.create();
        app.get("/api/health", ctx -> ctx.json(Map.of("status", "UP")));
        app.get("/api/orders/preview", ctx -> ctx.json(Map.of("total", 200)));
        app.get("/api/features", ctx -> ctx.json(flags));
        var discounts = new DiscountService();
        app.post("/api/orders/discount-preview", ctx -> {
            // Tjek før parsing: deaktiveret funktion afviser også direkte API-kald.
            if (!flags.discountCodes()) throw new ForbiddenResponse("Rabatkoder er ikke aktiveret.");
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
    public record DiscountRequest(String code) {}
}
