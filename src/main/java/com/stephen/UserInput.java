package com.stephen;

import java.util.ArrayList;
import java.util.Collections;
import static java.lang.Math.random;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class UserInput {
    private static final Logger log = LoggerFactory.getLogger(UserInput.class);


    // --- CONSTRUCTOR ---
    public UserInput(){
    }


    // --- GETTERS ---
    public static boolean getFrameResult(){
        boolean user1Input;
        boolean user2Input;
        do {
            user1Input = getRandomBool();
            user2Input = getRandomBool();
        } while (user1Input != user2Input);
        return user1Input;
    }

    private static boolean getRandomBool(){
        log.info("Generating random boolean value...");
        return random() > 0.5;
    }

    public static boolean getBreakDish(){
        double r = random();
        log.info("Checking if break dish occurs...");
        if (r > 0.9 || r < 0.1){
            return getFrameResult();
        }else{
            return false;
        }
    }


    // --- LOGIC ---
    public static Player pickPlayer(ArrayList<Player> playerArrayList){
        log.info("Picking player from list...");
        Collections.shuffle(playerArrayList);
        return playerArrayList.getFirst();
    }

    public static boolean shotResult_KILLER(){
        double r = random();
        log.info("Generating shot result...");
        return r > 0.8 || r < 0.2;
    }

    public static boolean shotBlackBall_KILLER(){
        double r = random();
        log.info("Generating black ball shot result...");
        return r < 0.033 || r > 0.096;
    }
}
