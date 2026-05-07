package com.stephen.Functions;

import java.util.ArrayList;
import java.util.Collections;
import static java.lang.Math.random;
import com.stephen.Player.Player;


public class UserInput {


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
        return random() > 0.5;
    }

    public static boolean getBreakDish(){
        double r = random();
        if (r > 0.9 || r < 0.1){
            return getFrameResult();
        }else{
            return false;
        }
    }


    // --- LOGIC ---
    public static Player pickPlayer(ArrayList<Player> playerArrayList){
        Collections.shuffle(playerArrayList);
        return playerArrayList.getFirst();
    }

    public static boolean shotResult_KILLER(){
        double r = random();
        return r > 0.8 || r < 0.2;
    }

    public static boolean shotBlackBall_KILLER(){
        double r = random();
        return r < 0.033 || r > 0.096;
    }

    public static boolean foulResult_KILLER(){
        double r = random();
        return r > 0.9 || r < 0.1;
    }
}
