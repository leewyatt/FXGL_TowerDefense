package com.leewyatt.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.control.ToggleButton;

/**
 * @author LeeWyatt
 *
 * 暂停按钮:
 *     点击后暂停或者继续游戏
 */
public class PauseButtonComponent extends Component {

    public PauseButtonComponent() {

    }

    @Override
    public void onAdded() {
        ToggleButton btn = new ToggleButton();
        btn.setPrefSize(85, 71);
        btn.getStyleClass().add("pause-btn");
        btn.getStylesheets().add(FXGL.getAssetLoader().loadCSS("game.css").getExternalForm());
        entity.getViewComponent().addChild(btn);

        btn.selectedProperty().addListener((ob, ov, nv) ->{
            if (nv) {
                FXGL.getGameController().pauseEngine();
            } else {
                FXGL.getGameController().resumeEngine();
            }
        });
    }
}
