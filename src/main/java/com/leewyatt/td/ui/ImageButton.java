package com.leewyatt.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.Label;

/**
 * @author LeeWyatt
 *
 * 图片按钮: 移入和移除是不同的图片
 *
 */
public class ImageButton extends Label {
    /**
     *
     * @param imgName 图片名称
     * @param w 按钮宽度
     * @param h 按钮高度
     * @param action 点击按钮后执行的事情
     */
    public ImageButton(String imgName, int w, int h, Runnable action) {
        setPrefSize(w, h);
        setStyle("-fx-background-image: url('assets/textures/ui/" + imgName + ".png')");
        hoverProperty().addListener((ob, ov, nv) -> {
            String imageName = imgName + (nv ? "Hover.png')" : ".png')");
            setStyle("-fx-background-image: url('assets/textures/ui/" + imageName);
        });
        setOnMouseClicked(e -> {
            FXGL.play("select.wav");
            action.run();
        });
        managedProperty().bind(visibleProperty());
        setOnMouseEntered(e -> FXGL.play("mainMenuHover.wav"));
    }
}
