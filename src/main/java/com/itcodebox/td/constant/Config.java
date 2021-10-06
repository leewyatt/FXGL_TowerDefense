package com.itcodebox.td.constant;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.itcodebox.td.data.TowerData;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public interface Config {

    TowerData LASER_TOWER_DATA = new TowerData("laserTower",30,30,15,220, 500,Duration.seconds(.2), FXGL.image("tower/laser/tower_icon.png",30,30));
    TowerData ARROW_TOWER_DATA = new TowerData("arrowTower",43,68,1,580, 467,Duration.seconds(0.7), FXGL.image("tower/arrow/tower_icon.png")){
        @Override
        public int getDamage() {
            return FXGLMath.random(10, 15);
        }
    };
    TowerData THUNDER_TOWER_DATA = new TowerData("thunderTower",45,72,1,260,600, Duration.seconds(.35), FXGL.image("tower/thunder/tower_icon.png")){
        @Override
        public int getDamage() {
            return FXGLMath.random(20, 30);
        }
    };
    TowerData FLAME_TOWER_DATA = new TowerData("flameTower",45,89,1,350,350, Duration.seconds(.6), FXGL.image("tower/flame/tower_icon.png")){
        @Override
        public int getDamage() {
            return FXGLMath.random(35, 50);
        }
    };

}
