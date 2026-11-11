package dk.ek;

import java.util.Map;

public record FeatureFlags(boolean discountCodes) {
    public static FeatureFlags fromEnvironment(Map<String, String> environment) {
        return new FeatureFlags(Boolean.parseBoolean(
                environment.getOrDefault("FEATURE_DISCOUNT_CODES", "false")));
    }
}
