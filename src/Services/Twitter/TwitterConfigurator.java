package Services.Twitter;

import Common.TwitterManager;
import Services.Twitter.Data.TwitterConfig;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by nsifniotis on 6/10/15.
 * 
 * Little GUI for adding, editing and removing twit config objects.
 */
public class TwitterConfigurator extends Application
{
    private ChoiceBox<TwitterConfig> selector;
    private TwitterConfig current_config;
    private TextField account_name;
    private TextField consumer_key;
    private TextField consumer_secret;
    private TextField access_token;
    private TextField access_token_secret;


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Button new_button = new Button ("New");
        new_button.setOnAction(e -> new_button_handler());
        Button edit_button = new Button ("Edit");
        edit_button.setOnAction(e -> edit_button_handler());
        selector = new ChoiceBox<>();
        HBox first_row = new HBox();
        first_row.setSpacing(20);
        first_row.getChildren().addAll(new_button, selector, edit_button);

        account_name = new TextField();
        access_token = new TextField();
        access_token_secret = new TextField();
        consumer_key = new TextField();
        consumer_secret = new TextField();

        HBox second_row = new HBox();
        second_row.setSpacing(10);
        second_row.getChildren().addAll(new Label("Account Name:"), account_name);
        HBox third_row = new HBox();
        third_row.setSpacing(10);
        third_row.getChildren().addAll(new Label("Consumer Key:"), consumer_key);
        HBox fourth_row = new HBox();
        fourth_row.setSpacing(10);
        fourth_row.getChildren().addAll(new Label("Consumer Secret:"), consumer_secret);
        HBox fifth_row = new HBox();
        fifth_row.setSpacing(10);
        fifth_row.getChildren().addAll(new Label("Access Token:"), access_token);
        HBox sixth_row = new HBox();
        sixth_row.setSpacing(10);
        sixth_row.getChildren().addAll(new Label("Access Token Secret:"), access_token_secret);

        HBox last_row = new HBox ();
        last_row.setSpacing(20);
        Button save_button = new Button ("Save");
        save_button.setOnAction(e -> save_button_handler());
        Button test_button = new Button ("Send Test Tweet");
        test_button.setOnAction(e -> test_button_handler());
        last_row.getChildren().addAll(save_button, test_button);

        VBox master_layout = new VBox();
        master_layout.getChildren().addAll(first_row, second_row, third_row, fourth_row, fifth_row, sixth_row, last_row);

        Scene scene = new Scene(master_layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Twitter Account Configuration");
        primaryStage.show();

        update_selector();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Update the selector with the list of current twitter configs.
     */
    private void update_selector()
    {
        selector.getItems().clear();

        for (TwitterConfig tc: Repository.GetTwitterConfigs())
            selector.getItems().add(tc);
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Fuck you and your comments
     */
    public void test_button_handler()
    {
        TwitterManager.SendTweet("Test tweet being sent to account " + this.current_config.AccountName());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Twitter Test");
        alert.setContentText("Test tweet sent - check the twitter account to see if it worked.");
        alert.showAndWait();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Event handler for New .. button click
     */
    private void new_button_handler()
    {
        this.current_config = Repository.NewConfig();
        this.update_selector();
        this.update_fields();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Event handler for the Edit button click.
     */
    private void edit_button_handler()
    {
        if (this.selector.getSelectionModel().getSelectedItem() == null)
            return;

        this.current_config = this.selector.getSelectionModel().getSelectedItem();
        this.update_fields();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Event handler for the save button ...
     */
    private void save_button_handler()
    {
        this.current_config.SetAccountName(this.account_name.getText());
        this.current_config.SetAccessToken(this.access_token.getText());
        this.current_config.SetAccessTokenSecret((this.access_token_secret.getText()));
        this.current_config.SetConsumerKey(this.consumer_key.getText());
        this.current_config.SetConsumerSecret(this.consumer_secret.getText());
        Repository.SaveConfig(this.current_config);

        update_selector();
    }


    /**
     * Nick Sifniotis u5809912
     * 06/10/2015
     *
     * Repopulate the text fields in the gui with the current configuration values..
     */
    private void update_fields()
    {
        this.account_name.setText(this.current_config.AccountName());
        this.consumer_secret.setText(this.current_config.ConsumerSecret());
        this.consumer_key.setText(this.current_config.ConsumerKey());
        this.access_token.setText(this.current_config.AccessToken());
        this.access_token_secret.setText(this.current_config.AccessTokenSecret());
    }
}
