package com.mcndsj.lobby_signin.utils;

/**
 * Created by Matthew on 3/07/2016.
 */
public class VIPUtils {
    public static int vipLevelToMonthChest(int level){
        switch(level){
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 5;
        }
        return 0;
    }

}
