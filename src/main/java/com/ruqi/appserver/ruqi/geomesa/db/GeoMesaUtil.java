package com.ruqi.appserver.ruqi.geomesa.db;

import org.geotools.util.Converter;
import org.locationtech.geomesa.utils.geotools.converters.JavaTimeConverterFactory;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class GeoMesaUtil {
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

   public static String getGeoMesaTimeStr(Date date){
        JavaTimeConverterFactory factory=new JavaTimeConverterFactory();
        Converter converter= factory.createConverter(Date.class,String.class,null);
        String dateStr= "";
        try {
            dateStr = converter.convert(date, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}
