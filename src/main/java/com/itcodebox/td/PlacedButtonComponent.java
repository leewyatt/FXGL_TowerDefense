package com.itcodebox.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class PlacedButtonComponent extends Component {
    private AnimationChannel acBorder = new AnimationChannel(FXGL.image("selected_border.png"), 5, 80, 80, Duration.seconds(1), 0, 14);
    private AnimatedTexture texture;
    @Override
    public void onAdded() {
        texture = new AnimatedTexture(acBorder);
        texture.setVisible(false);
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            FXGL.set("placed", !FXGL.getb("placed"));
        });
        FXGL.getbp("placed").addListener((ob,ov,nv)->{
            if (nv) {
                texture.setVisible(true);
                texture.loop();
            }else {
                texture.stop();
                texture.setVisible(false);
            }
        });
    }
}
