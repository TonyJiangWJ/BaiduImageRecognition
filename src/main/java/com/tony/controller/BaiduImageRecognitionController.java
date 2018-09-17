package com.tony.controller;

import com.alibaba.fastjson.JSON;
import com.tony.utils.BaiduRecognitionUtil;
import com.tony.utils.JsonFormatUtil;
import com.tony.utils.ResultResolver;
import com.tony.utils.ScreenCapture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @FXML
    private CheckBox showFullText;
    @FXML
    private CheckBox segmenterCheckbox;

    private File chosenFile;

    private volatile byte[] captureImageArray;

    private String tempPrettyJSON;

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
            captureImageArray = null;
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
        if (chosenFile != null && chosenFile.canRead() || captureImageArray != null) {
            loadingText.setVisible(true);
            textContent.setWrapText(true);
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    String result;
                    if (captureImageArray != null) {
                        result = BaiduRecognitionUtil.getPicContent(captureImageArray);
                    } else {
                        result = BaiduRecognitionUtil.getPicContent(chosenFile);
                    }
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
                            this.tempPrettyJSON = JsonFormatUtil.prettyJson(result);
                            this.fullTextChanged();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (captureImageArray != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(captureImageArray);
            imageView.setImage(new Image(byteArrayInputStream));
        }

        if (chosenFile != null || captureImageArray != null) {
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
        }
    }

    /**
     * 截图
     */
    @FXML
    private void captureScreen() {
        primaryStage.setIconified(true);
        int flag = 10;
        // 等待200毫秒 避免窗口未完全最小化
        while (flag-- > 0) {
            // wait iconified
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ScreenCapture screenCapture = new ScreenCapture();
        screenCapture.captureRectangle();
        BufferedImage bufferedImage = screenCapture.getPickedImage();
        try (
                ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        ) {
            ImageIO.write(bufferedImage, "PNG", imageOutputStream);
            ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageOutputStream.toByteArray());
            captureImageArray = imageOutputStream.toByteArray();
            imageView.setImage(new Image(imageInputStream));
            imageInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setIconified(false);
    }

    @FXML
    private void fullTextChanged() {
        if (tempPrettyJSON != null) {
            // 是否只显示执行识别之后的文本
            if (showFullText.isSelected()) {
                String fullContent = ResultResolver.getFullText(JSON.parseObject(tempPrettyJSON));
                // 是否开启分词
                if (segmenterCheckbox.isSelected()) {
                    fullContent = ResultResolver.getSegmenterString(fullContent);
                }
                textContent.setText(fullContent);
            } else {
                textContent.setText(tempPrettyJSON);
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
