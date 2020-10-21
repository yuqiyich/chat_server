package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;

import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PointFilterChain {
    private LinkedList<PointFilterWrapper> chains;

    public PointFilterChain(LinkedList<PointFilterWrapper> chains){
        this.chains=chains;
    };

   public List<SimpleFeature> filtersWork(double lng, double lat,List<SimpleFeature> inputPoints,String env){
        List<SimpleFeature> filterPoints=new ArrayList<>();
        if (chains!=null&&!chains.isEmpty()){
            int chainSize=chains.size();
            int index = 0;
            while (index < chainSize && filterPoints != null) {
                filterPoints = chains.get(index).doFilter(lng, lat, inputPoints,env);
                index++;
            }

       }
        return filterPoints;
    }
}
