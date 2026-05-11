//note to self: this is the main file
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class UsersApp {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        File inputfile = new File("users.txt");
        File outputfile = new File("out.txt");

        try (
                Scanner reader = new Scanner(inputfile);
                PrintWriter writer = new PrintWriter(outputfile)
        ) {

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                String[] parts = trimmed.split("\\s+");

                if (parts.length != 2) {
                    System.err.println(line);
                    System.err.println("Please enter a valid Email as username");
                    continue;
                }

                String username = parts[0];
                String password = parts[1];

                try {
                    User user = new User(username, password);
                    users.add(user);
                } catch (IllegalArgumentException e) {
                    System.err.println(line);
                    System.err.println(e.getMessage());
                }
            }

            Collections.sort(users);

            for (User user : users) {
                writer.println(user);
            }

        } catch (FileNotFoundException e) {
        }
    }
}