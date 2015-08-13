package com.gviryaskin.avangardsender.util;

import java.util.Date;

/**
 * Created by gviryaskin on 16.07.15.
 */
public class HistoryItem {
    protected Date mDate;
    protected double mCold,
                   mHot,
                   mColdDif,
                   mHotDif;

    public Date getDate(){return mDate;}
    public double getCold(){return mCold;}
    public double getHot(){return mHot;}
    public double getColdDif(){return mColdDif;}
    public double getHotDif(){return mHotDif;}

    public HistoryItem(Date aDate, double aCold,double aHot,double aColdDif,double aHotDif){
        mDate=aDate;
        mCold=aCold;
        mHot=aCold;
        mColdDif=aColdDif;
        mHotDif=aHotDif;
    }

}
