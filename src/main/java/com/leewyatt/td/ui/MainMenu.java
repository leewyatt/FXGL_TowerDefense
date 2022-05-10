package com.leewyatt.td.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * @author LeeWyatt
 *
 * 主菜单
 */
public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);
        ImageView iv = new ImageView(FXGL.image("ui/rectMenu/mainMenuBg.png"));
        VBox box = new VBox(10,
                new ImageButton("rectMenu/newGame", 140, 28, () -> getController().startNewGame()),
                new ImageButton("rectMenu/settings", 140, 28, () -> getController().gotoGameMenu()),
                // 帮助按钮, 并未完成,比如显示玩法简介弹窗等
                new ImageButton("rectMenu/help", 140, 28, () -> {}),
                new ImageButton("rectMenu/quit", 140, 28, () -> getController().exit())
        );
        box.setLayoutY(320);
        box.setLayoutX(820);
        getContentRoot().getChildren().addAll(iv, box);
    }

    @Override
    public void onCreate() {
        FXGL.play("launch_sound.wav");
    }


}


