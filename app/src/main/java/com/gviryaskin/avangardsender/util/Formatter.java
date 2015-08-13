package com.gviryaskin.avangardsender.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by gviryaskin on 15.07.15.
 */
public class Formatter {
    private Formatter(){}

    public final static SimpleDateFormat DATE=new SimpleDateFormat("dd.MM.yyyy");
    public final static DecimalFormat DECIMAL=new DecimalFormat("0.00");

}
