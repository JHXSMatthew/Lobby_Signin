package com.mcndsj.lobby_signin.utils;

import com.mcndsj.JHXSMatthew.Shared.LobbyManager;
import com.mcndsj.JHXSMatthew.Shared.MoneyManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Matthew on 3/07/2016.
 */
public class SQLUtils {


    //TODO: finish this , attention, return a 0,-1,0 to indicates not sign before!!!
    public static SignInfoContainer getSignInfo(String name){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int month = -1;
        long lastSign = 0;
        int stack = 0;

        try {
            connection = LobbyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                System.err.println("error db1!");

                return new SignInfoContainer(month,lastSign,stack);
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM `aSignTable` Where `name`='"+name+"';");
            if(resultSet.next()){
                lastSign = resultSet.getLong("sign");
                stack = resultSet.getInt("stack");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (statement != null) try { statement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }

        //month
        try {
            connection = MoneyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                System.err.println("error db2!");
                return new SignInfoContainer(month,lastSign,stack);
            }
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT lastReward FROM `vipStats` Where `name`='"+name+"';");
            if(resultSet.next()){
                month = resultSet.getInt("lastReward");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            if (statement != null) try { statement.close(); } catch (SQLException e) {e.printStackTrace();}
            if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }


        return  new SignInfoContainer(month,lastSign,stack);
    }

    public static void setLastSign(String name,long time,int stack,boolean first){

        Connection connection = null;
        Statement statement = null;
        try {
            connection = LobbyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                return;
            }
            statement = connection.createStatement();
            if(first){
                statement.executeUpdate("INSERT INTO `aSignTable` (`name`,`sign`,`stack`) VALUES ('"+ name+"','"+ time + "','"+ stack + "');");
            }else{
                statement.executeUpdate("UPDATE `aSignTable` SET `sign`='" + time + "',`stack`='" +stack+  "' Where `name`='"+ name +"';");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setVipLastSignMonth(String name, int month){

        Connection connection = null;
        Statement statement = null;
        try {
            connection = MoneyManager.getInstance().getConnection();
            if(connection == null || connection.isClosed()){
                return;
            }
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `vipStats` SET `lastReward`='"+ month +"' Where `name`='"+name+"';");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
        }
        if (statement != null) try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
