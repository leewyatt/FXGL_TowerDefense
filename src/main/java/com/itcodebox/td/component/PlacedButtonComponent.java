package com.itcodebox.td.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.itcodebox.td.constant.TowerType;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class PlacedButtonComponent extends Component {
    private  static AnimationChannel acBorder = new AnimationChannel(FXGL.image("selected_border.png"), 5, 80, 80, Duration.seconds(1), 0, 14);
    private AnimatedTexture texture;
    private TowerType towerType;
    private boolean selected;
    public PlacedButtonComponent(TowerType towerType) {
        this.towerType = towerType;
    }

    @Override
    public void onAdded() {
        texture = new AnimatedTexture(acBorder);
        texture.setVisible(false);
        entity.getViewComponent().addChild(texture);

        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            selected = !selected;
            if (selected) {
                FXGL.set("towerType", towerType);
            }else {
                FXGL.set("towerType", TowerType.NONE);
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            texture.setVisible(true);
            texture.loop();
        }else {
            texture.stop();
            texture.setVisible(false);
        }
    }
}
