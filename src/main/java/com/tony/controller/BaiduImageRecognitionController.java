package com.tony.controller;

import com.tony.utils.BaiduRecognitionUtil;
import com.tony.utils.JsonFormatUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Executors;


/**
 * @author jiangwj20966 2018/9/14
 */
public class BaiduImageRecognitionController {

    private TranslatePicture translatePicture;

    private Stage primaryStage;

    @FXML
    private TextField fileLocation;
    @FXML
    private ImageView imageView;
    @FXML
    private TextArea textContent;
    @FXML
    private Label loadingText;

    private File chosenFile;

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );


        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            chosenFile = file;
            fileLocation.setText(chosenFile.getAbsolutePath());
            if (chosenFile.canRead()) {
                try (
                        FileInputStream fileInputStream = new FileInputStream(chosenFile);
                ) {
                    Image image = new Image(fileInputStream);
                    imageView.setImage(image);
                } catch (Exception e) {

                }

            }
        }
    }

    @FXML
    private void startRecognition() {
        if (chosenFile != null && chosenFile.canRead()) {
            loadingText.setVisible(true);
            textContent.setWrapText(false);
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    String result = BaiduRecognitionUtil.getPicContent(chosenFile);
                    if (result == null) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("请检查百度api相关配置，点击EDIT菜单修改");

                            alert.showAndWait();
                        });
                    } else {
                        Platform.runLater(() -> {
                            JsonFormatUtil jsonFormatUtil = new JsonFormatUtil();

                            textContent.setText(jsonFormatUtil.formatJSONString(result));
                        });
                    }
                    Platform.runLater(() -> {
                        loadingText.setVisible(false);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 单击图片后展示图片大图，暂时不可缩放
     */
    @FXML
    private void showBigPicture() {
        Dialog dialog = new Dialog();
        ImageView imageView = new ImageView();
        if (chosenFile != null) {
            try (FileInputStream fileInputStream = new FileInputStream(chosenFile)) {
                imageView.setImage(new Image(fileInputStream));

                imageView.setSmooth(true);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);

                scrollPane.setContent(imageView);

                dialog.getDialogPane().setMaxHeight(600);
                dialog.getDialogPane().setMaxWidth(600);

                dialog.getDialogPane().setContent(scrollPane);

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                dialog.setResizable(true);
                dialog.showAndWait();
            } catch (Exception e) {

            }
        }
    }


    public void setTranslatePicture(TranslatePicture translatePicture) {
        this.translatePicture = translatePicture;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
}
