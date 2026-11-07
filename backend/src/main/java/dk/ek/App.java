package dk.ek;

import io.javalin.Javalin;
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
        return app;
    }
}
