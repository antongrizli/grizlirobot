package com.twitter.robot.controller;

import com.google.common.eventbus.EventBus;
import com.twitter.robot.activity.TwitterAuth;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    private TextField txtfldLogin;
    @FXML
    private PasswordField txtfldPass;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLogout;
    @FXML
    private Label lWarning;
    @FXML
    private Stage stage;
    @FXML
    private Button notify;
    private Twitter twitter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!MainController.netIsAvailable()) {
            btnLogin.setDisable(true);
            btnLogout.setDisable(true);
            txtfldLogin.setDisable(true);
            txtfldPass.setDisable(true);

            lWarning.setText("No Internet access!");
            lWarning.setStyle("-fx-text-fill: red");
        }

    }

    public void initData(Stage stage, boolean connect, String screenName) {
        this.stage = stage;
        this.stage.setTitle(String.format("You authorized as: %s", screenName));
        if (connect) {
            btnLogin.setDisable(true);
            btnLogout.setDisable(false);
            txtfldLogin.setDisable(true);
            txtfldPass.setDisable(true);

            lWarning.setText(String.format("You authorized as: %s", screenName));
            lWarning.setStyle("-fx-text-fill: black");
        }
    }

    public void login(ActionEvent actionEvent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    TwitterAuth auth = new TwitterAuth();
                    String s = null;
                    if (txtfldLogin.getText().length() >= 0 || txtfldPass.getText().length() >= 0) {
                        twitter = auth.getTwitter(txtfldLogin.getText(), txtfldPass.getText());
                        if (auth.isConnect()) {
                            btnLogin.setDisable(true);
                            btnLogout.setDisable(false);
                            txtfldLogin.setDisable(true);
                            txtfldPass.setDisable(true);
                            s = String.format("You authorized as: %s", twitter.getScreenName().toString());
                            lWarning.setText(s);
                            lWarning.setStyle("-fx-text-fill: black");
                            stage.setTitle(s);

                            // User ID acquired from a textbox called txt_user_id

                            /*EventBus eventBus = new EventBus();
                            eventBus.register(new MainController());
                            System.out.println("post twitter");
                            eventBus.post(twitter);
                            eventBus.post(true);*/


                        }
                    } else {
                        s = String.format("You input incorrect email or password!");
                        lWarning.setText(s);
                        lWarning.setStyle("-fx-text-fill: red");
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logout(ActionEvent actionEvent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Properties prop = new Properties();
                OutputStream output = null;
                try {
                    output = new FileOutputStream("config.properties");
                    // set the properties value
                    prop.setProperty("token", "");
                    prop.setProperty("secret_token", "");
                    // save properties to project root folder
                    prop.store(output, null);

                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                btnLogin.setDisable(false);
                btnLogout.setDisable(true);
                txtfldLogin.setDisable(false);
                txtfldPass.setDisable(false);
                lWarning.setVisible(false);
                stage.setTitle("Please authorization!");

                EventBus eventBus = new EventBus();
                eventBus.register(new MainController());
                System.out.println("post twitter null");
                eventBus.post(false);
            }

        });

    }

    public void initData(Stage stage, boolean connect) {
        this.stage = stage;
        this.stage.setTitle("Please authorization in your account");
        if (connect) {
            btnLogin.setDisable(true);
            btnLogout.setDisable(false);
            txtfldLogin.setDisable(true);
            txtfldPass.setDisable(true);
        }
    }

    public void press(ActionEvent actionEvent) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Look, a Custom Login Dialog");

        // Set the icon (must be included in the project).
        //  dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        ButtonType logoutButtonType = new ButtonType("Logout", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, logoutButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });

    }
}
