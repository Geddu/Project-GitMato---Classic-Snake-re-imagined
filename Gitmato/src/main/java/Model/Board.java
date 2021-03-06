/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import GUI.Matopeli;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import Controller.PlayerController;
import Sound.Music;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;
import java.util.TimerTask;

import Spawnables.*;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javax.swing.ImageIcon;

/**
 *
 * @author maxki
 */
public final class Board extends JPanel implements ActionListener {

    private static List<Worm> worms;
    private final int DELAY = 10;
    //Lista Tail paloista
    private final List<Tail> body;
    private final List<Tail> body2;
    private final List<Spawnables> pickableList;
    private final List<Point2D> cordinates;
    private final List<Point2D> cordinates2;
    // Wormin locaatio muuttujat:
    Point2D p;
    Point2D p2;// coordinaatit
    ImageIcon filtteri = new ImageIcon("src/main/resources/images/BlackFilter.png");
    private Worm worm;
    private Worm worm2;
    private PlayerController control;
    private Tail tail;
    private Tail tail2;
    private Timer timer;
    private Snack snack;
    private Faster faster;
    private Slower slower;
    private Confuse reverse;
    private Life HP;
    private Shield shield;
    private Bombs bombs;
    private Laser laser;
    private boolean ingame;
    private ImageIcon Ironpic;
    //pidetään lukua kuinka monta Tail objektia on.
    private int tailNro = 0;
    private int tailNro2 = 0;
    private int x, y;
    private int x2, y2;
    private Image halo;
    private Image halo2;
    private String pelimoodi = "versus";
    private Matopeli engine;
    private Image background;
    private Image filter;
    private Highscore hscore = new Highscore();
    private int score;
    DBConnection connection = new DBConnection();
    private long currentTime = 0; // nykyinen aika (ms)
    private long previousTime = 0; // viime framen aika (ms)
    private double timeCounter = 0; // aikalaskuri (sec)
    private int frameCounter = 0;
    private double theRealFpsCounter = 0; // näyttää jatkuvasti oikean fps:n

    public Board(Matopeli e, String pelimoodi) {
        this.engine = e;
        this.pelimoodi = pelimoodi;

        //alustetaan listat
        pickableList = new ArrayList<>();
        Board.worms = new ArrayList<>();

        this.cordinates = new ArrayList<>();
        this.body = new ArrayList<>();
        this.p = new Point2D.Double(0, 0);

        this.cordinates2 = new ArrayList<>();
        this.body2 = new ArrayList<>();
        this.p2 = new Point2D.Double(0, 0);

        initBoard();

    }

    public static List getWorms() {
        return worms;
    }

    private void initBoard() {
        //TODO: Tähän täytyy tehdä kaikki mahdolliset pelimuodot

        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);

        faster = new Faster();
        slower = new Slower();
        reverse = new Confuse();
        HP = new Life();
        shield = new Shield();
        bombs = new Bombs();
        laser = new Laser();

        snack = new Snack();

        pickableList.add(faster);
        pickableList.add(slower);
        pickableList.add(reverse);
        pickableList.add(HP);
        pickableList.add(shield);
        pickableList.add(bombs);
        pickableList.add(laser);
        pickableList.add(snack);
        worms.add(worm = new Worm(1)); //lista worm olioista
        worms.add(worm2 = new Worm(2));

        timer = new Timer(DELAY, this);
        timer.start();
        ingame = true;

        control = new PlayerController(); //
        control.updateWorms(); // Worms-lista liitetään playercontrolleriin
        control.yksinPeli(pelimoodi);
        control.updateBoard(this);

        ImageIcon kuvamato = new ImageIcon("src/main/resources/images/BlueBG800x600.png");
        background = kuvamato.getImage();
        if (pelimoodi == "vs AI") {
            BotTurnDown();
        }
        if (pelimoodi == "sp") {
            worm2.setX(-1000);
            worm2.setY(-2000); //läpäl
            worm.setLife(1);
        }

