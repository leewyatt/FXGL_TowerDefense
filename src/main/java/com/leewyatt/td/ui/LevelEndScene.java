package com.leewyatt.td.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.leewyatt.td.TowerDefenseApp;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 结束时候的场景:
 *    可与显示玩家的得分, 通关用时, 几星通关等;
 *    因为偷懒, 并没有计算积分, 只是显示按钮
 *    1. 回到主菜单按钮
 *    2. 如果通过,显示继续(下一关)按钮; 显示通过的图片
 *       如果没有通过,那么显示重玩 按钮; 显示未通过的图片
 *
 */
public class LevelEndScene extends SubScene {
    private static final int paneWidth = 575;
    private static final int paneHeight = 326;
    private final HBox menuBox;
    private final StackPane contentPane;

    public LevelEndScene() {
        //() -> executeAnimation(e->{动画完成后需要执行的代码})
        // 点击图片按钮后, 首先播放收起这个弹窗界面的动画,动画执行完成后,还需要执行跳转等行为
        ImageButton mainMenuBtn = new ImageButton("hexMenu/mainMenu", 128, 98, () -> executeAnimation(e -> {
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().gotoMainMenu();
        }));
        ImageButton restartBtn = new ImageButton("hexMenu/restart", 128, 98, () -> executeAnimation(e -> {
            FXGL.getSceneService().popSubScene();
            FXGL.<TowerDefenseApp>getAppCast().restartLevel();
        }));
        ImageButton continueBtn = new ImageButton("hexMenu/continue", 128, 98, () -> executeAnimation(e -> {
            FXGL.getSceneService().popSubScene();
            FXGL.<TowerDefenseApp>getAppCast().continueNextLevel();
        }));
        menuBox = new HBox(30, mainMenuBtn, continueBtn, restartBtn);
        //如果通过成功,那么显示继续按钮
        continueBtn.visibleProperty().bind(FXGL.getbp("complete"));
        continueBtn.managedProperty().bind(continueBtn.visibleProperty());
        //如果通过失败,那么显示重玩按钮
        restartBtn.visibleProperty().bind(FXGL.getbp("complete").not());
        restartBtn.managedProperty().bind(restartBtn.visibleProperty());
        menuBox.setAlignment(Pos.BOTTOM_CENTER);
        menuBox.setPadding(new Insets(22));
        ImageView bgImgView = new ImageView();
        contentPane = new StackPane(bgImgView, menuBox);
        contentPane.setPrefSize(paneWidth, paneHeight);
        //背景图片的绑定,根据通过与否,来决定背景图片
        bgImgView.imageProperty().bind(Bindings
                .when(FXGL.getbp("complete"))
                .then(FXGL.image("ui/scene/completeScene.png"))
                .otherwise(FXGL.image("ui/scene/failedScene.png")));
        contentPane.setTranslateX((FXGL.getAppWidth() - paneWidth) / 2.0);
        contentPane.setTranslateY(-paneHeight);
        Pane root = new Pane(contentPane);
        root.setStyle("-fx-background-color: #0005");
        root.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        getContentRoot().getChildren().setAll(root);
        menuBox.setVisible(false);
    }

    @Override
    public void onCreate() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.8), contentPane);
        tt.setInterpolator(Interpolators.BACK.EASE_OUT());
        tt.setToY((FXGL.getAppHeight() - paneHeight) / 2.0);
        tt.setOnFinished(event -> menuBox.setVisible(true));
        tt.play();
    }

    public void executeAnimation(EventHandler<ActionEvent> handler) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.6), contentPane);
        tt.setInterpolator(Interpolators.BACK.EASE_IN());
        tt.setToY(-paneHeight);
        //动画播放完毕后需要执行的代码
        tt.setOnFinished(handler);
        tt.play();
    }

    @Override
    public void onDestroy() {
        menuBox.setVisible(false);
        contentPane.setTranslateY(-paneHeight);
    }
}
