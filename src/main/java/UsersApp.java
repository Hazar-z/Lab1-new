//note to self: this is the main file

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UsersApp extends Application {

    private static ArrayList<User> users = new ArrayList<>();
    private static Stage loginStage;

    //store the valid users loaded from the file
    public static ArrayList<User> loadUsersFromFile(String fileName) {
        ArrayList<User> users = new ArrayList<>();
        File inputfile = new File(fileName); //open the input file

        try (Scanner reader = new Scanner(inputfile)) { //read the file

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String trimmed = line.trim();

                if (trimmed.isEmpty()) { //if the line empty skip it
                    continue;
                }

                //split the username and passwords
                String[] parts = trimmed.split("\\s+");
                //if the line does not contain two parts, its invalid display error message
                if (parts.length != 2) {
                    System.err.println(line);
                    System.err.println("Please enter a valid Email as username");
                    continue;
                }

                String username = parts[0];
                String password = parts[1];

                try {// creat username only if username and password are valid
                    User user = new User(username, password);
                    users.add(user);
                } catch (IllegalArgumentException e) { //print original invalid lines and error message
                    System.err.println(line);
                    System.err.println(e.getMessage());
                }
            }

        } catch (FileNotFoundException e) { //if the file  can not be opened throw exeption
            System.err.println("Could not open users.txt");
            e.printStackTrace();

        }

        return users;
    }

    //check if the enterd username and password exist in the valid list
    public static boolean isValidLogin(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;//else return false
    }

    //if the user+password is valid  and they match successfully open welcome window
    public static void openWelcomeWindow() {
        try {
            Parent root = FXMLLoader.load(UsersApp.class.getResource("/Welcome.fxml"));
            Scene welcomeScene = new Scene(root, 300, 180);
            Stage welcomeStage = new Stage();

            welcomeStage.setTitle("Welcome");
            welcomeStage.setScene(welcomeScene);

            //closing welcom window close the whole program
            welcomeStage.setOnCloseRequest(UsersApp::closeApplication);
            welcomeStage.show();

            if (loginStage != null) { //hide login window when  login succeeds
                loginStage.hide();
            }

        } catch (Exception e) {  // throw exception if welcome window cannot open
            System.out.println("Could not open welcome window: " + e.getMessage());

        }
    }
    // closes the whole application
    private static void closeApplication(WindowEvent event) {
        Platform.exit();
        System.exit(0);
    }

    // starts the JavaFX application
    @Override
    public void start(Stage primaryStage) throws Exception {

        // loads valid users before showing login screen
        users = loadUsersFromFile("users.txt");

        // loads the login FXML screen
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Scene scene = new Scene(root, 360, 260);

        loginStage = primaryStage;
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);

        // Closing login window closes the whole app
        primaryStage.setOnCloseRequest(UsersApp::closeApplication);

        primaryStage.show();
    }

    // Program starts here
    public static void main(String[] args) {
        launch(args);
    }
}