        if (pelimoodi != "sp") {
            powerUpCD(); //piilottaa powerupit alussa
        }
    }

    public void restartGame() {
        if (!ingame) {
            snack.init();
            ingame = true;

            worms.clear();

            worms.add(worm = new Worm(1)); //lista worm olioista
            if (pelimoodi != "sp") {

                worms.add(worm2 = new Worm(2));
                powerUpCD();
                cordinates2.clear();
                body2.clear();
                tailNro2 = 0;
            }

            timer.start();

            control.updateWorms();
            control.updateBoard(this);

            cordinates.clear();

            body.clear();

            tailNro = 0;

            Sound.Music.sound1.loop();
            if (pelimoodi == "vs AI") {
                BotTurnDown();
            }

            if (pelimoodi == "sp") {
                worm2.setX(-1000);
                worm2.setY(-2000);
                worm.setLife(1);
            }
        }

    }

    private void inGame() {
        if (!ingame) {
            timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        if (ingame) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setPaint(Color.BLACK);
            g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            g.drawImage(this.background, 0, 0, null);
            doDrawing(g);

            Toolkit.getDefaultToolkit().sync();
        } else {
            drawGameOver(g);
            inGame();
        }
    }

    private void doDrawing(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;
        drawPisteet(g);

        //tarkistetaan onko häntiä piirrettäväksi
        if (tailNro > 0) {
            for (int i = 0; i < body.size(); i++) {
                // pidetään huoli että jokainen "tail" tulee piirrettyä per frame
                g2d.drawImage(body.get(i).getImage(), body.get(i).getX(), body.get(i).getY(), this);
                //System.out.println("tätä tehdään");
            }
        }

        if (tailNro2 > 0) {
            for (int i = 0; i < body2.size(); i++) {
                // pidetään huoli että jokainen "tail" tulee piirrettyä per frame
                g2d.drawImage(body2.get(i).getImage(), body2.get(i).getX(), body2.get(i).getY(), this);
                //System.out.println("tätä tehdään");
            }
        }
        // piirretään power-upit matojen päälle, jotta ne ovat helpommit nähtävissä
        g2d.drawImage(snack.getImage(), snack.getX(), snack.getY(), this);
        g2d.drawImage(faster.getImage(), faster.getX(), faster.getY(), this);
        g2d.drawImage(slower.getImage(), slower.getX(), slower.getY(), this);
        g2d.drawImage(reverse.getImage(), reverse.getX(), reverse.getY(), this);
        g2d.drawImage(HP.getImage(), HP.getX(), HP.getY(), this);
        g2d.drawImage(shield.getImage(), shield.getX(), shield.getY(), this);
        g2d.drawImage(bombs.getImage(1), bombs.getX(), bombs.getY(), this);
        g2d.drawImage(bombs.getImage(2), bombs.getXBombs(1), bombs.getYBombs(1), this);
        g2d.drawImage(bombs.getImage(3), bombs.getXBombs(2), bombs.getYBombs(2), this);
        g2d.drawImage(bombs.getImage(2), bombs.getXBombs(3), bombs.getYBombs(3), this);
        g2d.drawImage(bombs.getImage(3), bombs.getXBombs(4), bombs.getYBombs(4), this);
        g2d.drawImage(bombs.getImage(2), bombs.getXBombs(5), bombs.getYBombs(5), this);
        g2d.drawImage(bombs.getImage(3), bombs.getXBombs(6), bombs.getYBombs(6), this);
        g2d.drawImage(laser.getImage(), laser.getX(), laser.getY(), this);
        if (!laser.getLethal()) {
            g2d.drawImage(laser.getlasersightH(), laser.getX3(), laser.getY3(), this);
            g2d.drawImage(laser.getLasersightV(), laser.getX2(), laser.getY2(), this);

        } else {
            g2d.drawImage(laser.getImageHori(), laser.getX3(), laser.getY3(), this);
            g2d.drawImage(laser.getImageVert(), laser.getX2(), laser.getY2(), this);
        }
        g2d.drawImage(worm.getImage(), worm.getX(), worm.getY(), this);
        if (pelimoodi != "sp") {
            g2d.drawImage(worm2.getImage(), worm2.getX(), worm2.getY(), this);
        }
        if (worm.getShield(worm)) {
            g2d.drawImage(shield.getShieldImage(), worm.getX() - 5, worm.getY() - 4, this);
        }
        if (worm2.getShield(worm2)) {
            g2d.drawImage(shield.getShieldImage(), worm2.getX() - 5, worm2.getY() - 4, this);
        }
        if (worm.getLife() <= 0 || worm2.getLife() <= 0) {
            drawGameOver(g);
        }
        if (worm.getReverse(worm)) {
            g2d.drawImage(reverse.getConfusionImage(), worm.getX() - 5, worm.getY() - 4, this);
        }
        if (worm2.getReverse(worm2)) {
            g2d.drawImage(reverse.getConfusionImage(), worm2.getX() - 5, worm2.getY() - 4, this);
        }

    }

    private void drawPisteet(Graphics g) {
        Font small = new Font("Helvetica", Font.BOLD, 20);
        Font smaller = new Font("Helvetica", Font.PLAIN, 15);
        FontMetrics fm = getFontMetrics(small);
        FontMetrics fm2 = getFontMetrics(smaller);
        String pt3 = "FPS: " + theRealFpsCounter;
        
        g.setColor(Color.RED);
        g.setFont(small);
        String hp = "HP: " + worm.getLife();
        String pt = "Pisteet: " + worm.getPoints();
        g.drawString(hp, 10, 25);
        g.drawString(pt, 10, 50);

        if (pelimoodi != "sp") {
            g.setColor(Color.BLUE);
            String hp2 = "HP: " + worm2.getLife();
            String pt2 = "Pisteet: " + worm2.getPoints();

            g.drawString(hp2, (790 - fm.stringWidth(hp2)), 25);
            g.drawString(pt2, (790 - fm.stringWidth(pt2)), 50);
        }
        g.setColor(Color.WHITE);
        g.setFont(smaller);
        g.drawString(pt3, ((790 - fm2.stringWidth(pt3))/2), 20);//piirtää fps

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        currentTime = System.currentTimeMillis();
        double deltaTime = (double) (currentTime - previousTime) / 1_000;
        // 1/deltaTime); <- kertoo nykyisen fps joka frame.
       
        double interval = 0.5;
        if(timeCounter > interval){
            theRealFpsCounter = frameCounter;
            frameCounter = 0;
            timeCounter = 0;
        }
        else{
            timeCounter += deltaTime;
            frameCounter = frameCounter + (int)(1/interval);
        }
        
        checkCollisions();
        worm.move();
        worm.moveCont();
        worm2.move();
        worm2.moveCont();
        //tallennnetaan wormin coordinaatit yhteen 2D muuttujaan
        x = worm.getX();
        y = worm.getY();
        x2 = worm2.getX();
        y2 = worm2.getY();
        p = new Point2D.Double(x, y);
        p2 = new Point2D.Double(x2, y2);
        //Lisätään coortinaatit listan cordinates alkuun (0).
        //siirtää automaattisesti taulukon arvot yhden eteenpäin, 0->1
        cordinates.add(0, p);
        cordinates2.add(0, p2);

        //jos lista liian suuri poistetaan viimeinen
        if (cordinates.size() >= 10000) {
            cordinates.remove(cordinates.size() - 1);
        }

        if (cordinates2.size() >= 10000) {
            cordinates2.remove(cordinates2.size() - 1);
        }

        //Päivitetään jokaisen "Tail" olion coordinaatit
        for (int i = 0; i < body.size(); i++) {
            int f = body.get(i).getCordinateInt();
            p = cordinates.get(f);
            x = (int) p.getX();
            y = (int) p.getY();
            body.get(i).setX(x);
            body.get(i).setY(y);
        }

        for (int i = 0; i < body2.size(); i++) {
            int f = body2.get(i).getCordinateInt();
            p2 = cordinates2.get(f);
            x2 = (int) p2.getX();
            y2 = (int) p2.getY();
            body2.get(i).setX(x2);
            body2.get(i).setY(y2);
        }

        repaint();

        if (pelimoodi == "vs AI") {
            BlueAIBot();
        }
        
        previousTime = currentTime;
    }

    public void checkCollisions() {
        Rectangle Matokuutio = worm.getBounds();
        Rectangle Matokuutio2 = worm2.getBounds();

        Rectangle s = snack.getBounds();
        Rectangle pf = faster.getBounds();
        Rectangle ps = slower.getBounds();
        Rectangle pr = reverse.getBounds();
        Rectangle pl = HP.getBounds();
        Rectangle psh = shield.getBounds();
        Rectangle pb = bombs.getBounds();
        Ellipse2D pb2 = bombs.getBoundsBombs(2);
        Ellipse2D pb3 = bombs.getBoundsBombs(4);
        Ellipse2D pb4 = bombs.getBoundsBombs(6);
        Rectangle pla = laser.getBounds();
        Rectangle beam = laser.getBoundsB();

        for (int i = 0; i < body.size(); i++) {
            Rectangle Matotail = body.get(i).getBounds();
            if (Matokuutio2.intersects(Matotail) && !shield.isActive(worm2) && pelimoodi != "sp") {
                if (worm2.getLife() > 1) {
                    shield.shield(worm2, 50);
                    worm2.randomizeXY();
                    if (pelimoodi == "vs AI") {
                        BotTurnDown();
                    }

                }
                Life.loseLife(worm2);
            }
        }

        for (int i = 0; i < body2.size(); i++) {
            Rectangle Matotail2 = body2.get(i).getBounds();
            if (Matokuutio.intersects(Matotail2) && !shield.isActive(worm)) {
                if (worm.getLife() > 1) {
                    shield.shield(worm, 50);
                    worm.randomizeXY();
                    worm.setSuuntaAdv(0);
                    worm.setSuunta(0);
                }
                Life.loseLife(worm);
            }
        }
        if (pelimoodi == "sp") {
            for (int i = 2; i < body.size(); i++) {
                Rectangle Matotail2 = body.get(i).getBounds();
                if (Matokuutio.intersects(Matotail2) && !shield.isActive(worm)) {
                    if (worm.getLife() > 1) {
                        shield.shield(worm, 50);
                        worm.randomizeXY();
                        worm.setSuuntaAdv(0);
                        worm.setSuunta(0);
                    }
                    Life.loseLife(worm);
                }
            }
        }

        //mato 1 collisions
        if (s.intersects(Matokuutio)) {
            snack.randomizePowerUpLocation();
            worm.setPoints(worm.getPoints() + 100);
            spawnTail(1);
        }

        if (pf.intersects(Matokuutio)) {
            faster.faster(worm);
            powerUpCD();
        }

        if (ps.intersects(Matokuutio)) {
            slower.slower(worm, worm2);
            powerUpCD();
        }

        if (pr.intersects(Matokuutio)) {
            reverse.confuse(worm, worm2);
            powerUpCD();
        }

        if (pl.intersects(Matokuutio)) {
            HP.Life(worm);
            powerUpCD();
        }

        if (psh.intersects(Matokuutio)) {
            shield.shield(worm, 10000);
            powerUpCD();
        }
        if (pb.intersects(Matokuutio)) {
            bombs.bombs(worm);
            bombs.bombZone();
            powerUpCD();
        }

        if (pb2.intersects(Matokuutio) || pb3.intersects(Matokuutio) || pb4.intersects(Matokuutio) && !shield.isActive(worm)) {
            bombs.damage(worm);
        }
        if (pla.intersects(Matokuutio)) {
            laser.onPickup(worm, worm2);
            beam = laser.getBoundsB();
            powerUpCD();
        }
        if (beam.intersects(Matokuutio) && !shield.isActive(worm)) {
            laser.damage(worm);
        }

        if (worm.getX() < 5 || worm.getX() > 760 || worm.getY() < 5 || worm.getY() > 550) {
            if (worm.getLife() > 1) {
                worm.randomizeXY();
                worm.setSuuntaAdv(0);
                worm.setSuunta(0);
            }
            Life.loseLife(worm);
            worm.setPoints(worm.getPoints() - 100);
        }

        //mato 2 collisions
        if (s.intersects(Matokuutio2)) {
            snack.randomizePowerUpLocation();
            worm2.setPoints(worm2.getPoints() + 100);
            spawnTail(2);
        }

        if (pf.intersects(Matokuutio2)) {
            faster.faster(worm2);
            powerUpCD();
        }

        if (ps.intersects(Matokuutio2)) {
            slower.slower(worm2, worm);
            powerUpCD();
        }

        if (pr.intersects(Matokuutio2)) {
            reverse.confuse(worm2, worm);
            powerUpCD();
        }

        if (pl.intersects(Matokuutio2)) {
            HP.Life(worm2);
            powerUpCD();
        }

        if (psh.intersects(Matokuutio2)) {
            shield.shield(worm2, 10000);
            powerUpCD();
        }
        if (pb.intersects(Matokuutio2)) {
            bombs.bombs(worm2);
            bombs.bombZone();
            powerUpCD();
        }

        if (pb2.intersects(Matokuutio2) || pb3.intersects(Matokuutio2) || pb4.intersects(Matokuutio2) && !shield.isActive(worm2)) {
            bombs.damage(worm2);
        }
        if (pla.intersects(Matokuutio2)) {
            laser.onPickup(worm2, worm);
            beam = laser.getBoundsB();
            powerUpCD();
        }
        if (beam.intersects(Matokuutio2) && !shield.isActive(worm2)) {
            laser.damage(worm2);
        }
        if ((worm2.getX() < 5 || worm2.getX() > 760 || worm2.getY() < 5 || worm2.getY() > 550) && pelimoodi != "sp") {
            if (worm2.getLife() > 1) {
                worm2.randomizeXY();
                if (pelimoodi == "vs AI") {
                    BotTurnDown();
                } else {
                    worm2.setSuuntaAdv(0);
                    worm2.setSuunta(0);
                }

            }
            Life.loseLife(worm2);
            worm2.setPoints(worm2.getPoints() - 100);
        }
    }

    private void drawGameOver(Graphics g) {
        Music.sound4.play();
        laser.hide();
        filter = filtteri.getImage();
        String msg = null;
        String msg2;
        String msg3;
        Graphics2D g3 = (Graphics2D) g;
        g3.drawImage(filter, 0, 0, null);

        Font small = new Font("Helvetica", Font.BOLD, 20);
        Font big = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics fm2 = getFontMetrics(small);
        FontMetrics fm = getFontMetrics(big);
        g3.setFont(big);

        Music.sound1.stop();
        ingame = false;
        if (worm.getLife() <= 0) {
            if (pelimoodi != "sp") {
                score = worm2.getPoints();
                msg = "BLUE Won!";
                g3.setColor(Color.blue);
            } else {
                score = worm.getPoints();
                msg = "GAME OVER!";
                g3.setColor(Color.white);
            }
        } else if (worm2.getLife() <= 0) {
            score = worm.getPoints();
            msg = "RED Won!";
            g3.setColor(Color.red);
        }
        msg2 = "Press SPACE to play again.";
        msg3 = "Press H to submit your highscore";
        g3.drawString(msg, (806 - fm.stringWidth(msg)) / 2, 270);
        g3.setFont(small);
        g3.setColor(Color.white);
        g3.drawString(msg2, (806 - fm2.stringWidth(msg2)) / 2, 600 / 2);
        g3.drawString(msg3, (806 - fm2.stringWidth(msg3)) / 2, 320);
    }

    private void spawnTail(int n) {
        //tulee yksi Tail pala lisää
        switch (n) {
            case 1:
                tailNro++;
                body.add(tail = new Tail(tailNro * 15, 1));
                break;
            case 2:
                tailNro2++;
                body2.add(tail = new Tail(tailNro2 * 15, 2));
        }
    }

    public void powerUpCD() {

        faster.setX(-100);
        faster.setY(-100);
        slower.setX(-100);
        slower.setY(-100);
        reverse.setX(-100);
        reverse.setY(-100);
        HP.setX(-100);
        HP.setY(-100);
        shield.setX(-100);
        shield.setY(-100);
        bombs.setY(-100);
        bombs.setX(-100);
        for (int i = 1; i < 7; i++) {
            bombs.setXBombs(i, -1000);
            bombs.setYBombs(i, -1000);
        }
        laser.setY(-100);
        laser.setX(-100);

        java.util.Timer timer2 = new java.util.Timer();
        timer2.schedule(new TimerTask() {

            @Override
            public void run() {

                int n = (int) (Math.random() * 7);

                switch (n) {
                    case 0:
                        shield.randomizePowerUpLocation();
                        break;
                    case 1:
                        faster.randomizePowerUpLocation();
                        break;
                    case 2:
                        slower.randomizePowerUpLocation();
                        break;
                    case 3:
                        reverse.randomizePowerUpLocation();
                        break;
                    case 4:
                        HP.randomizePowerUpLocation();
                        break;
                    case 5:
                        bombs.randomizePowerUpLocation();
                        break;
                    case 6:
                        laser.randomizePowerUpLocation();
                        break;
                }

            }
        }, 5000); //aika (ms), joka odotetaan
    }

    public void BlueAIBot() {

        Ellipse2D pb1 = bombs.getBoundsBombs(1);
        Ellipse2D pb2 = bombs.getBoundsBombs(2);
        Ellipse2D pb3 = bombs.getBoundsBombs(3);
        Ellipse2D pb4 = bombs.getBoundsBombs(4);
        Ellipse2D pb5 = bombs.getBoundsBombs(5);
        Ellipse2D pb6 = bombs.getBoundsBombs(6);

        if (tailNro2 > 3) {
            if (worm2.getBounds().intersects(body2.get(body2.size() - 1).getBounds()) && body2.size() > 3) {
                BotTurnDown();
            }

        }
        for (int i = 0; i < pickableList.size(); i++) {

            if (worms.get(1).getX() < (pickableList.get(i).getX() + 10) && worms.get(1).getX() > (pickableList.get(i).getX() - 10) && !worms.get(1).getReverse(worms.get(1))) {
                if (worms.get(1).getY() < pickableList.get(i).getY()) {
                    BotTurnDown();

                    //alas
                } else {
                    BotTurnUp();

                    //ylös
                }

            }

            if (worms.get(1).getY() < (pickableList.get(i).getY() + 10) && worms.get(1).getY() > (pickableList.get(i).getY() - 10) && !worms.get(1).getReverse(worms.get(1))) {

                if (worms.get(1).getX() < pickableList.get(i).getX()) {
                    BotTurnRight();

                    //oikea
                } else {
                    BotTurnLeft();

                    //vasen
                }

            }
        }

        if (worms.get(1).getX() < 20 && worms.get(1).getSuunta() != 4) {
            if (worms.get(1).getSuunta() != 2) {
                BotTurnUp();
                worms.get(1).setX(25);
            }

            if (worms.get(1).getReverse(worms.get(1))) {
                BotTurnUp();
                worms.get(1).setX(25);
            }

        }

        if (worms.get(1).getX() > 715 && worms.get(1).getSuunta() != 3) {
            if (worms.get(1).getSuunta() != 1) {
                BotTurnDown();
                worms.get(1).setX(710);
            }

            if (worms.get(1).getReverse(worms.get(1))) {
                BotTurnDown();
                worms.get(1).setX(710);
            }

        }

        if (worms.get(1).getY() > 540 && worms.get(1).getSuunta() != 2) {
            if (worms.get(1).getSuunta() != 3) {
                BotTurnLeft();
                worms.get(1).setY(535);
            }

            if (worms.get(1).getReverse(worms.get(1))) {
                BotTurnLeft();
                worms.get(1).setY(535);
            }

        }

        if ((worms.get(1).getY() < 20 && worms.get(1).getSuunta() != 1)) {
            if (worms.get(1).getSuunta() != 4) {
                BotTurnRight();
                worms.get(1).setY(25);
            }

            if (worms.get(1).getReverse(worms.get(1))) {
                BotTurnRight();
                worms.get(1).setY(25);
            }

        }

        Rectangle AIleft = getBoundsLeft();
        for (int i = 0; i < body.size(); i++) {
            Rectangle MatotailForAI = body.get(i).getBounds();

            Rectangle2D l2 = laser.getBoundsB();
            if ((AIleft.intersects(MatotailForAI) || pb1.intersects(AIleft) || pb2.intersects(AIleft) || pb3.intersects(AIleft) || pb4.intersects(AIleft) || pb5.intersects(AIleft) || pb6.intersects(AIleft) || (l2.intersects(AIleft) && laser.getHorizontal()) || (l2.intersects(AIleft) && !l2.intersects(worms.get(1).getBounds()))) && (worms.get(1).getSuunta() == 1 || worms.get(1).getReverse(worms.get(1)))) {
                int n = (int) (Math.random() * 1);

                switch (n) {
                    case 0:
                        do {
                            BotTurnUp();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));

                    case 1:
                        do {
                            BotTurnDown();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));
                }
            }

        }

        Rectangle AIright = getBoundsRight();
        for (int i = 0; i < body.size(); i++) {
            Rectangle MatotailForAI = body.get(i).getBounds();

            Rectangle2D l2 = laser.getBoundsB();
            if ((AIright.intersects(MatotailForAI) || pb1.intersects(AIright) || pb2.intersects(AIright) || pb3.intersects(AIright) || pb4.intersects(AIright) || pb5.intersects(AIright) || pb6.intersects(AIright) || (l2.intersects(AIright) && laser.getHorizontal()) || (l2.intersects(AIright) && !l2.intersects(worms.get(1).getBounds()))) && (worms.get(1).getSuunta() == 2 || worms.get(1).getReverse(worms.get(1)))) {
                int n = (int) (Math.random() * 2);

                switch (n) {
                    case 0:
                        do {
                            BotTurnUp();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));

                    case 1:
                        do {
                            BotTurnDown();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));
                }
            }

        }

        Rectangle AIup = getBoundsUp();
        for (int i = 0; i < body.size(); i++) {
            Rectangle MatotailForAI = body.get(i).getBounds();

            Rectangle2D l2 = laser.getBoundsB();
            if ((AIup.intersects(MatotailForAI) || pb1.intersects(AIup) || pb2.intersects(AIup) || pb3.intersects(AIup) || pb4.intersects(AIup) || pb5.intersects(AIup) || pb6.intersects(AIup) || (l2.intersects(AIup) && !laser.getHorizontal()) || (l2.intersects(AIup) && !l2.intersects(worms.get(1).getBounds()))) && (worms.get(1).getSuunta() == 3 || worms.get(1).getReverse(worms.get(1)))) {
                int n = (int) (Math.random() * 2);

                switch (n) {
                    case 0:
                        do {
                            BotTurnLeft();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));

                    case 1:
                        do {
                            BotTurnLeft();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));
                }
            }
            //kek
        }

        Rectangle AIdown = getBoundsDown();
        for (int i = 0; i < body.size(); i++) {
            Rectangle MatotailForAI = body.get(i).getBounds();

            Rectangle2D l2 = laser.getBoundsB();
            if ((AIdown.intersects(MatotailForAI) || pb1.intersects(AIdown) || pb2.intersects(AIdown) || pb3.intersects(AIdown) || pb4.intersects(AIdown) || pb5.intersects(AIdown) || pb6.intersects(AIdown) || (l2.intersects(AIdown) && !laser.getHorizontal()) || (l2.intersects(AIdown) && !l2.intersects(worms.get(1).getBounds()))) && (worms.get(1).getSuunta() == 4 || worms.get(1).getReverse(worms.get(1)))) {
                int n = (int) (Math.random() * 2);

                switch (n) {
                    case 0:
                        do {
                            BotTurnLeft();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));

                    case 1:
                        do {
                            BotTurnLeft();
                            break;
                        } while (l2.intersects(worms.get(1).getBounds()));
                }
            }

        }

    }

    public Rectangle getBoundsLeft() {
        return new Rectangle(worms.get(1).getX() - 50, worms.get(1).getY(), 35, 42);
    }

    public Rectangle getBoundsRight() {
        return new Rectangle(worms.get(1).getX(), worms.get(1).getY(), 85, 42);
    }

    public Rectangle getBoundsUp() {
        return new Rectangle(worms.get(1).getX(), worms.get(1).getY() - 50, 35, 42);
    }

    public Rectangle getBoundsDown() {
        return new Rectangle(worms.get(1).getX(), worms.get(1).getY(), 35, 92);
    }

    public void BotTurnLeft() {
        worms.get(1).setSuunta(1);
        worms.get(1).setSuuntaAdv(2);
    }

    public void BotTurnRight() {
        worms.get(1).setSuunta(2);
        worms.get(1).setSuuntaAdv(2);
    }

    public void BotTurnUp() {
        worms.get(1).setSuunta(3);
        worms.get(1).setSuuntaAdv(1);
    }

    public void BotTurnDown() {
        worms.get(1).setSuunta(4);
        worms.get(1).setSuuntaAdv(1);
    }

    public void yksinpeliTrue() {
        this.pelimoodi = "sp";
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            control.keyPressed(e);
        }
    }

    public void submitHighscore() {
        if (!ingame) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    TextInputDialog dialog = new TextInputDialog("Type your name here!");
                    dialog.setTitle("Highscore");
                    dialog.setHeaderText("Submit your highscore!\n " + score);
                    dialog.setContentText("Please enter your name:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        hscore.setHighscore(score);
                        hscore.setName(result.get());
                        connection.submitScore(hscore.getHighscore(), hscore.getName(), pelimoodi);
                        connection.showHighscore(pelimoodi);

                    }
                }
            });
        }

    }
}
