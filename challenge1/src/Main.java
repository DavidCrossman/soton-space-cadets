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
                3. Secure ECS Related People
                >\040""");

        String userInput;
        try {
            userInput = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (userInput) {
            case "1" -> ECSNameReader.run();
            case "2" -> SecureECSNameReader.run();
            case "3" -> SecureECSRelatedPeople.run();
            default -> System.out.println("Not an option");
        }
    }
}