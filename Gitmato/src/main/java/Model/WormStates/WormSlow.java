package Model.WormStates;

import Model.Worm;

import java.util.Timer;
import java.util.TimerTask;

public class WormSlow extends WormState {

    //create a singleton instance of this class
    private static WormSlow instance = null;

    private WormSlow() {}

    /**
     * Class 'constructor'. Retrieves this class's instance.
     * This class is defined as a singleton, so it is only ever created once.
     * @return the instance of this class
     */
    public static WormSlow getInstance() {
        if (instance == null) {
            instance = new WormSlow();
        }
        return instance;
    }

    /**
     * Slows down the worm object temporarily.
     * @param worm the worm that is supposed to be slowed down
     */
    @Override
    public void action(Worm worm) {
        //set speed of this worm to be less
        worm.setSpeed(worm.getSpeed()-2);

        //create a timer, after which call on normal state
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //normalize the speed again
                worm.changeState(WormStateNormal.getInstance());
                }
            }, 5000); //delay (in ms) after which everything under timer.schedule is done
        }
}