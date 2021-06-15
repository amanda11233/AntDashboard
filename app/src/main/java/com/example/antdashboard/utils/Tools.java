package com.example.antdashboard.utils;

import java.util.List;

public class Tools {

    public static int convertToInt(String str){
        try{
            return Integer.parseInt(str);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public static double calculatingAverageSpeed(List<Integer> speedArr){
        Integer sum = 0;
        if(!speedArr.isEmpty()){
            for (Integer speed : speedArr){
                sum += speed;
            }
            return sum.doubleValue() / speedArr.size();
        }
        return sum;
    }



}
