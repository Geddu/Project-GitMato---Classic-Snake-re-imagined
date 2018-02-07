/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author maxki
 */
public class Snack implements Spawnables{
    private int xe;
    private int ye;
    private Image image;

    public Snack() {
        init();
    }
    
    @Override
    public void init() {
        
        ImageIcon kuva = new ImageIcon("src/Images/Apple(800x600).png");
        image = kuva.getImage();
            
        setX((int) (Math.random() * 750));
        setY((int) (Math.random() * 550));
    }
@Override    
    public void loadImage(String imageName) {
        ImageIcon ii = new ImageIcon(imageName);
        image = ii.getImage();
    }
@Override
    public Image getImage() {
        return image;
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
    public void setX(int x){
        this.xe = x;
    }
@Override    
    public void setY(int y){
        this.ye = y;
    }

@Override    
    public Rectangle getBounds() {
        return new Rectangle(xe, ye, 41, 55);
    }
@Override
    public void randomizeXY() {
        setX((int) (Math.random() * 750));
        setY((int) (Math.random() * 550));
    }
}

