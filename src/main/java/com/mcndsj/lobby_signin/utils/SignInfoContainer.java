package com.mcndsj.lobby_signin.utils;

/**
 * Created by Matthew on 3/07/2016.
 */
public class SignInfoContainer {

    private long lastSign = 0;
    private int stack = 0;
    private int lastSignMonth = -1;

    public SignInfoContainer(int month,long last, int stack){
        this.lastSignMonth = month;
        this.lastSign = last;
        this.stack = stack;
    }

    public int getStack(){
        return stack;
    }

    public long getLastSign(){
        return lastSign;
    }

    public int getLastSignMonth(){
        return lastSignMonth;
    }

    public void setStack(int s){
        this.stack = s;
    }

}
