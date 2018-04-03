package Model.WormStates;

import Model.Worm;

import java.util.Timer;
import java.util.TimerTask;

public class WormFast extends WormState {

    //create a singleton instance of this class
    private static WormFast instance;

    private WormFast() {}

    /**
     * Class 'constructor'. Retrieves this class's instance.
     * This class is defined as a singleton, so it is only ever created once.
     * @return the instance of this class
     */
    public static WormFast getInstance() {
        if (instance == null) {
            instance = new WormFast();
        }
        return instance;
    }

    /**
     * Makes the worm object move quicker temporarily.
     * @param worm the worm that's supposed to move quicker
     */
    @Override
    public void action(Worm worm) {
        //set speed of this worm to be higher
        worm.setSpeed(worm.getSpeed()+2);

        //create a new timer
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //normalize the speed again
                worm.changeState(WormStateNormal.getInstance());
            }
        }, 5000); //after this delay (ms)
    }

}