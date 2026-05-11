
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class LoginController {// Controller class for the login window
    @FXML  // username input field from fxml
    private TextField userNameField;

    @FXML  // password input field from fxml
    private PasswordField passwordField;

    @FXML // label to show the error message
    private Label errorLabel;

    @FXML //handle closing the application
    private void handleClose() {
        Platform.exit();
        System.exit(0);
    }

    @FXML //handle login button action
    private void handleLogin() {//get username/password
        String userName = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        //check if username or password is empty, if so display the error message
        if (userName.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        //check if login is valid, if so display new welcome window
        if (UsersApp.isValidLogin(userName, password)) {
            errorLabel.setText("");
            UsersApp.openWelcomeWindow();

        } else {
            //if login is not valid show the error message
            errorLabel.setText("Invalid username or password.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
}