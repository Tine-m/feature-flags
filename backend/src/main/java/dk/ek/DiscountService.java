package dk.ek;

import io.javalin.http.BadRequestResponse;
import java.math.BigDecimal;

public class DiscountService {
    public record Preview(BigDecimal originalTotal, BigDecimal discount, BigDecimal total) {}
    public Preview preview(String code) {
        if (!"STUDENT10".equals(code)) throw new BadRequestResponse("Ugyldig rabatkode.");
        // Demo: serveren ejer prisen. Beløb beregnes med BigDecimal.
        var original = new BigDecimal("200.00");
        var discount = original.multiply(new BigDecimal("0.10"))
            .setScale(2, java.math.RoundingMode.HALF_UP);
        return new Preview(original, discount, original.subtract(discount));
    }
}
