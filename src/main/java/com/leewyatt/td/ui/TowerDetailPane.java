package com.leewyatt.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.leewyatt.td.data.BulletData;
import com.leewyatt.td.data.TowerData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author LeeWyatt
 *
 * 炮塔详情页面; 显示名称, 攻击范围,攻击速度, 特殊攻击效果等信息
 *
 * 因为经常频繁的使用,只创建了一次(可以改为单例),其余时间调用的updateTowerData方法,来更新炮塔信息;
 */
public class TowerDetailPane extends BorderPane {

    private final Label nameLabel;
    private final Label attackRateLabel;
    private final Label damageLabel;
    private final Label rangeLabel;
    private final Label costLabel;
    private final Label effectLabel;
    private final ImageView effectImageView;
    private final ImageView nameIconImageView;
    private static final double ICON_WIDTH =22;
    private static final double ICON_HEIGHT =22;
    public TowerDetailPane() {
        setPrefSize(260, 103);
        setEffect(new DropShadow(10, Color.BLACK));
        setStyle("""
                -fx-background-image: url("assets/textures/ui/info/infoBg.png");
                -fx-background-repeat: no-repeat;
                -fx-background-position: center;
                """);
        setPadding(new Insets(6, 30, 9, 30));
        nameIconImageView = new ImageView();
        nameLabel = new Label("", nameIconImageView);
        nameLabel.setPrefHeight(15);
        nameLabel.setFont(Font.font(13));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPrefWidth(260);
        String css1 = """
                -fx-border-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #8080803b 0.0%, #8080803b 1.104%, #808080ff 19.167%, #808080ff 79.375%, #8080803b 100.0%);
                -fx-border-width: 0 0 1 0;
                -fx-border-insets: 0 0 2 0;
                """;
        nameLabel.setStyle(css1);
        nameLabel.setAlignment(Pos.CENTER);
        setTop(nameLabel);

        costLabel = createIconLabel("ui/info/cost.png");
        rangeLabel = createIconLabel("ui/info/range.png");
        VBox boxLeft = new VBox(2, costLabel, rangeLabel);
        setLeft(boxLeft);
        damageLabel = createIconLabel("ui/info/damage.png");

        attackRateLabel = createIconLabel("ui/info/speed.png");
        VBox boxRight = new VBox(2, damageLabel, attackRateLabel);
        setRight(boxRight);
        effectImageView = new ImageView();
        effectLabel = new Label("", effectImageView);
        effectLabel.setTextFill(Color.WHITE);
        effectLabel.setPrefWidth(260);
        String css2 = """
                -fx-border-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #8080803b 0.0%, #8080803b 1.104%, #808080ff 19.167%, #808080ff 79.375%, #8080803b 100.0%);
                -fx-border-width: 1 0 0 0;
                -fx-border-insets: 2 0 0 0;
                """;
        effectLabel.setStyle(css2);
        setBottom(effectLabel);
    }

    private Label createIconLabel(String iconUrl) {
        Label label = new Label("", FXGL.texture(iconUrl, ICON_WIDTH, ICON_HEIGHT));
        label.setTextFill(Color.WHITE);
        return label;
    }

    public void updateTowerData(double tx, double ty, TowerData data) {
        nameLabel.setText(data.name());
        nameIconImageView.setImage(FXGL.image(data.icon(), ICON_WIDTH, ICON_HEIGHT));
        attackRateLabel.setText("攻速: "+data.attackRate());
        BulletData bulletData = data.bulletData();
        damageLabel.setText("伤害: "+bulletData.attackDamage());
        rangeLabel.setText("范围: "+bulletData.range()*2);
        costLabel.setText("价格: "+data.cost() );
        effectImageView.setImage(FXGL.image(data.bulletData().effectData().imgName(), ICON_WIDTH, ICON_HEIGHT));
        //effectLabel.setText(bulletData.effectData().desc());
        effectLabel.setText(bulletData.effectData().desc());
        setTranslateX(tx);
        setTranslateY(ty);
    }


}
