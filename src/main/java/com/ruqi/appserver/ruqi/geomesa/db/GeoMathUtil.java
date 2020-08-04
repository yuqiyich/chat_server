package com.ruqi.appserver.ruqi.geomesa.db;

import java.text.DecimalFormat;

public class GeoMathUtil {
    public static String getPrecision(double selectLng, int precision) {
        StringBuilder pattern=  new StringBuilder("#.");
        for (int i = 0; i < precision; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return  df.format(selectLng);
    }

    public static double getPrecisionDouble(double selectLng, int precision) {
        StringBuilder pattern=  new StringBuilder("#.");
        for (int i = 0; i < precision; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return  Double.parseDouble(df.format(selectLng));
    }
}
