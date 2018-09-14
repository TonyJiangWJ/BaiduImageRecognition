package com.tony.controller;

import com.tony.utils.BaiduRecognitionUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Optional;
/**
 * @author jiangwj20966 2018/9/14
 */
public class TranslatePicture extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("百度识图");
        initRootLayout();
        initContainer();

    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TranslatePicture.class.getResource("/view/RootLayout.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initContainer() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TranslatePicture.class.getResource("/view/BaiduImageRecognition.fxml"));
            Pane mainContainer = loader.load();
            rootLayout.setCenter(mainContainer);

            BaiduImageRecognitionController controller = loader.getController();
            controller.setTranslatePicture(this);
            controller.setStage(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeConfigs() {
        // Create the custom dialog.
        Dialog<Config> dialog = new Dialog<>();
        dialog.setTitle("Config Dialog");
        dialog.setHeaderText("修改百度API配置信息");

        // Set the button types.
        ButtonType modifyButtonType = new ButtonType("修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 10));

        TextField apiUrl = new TextField();
        apiUrl.setPromptText("识别接口");
        apiUrl.setText(BaiduRecognitionUtil.getGenerateUrl());
        apiUrl.setMinWidth(350);

        TextField appKey = new TextField();
        appKey.setText(BaiduRecognitionUtil.getAppKey());
        appKey.setPromptText("appKey");

        TextField appSecret = new TextField();
        appSecret.setPromptText("appSecret");
        appSecret.setText(BaiduRecognitionUtil.getAppSecret());

        grid.add(new Label("识别接口:"), 0, 0);
        grid.add(apiUrl, 1, 0);
        grid.add(new Label("appKey:"), 0, 1);
        grid.add(appKey, 1, 1);
        grid.add(new Label("appSecret:"), 0, 2);
        grid.add(appSecret, 1, 2);


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> apiUrl.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifyButtonType) {
                return new Config(apiUrl.getText(), appKey.getText(), appSecret.getText());
            }
            return null;
        });

        Optional<Config> result = dialog.showAndWait();

        result.ifPresent(returnedValue -> {
            System.out.println("apiUrl=" + returnedValue.getApiUrl() + ", appKey=" + returnedValue.getAppKey() + ", appSecret=" + returnedValue.getAppSecret());
            BaiduRecognitionUtil.setAppKey(returnedValue.getAppKey());
            BaiduRecognitionUtil.setAppSecret(returnedValue.getAppSecret());
            BaiduRecognitionUtil.setGenerateUrl(returnedValue.getApiUrl());
            BaiduRecognitionUtil.writeConfigIntoProperties();
        });
    }

    private class Config {
        private String apiUrl;
        private String appKey;
        private String appSecret;

        public Config(String apiUrl, String appKey, String appSecret) {
            this.apiUrl = apiUrl;
            this.appKey = appKey;
            this.appSecret = appSecret;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

}
