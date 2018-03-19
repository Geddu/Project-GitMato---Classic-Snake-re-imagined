/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Spawnables;

import Model.Worm;

import Sound.Music;
import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
/**
 *
 * @author maxki
 */
public class Life implements Spawnables {
    
    private int xe;
    private int ye;
    private Image image;
    
    public void Life(Worm worm) {
        worm.setPoints(worm.getPoints()+100);
        addLife(worm);
    }
    
    public Life() {
        init();
    }
    
    public static void addLife (Worm worm) {
        Music.sound5.play();
        worm.setLife(worm.getLife()+1);
    }
    
    public static void loseLife(Worm worm) {
        Music.sound11.play();
        worm.setLife(worm.getLife()-1);
        worm.setShield(true);
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                worm.setShield(false);
                }
            }, 1000);
    }
     
    @Override
    public void loadImage(String imageName) {
        ImageView ii = new ImageView(imageName);
        image = ii.getImage();
    }

    @Override
    public void init() {
        ImageView kuva = new ImageView("src/main/resources/images/Life_1.png");
        image = kuva.getImage();
            
        setX(-100);
        setY(-100);
    }

    @Override
    public Bounds getBounds() {
        Rectangle life = new Rectangle(xe+3, ye+3, 30, 30);
        return life.getLayoutBounds();
    }

    @Override
    public int getX() {
        return xe;    
    }

    @Override
    public int getY() {
        return ye;
    }

    @Override
    public void setX(int x) {
        this.xe = x;
    }

    @Override
    public void setY(int y) {
        this.ye = y;
    }

    @Override
    public Image getImage() {
        return image;
    }
    @Override
    public void randomizePowerUpLocation() {
        setX((int) (Math.random() * 750));
        setY((int) (Math.random() * 550));
    }
}
