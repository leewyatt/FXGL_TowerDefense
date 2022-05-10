package com.leewyatt.td.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class LevelStartScene extends SubScene {

    private static final int paneWidth = 501;
    private static final int paneHeight = 341;
    private final ImageButton playBtn;
    private final Pane contentPane;
    private final ImageView previewImg;

    public LevelStartScene() {
        Label levelLabel = new Label();
        levelLabel.setFont(Font.font(null, FontWeight.BOLD, FontPosture.REGULAR, 15));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.textProperty().bind(FXGL.getip("level").asString());
        levelLabel.setLayoutX(130);
        levelLabel.setLayoutY(207);

        Label hpLabel = new Label();
        hpLabel.setFont(Font.font(null, FontWeight.BOLD, FontPosture.REGULAR, 15));
        hpLabel.setTextFill(Color.WHITE);
        hpLabel.textProperty().bind(FXGL.getip("enemyHp").asString());
        hpLabel.setLayoutX(130);
        hpLabel.setLayoutY(240);
        previewImg = new ImageView();
        StackPane imgPane = new StackPane(previewImg);
        imgPane.setPrefSize(138, 102);
        imgPane.setLayoutX(75);
        imgPane.setLayoutY(77);
        playBtn = new ImageButton("hexMenu/start", 97, 90, () -> FXGL.getSceneService().popSubScene());
        playBtn.setPrefSize(97, 90);
        playBtn.setLayoutX(325);
        playBtn.setLayoutY(125);
        contentPane = new Pane(playBtn, imgPane, levelLabel, hpLabel);
        contentPane.setMaxSize(paneWidth, paneHeight);
        contentPane.setStyle("""
                -fx-background-image: url('assets/textures/ui/scene/startScene.png');
                """);

        StackPane root = new StackPane(contentPane);
        root.setStyle("-fx-background-color: #0005");
        root.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        getContentRoot().getChildren().setAll(root);
        contentPane.setScaleX(0.3);
        contentPane.setScaleY(0.3);
    }

    @Override
    public void onCreate() {
        ScaleTransition tt = new ScaleTransition(Duration.seconds(1.38), contentPane);
        tt.setInterpolator(Interpolators.BOUNCE.EASE_OUT());
        tt.setToX(1.0);
        tt.setToY(1.0);
        tt.play();
        String enemyPreview = FXGL.gets("enemyPreview");
        previewImg.setImage(enemyPreview.isEmpty()?null:FXGL.image(enemyPreview));
    }

    @Override
    public void onDestroy() {
        contentPane.setScaleX(0.3);
        contentPane.setScaleY(0.3);
    }
}
