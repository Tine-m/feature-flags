package dk.ek;

public class Main {
    public static void main(String[] args) {
        App.create(FeatureFlags.fromEnvironment(System.getenv())).start(7070);
    }
}
