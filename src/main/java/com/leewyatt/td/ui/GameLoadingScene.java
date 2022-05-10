package com.leewyatt.td.ui;

import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 加载动画
 *  里外两个圆圈,一个顺时针旋转,一个逆时针旋转;
 */
public class GameLoadingScene extends LoadingScene {

    /**
     * 并行动画, 两个动画,一起执行
     */
    private ParallelTransition pt;

    public GameLoadingScene() {
        ImageView iv = new ImageView(FXGL.image("ui/scene/loading.png"));

        RotateTransition rt1 = new RotateTransition(Duration.seconds(1.0), iv);
        rt1.setFromAngle(0);
        rt1.setToAngle(360);

        ImageView ivOut = new ImageView(FXGL.image("ui/scene/loadingOut.png"));
        RotateTransition rt2 = new RotateTransition(Duration.seconds(3.0), ivOut);
        rt2.setFromAngle(720);
        rt2.setToAngle(0);

        pt = new ParallelTransition(rt1, rt2);
        pt.setCycleCount(5);
        StackPane stackPane = new StackPane(iv,ivOut);
        stackPane.setStyle("-fx-background-color: rgb(0,0,0)");
        stackPane.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        getContentRoot().getChildren().setAll(stackPane);
    }

    @Override
    public void onCreate() {
        pt.play();
    }

    @Override
    public void onDestroy() {
        pt.stop();
    }
}
