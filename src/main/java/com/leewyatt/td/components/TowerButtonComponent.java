package com.leewyatt.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.leewyatt.td.TowerDefenseApp;
import com.leewyatt.td.data.TowerData;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 * 炮塔列表里面的炮塔图标组件
 * 当 金钱>=建造此炮塔需要的金钱才高亮显示,才能点击建造
 */
public class TowerButtonComponent extends Component {
    private static AnimationChannel selectedBorder;
    private AnimatedTexture texture;
    private TowerData towerData;
    private final ToggleButton btn;

    public TowerButtonComponent() {
        selectedBorder = new AnimationChannel(FXGL.image("ui/right/selected_border.png"), 5, 80, 80, Duration.seconds(0.5), 0, 14);
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<TowerDefenseApp>getAppCast().getTowerBtnGroup());
        btn.setPrefSize(80, 80);
    }

    @Override
    public void onAdded() {
        this.towerData = entity.getObject("towerData");
        texture = new AnimatedTexture(selectedBorder);
        texture.setVisible(false);
        entity.getViewComponent().addChild(texture);
        Label costLabel = new Label(towerData.cost() + "");
        costLabel.setAlignment(Pos.CENTER);
        costLabel.setPrefSize(50, 15);
        costLabel.setTranslateX(15);
        costLabel.setTranslateY(63);
        costLabel.setStyle("-fx-background-color: #f2f2f2;-fx-background-radius: 12;-fx-text-fill: #1E8CB9;");
        entity.getViewComponent().addChild(costLabel);
        entity.getViewComponent().addChild(btn);
        btn.getStyleClass().add("tower-btn");
        String externalForm = FXGL.getAssetLoader().loadCSS("game.css").getExternalForm();
        btn.getStylesheets().add(externalForm);
        btn.setDisable(FXGL.geti("gold") < towerData.cost());
        FXGL.getip("gold").addListener((ob, ov, nv) -> {
            btn.setDisable(FXGL.geti("gold") < towerData.cost());
        });

        btn.selectedProperty().addListener((ob, ov, nv) -> {
            FXGL.set("selectedTowerName", nv ? towerData.name() : "");
            if (nv) {
                texture.setVisible(true);
                texture.loop();
            } else {
                texture.stop();
                texture.setVisible(false);
            }
        });
        //鼠标移入时显示炮塔详情界面
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            FXGL.<TowerDefenseApp>getAppCast().showDetailPane(entity.getX() - 275, entity.getY() - 2, towerData);
        });
        //鼠标移除时隐藏详情页面
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            FXGL.<TowerDefenseApp>getAppCast().hideDetailPane();
        });

    }

}
