package Spawnables;
/**
 *
 *@author Olli
*/

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

/**
    @version 1
    abstract class that handles all the spawnables; every extending class

*/

public abstract class AbstractSpawnables implements Spawnables {

    //coordinates for the ICON
    int x;
    int y;

    @Override
    public Bounds getBoundsForIcon(){
        Rectangle icon = new Rectangle(x, y, 30, 30);
        return icon.getLayoutBounds();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void randomizePowerUpLocation() {
        //multiplied by size of the board -50 px to keep them visible
        setX((int) (Math.random() * 750));
        setY((int) (Math.random() * 550));
    }

    //when this ICON is created, hide it outside the vision of the user (user sees everything between 0 & width/height)
    @Override
    public void init(){
        setX(-100);
        setY(-100);
    }
}
