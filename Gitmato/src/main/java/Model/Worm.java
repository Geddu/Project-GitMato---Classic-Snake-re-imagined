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
public class Worm {

    private double dx;
    private double dy;
    private int x;
    private int y;
    private int suunta = 1;
    private int suuntaAdv = 0;
    private int playerNro;
    private boolean shield = false; //shield power-up
    private boolean reverse = false; //Reverse debuff up
    private int points;


    private Image image;    
    private ImageIcon wormup;
    private ImageIcon wormdown;
    private ImageIcon wormleft;
    private ImageIcon wormright;

    
    double nopeus = 2;
    private int life=3;

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }
    
    public Worm(int p) {  
        initWorm(p);
    }

    private void initWorm(int p) {
        this.playerNro = p;
        if(playerNro==1){
             wormup = new ImageIcon("src/main/resources/images/RedWormUp(800x600).png");
             wormdown = new ImageIcon("src/main/resources/images/RedWormDown(800x600).png");
             wormleft = new ImageIcon("src/main/resources/images/RedWormLeft(800x600).png");
             wormright = new ImageIcon("src/main/resources/images/RedWormRight(800x600).png");
            x = 200;
            y = 279; //oma puoli kentästä-kuvan korkeus
        }
        if(playerNro==2){
             wormup = new ImageIcon("src/main/resources/images/BlueWormUp(800x600).png");
             wormdown = new ImageIcon("src/main/resources/images/BlueWormDown(800x600).png");
             wormleft = new ImageIcon("src/main/resources/images/BlueWormLeft(800x600).png");
             wormright = new ImageIcon("src/main/resources/images/BlueWormRight(800x600).png");
            x = 565; //kentän puoliväli-kuvan leveys
            y = 279; //oma puoli kentästä-kuvan korkeus
        }
    }
    
    public void move() {
        if (x > 0 && dx < 0 || x < 960 && dx > 0) {
            if(suuntaAdv == 2){
                x += dx;
            }
            
        }
        if (y > 0 && dy < 0 || y < 950 && dy > 0) {
            if(suuntaAdv == 1){
                y += dy;
            }
            
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void setX(int luku) {
        this.x = luku;
    }

    public void setY(int luku) {
        this.y = luku;
    }

    public Image getImage() {
        return image;
    }
    
    public void setImage(Image img) {
        this.image = img;
    }
    
    public int getSuunta(){
        return suunta;
    }

    public void setSuunta(int s){
        this.suunta = s;
    }
    public void setSuuntaAdv(int a){
        this.suuntaAdv = a;
    }
    
     public double getNopeus() {
        return nopeus;
    }

    public void setNopeus(double nopeus) {
        this.nopeus = nopeus;
    }
    
    public boolean getShield(Worm worm) {
        return this.shield;
    }
    
    public void setShield(boolean active) {
        this.shield = active;
    }
    
    public void setReverse(boolean active) {
        this.reverse = active;
    }
    public boolean getReverse(Worm worm) {
        return this.reverse;
    }
    
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public void randomizeXY() {
        setX((int) (Math.random() * 740) + 10);
        setY((int) (Math.random() * 540) + 10);
    }
    
    public void moveCont(){
        //if shield is NOT active on worm
        if(suunta == 1){
            setImage (wormleft.getImage());
            dx = -1 * nopeus;
        }
        
        if(suunta == 2){
            setImage (wormright.getImage());
            dx = 1 * nopeus;
        }
        
        if(suunta == 3){
            setImage (wormup.getImage());
            dy = -1 * nopeus;
            
        }
        
        if(suunta == 4){
            setImage (wormdown.getImage());
            dy = 1 * nopeus;
            
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 35, 42);
    }
    
    
}