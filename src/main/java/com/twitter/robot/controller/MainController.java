package com.twitter.robot.controller;

import com.twitter.robot.activity.Frends;
import com.twitter.robot.activity.TwitterAuth;
import com.twitter.robot.model.tweet.*;
import com.twitter.robot.model.tweet.io.IOoperations;
import com.twitter.robot.write.WriteToXML;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import twitter4j.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneId;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private TextField txrfldFriends;
    @FXML
    private TextField txtfldFollowers;
    @FXML
    private DatePicker dpSince;
    @FXML
    private DatePicker dpUntil;
    @FXML
    private TextField txtfldSearch;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem miAuth;
    @FXML
    private MenuItem miClose;
    @FXML
    private Stage stage;
    @FXML
    private Parent root;
    @FXML
    private Button btnRun;
    @FXML
    private CheckBox chkbxCreateFriend;
    @FXML
    private CheckBox chkbxRetweet;
    @FXML
    private Tab tpStory;
    @FXML
    private DatePicker dpDelete;
    @FXML
    private CheckBox chkbxDeleteRetweet;
    @FXML
    private CheckBox chkbxDeleteFriend;
    @FXML
    private Button btnRunDelete;
    @FXML
    private Label lException;
    @FXML
    private TableView<Tweet> retweet;
    @FXML
    private TableColumn<Tweet, Date> colRetweetDate;
    @FXML
    private TableColumn<Tweet, String> colText;
    @FXML
    private TableColumn<Tweet, String> colUserName;
    @FXML
    private TableColumn<Tweet, String> colChek;

    private Twitter twitter;
    private TwitterAuth auth = new TwitterAuth();

    public void initialize(URL location, ResourceBundle resources) {
        twitter = auth.getTwitter();

        loadXmlToTableView();

        if (netIsAvailable()) {
            if (auth.isConnect()) {
                setField();
            } else {
                disabledAllElement(true);
                lException.setText("You need authorization to your twitter account!");
            }
        } else {
            disabledAllElement(true);
            lException.setText("No Internet access!");
        }
    }

    private void setField() {
        try {
            txrfldFriends.setText(String.valueOf(Frends.getCountFriends(twitter)));
            txtfldFollowers.setText(String.valueOf(Frends.getCountFollowers(twitter)));
            lException.setText(String.format("User name: %s", twitter.getScreenName()));
            lException.setStyle("-fx-text-fill: black");
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private void disabledAllElement(boolean visible) {
        txrfldFriends.setDisable(visible);
        txtfldFollowers.setDisable(visible);
        dpUntil.setDisable(visible);
        dpSince.setDisable(visible);
        txtfldSearch.setDisable(visible);
        btnRun.setDisable(visible);
        chkbxCreateFriend.setDisable(visible);
        chkbxRetweet.setDisable(visible);
        tpStory.setDisable(visible);
        dpDelete.setDisable(visible);
        chkbxDeleteRetweet.setDisable(visible);
        chkbxDeleteFriend.setDisable(visible);
        btnRunDelete.setDisable(visible);

    }

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("https://api.twitter.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    public void runSearch(ActionEvent actionEvent) {
        String query = null;

        if (txtfldSearch.getText() != null && txtfldSearch.getText().length() > 0) {
            query = txtfldSearch.getText();
            if (dpSince.getValue() != null) {
                query = String.format("%s since:%s",
                        txtfldSearch.getText(), dpSince.getValue());
            }
            if (dpUntil.getValue() != null) {
                query = String.format("%s until:%s",
                        txtfldSearch.getText(), dpUntil.getValue());
            }
            if (dpSince.getValue() != null && dpUntil.getValue() != null && dpSince.getValue().isBefore(dpUntil.getValue())) {
                query = String.format("%s since:%s until:%s",
                        txtfldSearch.getText(), dpSince.getValue(), dpUntil.getValue());
            }
            if (query != null && query.length() > 0) {
                System.out.println(query);
                query(query);
                Platform.runLater(() -> {
                    loadXmlToTableView();
                });
            } else {
                lException.setText("Please set any query for search");
            }
        }

    }

    private void query(final String queryString) {
        try {
            Query query = new Query(queryString);
            QueryResult result;
            query.setCount(60);
            int count = 0;
            while (query != null) {
                result = twitter.search(query);
                System.out.println(result.getCount());
                List<Status> tweets = result.getTweets();
                Thread saveXML = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IOoperations.saveXml(tweets);
                    }
                });
                saveXML.setDaemon(true);
                saveXML.start();
                // if (WriteToXML.appendToXML(tweets) == -1) break;
                //WriteToXML.createXMLAndWrite(tweets);
                if (chkbxCreateFriend.isSelected() || chkbxRetweet.isSelected())
                    for (Status tweet : tweets) {
                        System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                        if (chkbxCreateFriend.isSelected()) {
                            Thread.sleep(1000);//1 second
                            twitter.createFriendship(tweet.getUser().getScreenName());
                        }
                        if (chkbxRetweet.isSelected()) {
                            Thread.sleep(2000);//2 secunds
                            twitter.retweetStatus(tweet.getId());
                        }
                    }//all time 3 minutes
                query = result.nextQuery();
                //Thread.sleep(5 * 1000 * 60);//5 minutes
                count++;
                if (count * 60 > 900) break;
            }
            //Если в файле уже много записей, тогда нужно перейти на проверку актуальности
            //Не актульным считается твит который добавлен в XML больше 7 дней
            //Если такой найден, то нужно отписаться от человека и от твита

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void authorization(ActionEvent actionEvent) {
        if (actionEvent.getSource() == miAuth) {
            {
                HBox hbButtons = new HBox();
                hbButtons.setSpacing(10.0);

                Button loginBtn = new Button("Login");
                Button logoutBtn = new Button("Logout");
                loginBtn.setMinWidth(75);
                logoutBtn.setMinWidth(75);
                hbButtons.getChildren().addAll(loginBtn, logoutBtn);

                // Create the custom dialog.
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Login Dialog");
                dialog.setHeaderText("Please enter login and password");

                // Set the icon (must be included in the project).
                ImageView imageView = new ImageView("login.png");
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                dialog.setGraphic(imageView);

                // Set the button types.
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

                // Create the username and password labels and fields.
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField username = new TextField();
                username.setPromptText("Username");
                PasswordField password = new PasswordField();
                password.setPromptText("Password");

                Label warning = new Label();

                grid.add(new Label("Username:"), 0, 0);
                grid.add(username, 1, 0);
                grid.add(new Label("Password:"), 0, 1);
                grid.add(password, 1, 1);
                grid.add(warning, 1, 2);
                grid.add(hbButtons, 1, 5);

                // Enable/Disable login button depending on whether a username was entered.
                loginBtn.setDisable(true);
                if (auth.isConnect()) {
                    logoutBtn.setDisable(false);
                    username.setDisable(true);
                    password.setDisable(true);
                } else {
                    logoutBtn.setDisable(true);
                    username.setDisable(false);
                    password.setDisable(false);
                }

                // Do some validation (using the Java 8 lambda syntax).
                username.textProperty().addListener((observable, oldValue, newValue) -> {
                    loginBtn.setDisable(newValue.trim().isEmpty());
                });

                dialog.getDialogPane().setContent(grid);

                // Request focus on the username field by default.
                Platform.runLater(() -> username.requestFocus());

                loginBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Thread loginThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (username.getText() != null && password.getText() != null) {
                                    System.out.println("Username=" + username.getText() + ", Password=" + password.getText());
                                    twitter = auth.getTwitter(username.getText(), password.getText());
                                    disabledAllElement(false);
                                    setField();
                                    logoutBtn.setDisable(false);
                                    username.setDisable(true);
                                    password.setDisable(true);
                                    try {
                                        dialog.setTitle(String.format("Hello %s", twitter.getScreenName()));
                                    } catch (TwitterException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        loginThread.setDaemon(true);
                        loginThread.start();
                    }
                });

                logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Username=" + username.getText() + ", Password=" + password.getText());
                        Properties prop = new Properties();
                        OutputStream output = null;
                        auth.setConnect(false);
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
                            logoutBtn.setDisable(true);
                            username.setDisable(false);
                            password.setDisable(false);
                        }
                        disabledAllElement(true);
                        lException.setStyle("-fx-text-fill: red");
                        lException.setText("You need authorization to your twitter account!");
                    }
                });
                dialog.showAndWait();
            }
        }
    }

    public void close(ActionEvent actionEvent) {
        if (actionEvent.getSource() == miClose) {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.close();
        }
    }

    public void setTwitter(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        System.out.println(twitter.getScreenName());
    }

    public void setOnActionSince(ActionEvent actionEvent) {
        if (dpSince.getValue() != null && dpUntil.getValue() != null)
            dpUntil.setDisable(dpSince.getValue().isAfter(dpUntil.getValue()));
    }

    public void setOnActionUntil(ActionEvent actionEvent) {
        if (dpSince.getValue() != null && dpUntil.getValue() != null)
            dpSince.setDisable(dpSince.getValue().isAfter(dpUntil.getValue()));
    }

    public void runDelete(ActionEvent actionEvent) {
        if (dpDelete.getValue() != null) {
            Date date = Date.from(dpDelete.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            WriteToXML.deleteOldTweetXML(date);
        }
    }

    public void loadXmlToTableView() {
        ObservableList<Tweet> data = FXCollections.observableArrayList();
        for (Tweet loop : IOoperations.getXml()) {
            data.add(loop);
        }
        colRetweetDate.setCellValueFactory(new PropertyValueFactory<Tweet, Date>("tweetDate"));
        colText.setCellValueFactory(new PropertyValueFactory<Tweet, String>("text"));
        colUserName.setCellValueFactory(new PropertyValueFactory<Tweet, String>("userName"));
        colChek.setCellValueFactory(new PropertyValueFactory<Tweet, String>("userName"));
        colChek.setCellFactory(new Callback<TableColumn<Tweet, String>, TableCell<Tweet, String>>() {
            @Override
            public TableCell<Tweet, String> call(TableColumn<Tweet, String> param) {
                return new CheckBoxTableCell<Tweet, String>() {
                    {
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {


                        if (!empty) {
                            TableRow row = getTableRow();

                            if (row != null) {
                                int rowNo = row.getIndex();
                                TableView.TableViewSelectionModel sm = retweet.getSelectionModel();

                                //if ( item )  sm.select( rowNo );
                                //  if ( item )
                                sm.selectAll();
                                //  else
                                //    sm.clearSelection( rowNo );
                            }
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        colChek.setEditable(true);
        colChek.setMaxWidth( 50 );
        colChek.setMinWidth( 50 );
        retweet.setItems(data);
    }
}