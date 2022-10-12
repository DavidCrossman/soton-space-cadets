import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("""
                --Select--
                1. ECS Name Reader
                2. Secure ECS Name Reader
                >\040""");

        String input;
        try {
            input = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (input) {
            case "1" -> ECSNameReader.run();
            case "2" -> SecureECSNameReader.run();
            default -> System.out.println("Not an option");
        }
    }
}