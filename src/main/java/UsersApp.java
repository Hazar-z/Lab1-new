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

    private static int maxFailedAttempts = 3;
    private static int lockDurationSeconds = 60;

    public static int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public static int getLockDurationSeconds() {
        return lockDurationSeconds;
    }

    public static ArrayList<User> loadUsersFromFile(String fileName) {
        ArrayList<User> loaded = new ArrayList<>();
        File inputFile = new File(fileName);

        try (Scanner reader = new Scanner(inputFile)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                String[] parts = trimmed.split("\\s+", 2);

                if (parts.length != 2) {
                    System.err.println(line);
                    System.err.println("Please enter a valid Email as username");
                    continue;
                }

                String email = parts[0];
                String password = parts[1].trim();

                try {
                    loaded.add(new User(email, password));
                } catch (IllegalArgumentException e) {
                    System.err.println(line);
                    System.err.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not open users.txt");
            e.printStackTrace();
        }

        return loaded;
    }

    public static User findUserByLogin(String login) {
        if (login == null) {
            return null;
        }

        String key = login.trim();

        for (User user : users) {
            if (user.getEmail().equals(key) || user.getUsername().equals(key)) {
                return user;
            }
        }

        return null;
    }

    public static void startBlockedCheckThread(User user, LoginAttemptCallbacks callbacks) {
        CheckBlockedThread thread = new CheckBlockedThread(user, lockDurationSeconds);

        thread.start();

        if (thread.isAllowedToLogin()) {
            Platform.runLater(callbacks::onWelcome);
        } else {
            Platform.runLater(callbacks::onAccountLocked);
        }
    }

    public static void startFailedAttemptThread(User user, LoginAttemptCallbacks callbacks) {
        int before = user.getFailedAttempts();

        FailedAttemptsThread thread = new FailedAttemptsThread(user, maxFailedAttempts);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Platform.runLater(callbacks::onInvalidCredentials);
            return;
        }

        int after = user.getFailedAttempts();

        System.out.println("Username: " + user.getEmail());
        System.out.println("Failed attempts before: " + before);
        System.out.println("Failed attempts after: " + after);
        System.out.println("Max attempts: " + maxFailedAttempts);
        System.out.println("Blocked: " + user.isBlocked());
        System.out.println("Blocked time: " + user.getBlockedTime());

        if (user.isBlocked()) {
            Platform.runLater(callbacks::onLockoutWithWait);
        } else {
            Platform.runLater(callbacks::onInvalidCredentials);
        }
    }

    public static void dispatchLoginAttempt(String userName, String password, LoginAttemptCallbacks callbacks) {
        User user = findUserByLogin(userName);

        if (user == null) {
            Platform.runLater(callbacks::onInvalidCredentials);
            return;
        }

        if (user.isBlocked()) {
            long now = System.currentTimeMillis();
            long passedTime = now - user.getBlockedTime();
            long lockMillis = lockDurationSeconds * 1000L;

            if (passedTime >= lockMillis) {
                user.unlockUser();
            } else {
                Platform.runLater(callbacks::onAccountLocked);
                return;
            }
        }

        if (user.getPassword().equals(password)) {
            user.resetFailedAttempts();
            Platform.runLater(callbacks::onWelcome);
        } else {
            startFailedAttemptThread(user, callbacks);
        }
    }

    public static void openWelcomeWindow() {
        try {
            Parent root = FXMLLoader.load(UsersApp.class.getResource("/Welcome.fxml"));
            Scene welcomeScene = new Scene(root, 430, 600);
            Stage welcomeStage = new Stage();

            welcomeStage.setTitle("Welcome");
            welcomeStage.setScene(welcomeScene);
            welcomeStage.setOnCloseRequest(UsersApp::closeApplication);
            welcomeStage.show();

            if (loginStage != null) {
                loginStage.hide();
            }

        } catch (Exception e) {
            System.out.println("Could not open welcome window");
            e.printStackTrace();
        }
    }

    private static void closeApplication(WindowEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        users = loadUsersFromFile("users.txt");

        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Scene scene = new Scene(root, 430, 600);

        loginStage = primaryStage;
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(UsersApp::closeApplication);
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java UsersApp <n> <t>");
            System.exit(1);
        }

        try {
            maxFailedAttempts = Integer.parseInt(args[0]);
            lockDurationSeconds = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("n and t must be integers.");
            System.exit(1);
        }

        if (maxFailedAttempts < 1 || lockDurationSeconds < 1) {
            System.err.println("n and t must be positive.");
            System.exit(1);
        }

        launch(args);
    }

    public interface LoginAttemptCallbacks {
        void onWelcome();

        void onInvalidCredentials();

        void onAccountLocked();

        void onLockoutWithWait();

        void onLockoutEnded();
    }
}