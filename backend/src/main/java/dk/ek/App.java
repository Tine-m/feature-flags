package dk.ek;

import io.javalin.Javalin;

import java.util.Map;

public class App {
    public static Javalin create() {
        var app = Javalin.create();
        app.get("/api/health", ctx -> ctx.json(Map.of("status", "UP")));
        app.get("/api/orders/preview", ctx -> ctx.json(Map.of("total", 200)));
        return app;
    }
}
