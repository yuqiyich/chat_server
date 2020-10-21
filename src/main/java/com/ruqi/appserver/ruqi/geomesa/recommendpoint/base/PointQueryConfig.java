package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;


import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PointQueryConfig {
    private List<IPointQueryStrategy> pointQueryStrategys=new ArrayList<>();
    private LinkedList<IPointFilter> pointFilters=new LinkedList<>();//串行数据
    private String mEvn= RPHandleManager.PRO;

     public  PointQueryConfig(){

     }

     public PointQueryConfig  addQueryStrategy(IPointQueryStrategy strategy){
         pointQueryStrategys.add(strategy);
         return  this;
     }

    public PointQueryConfig  addPointFilter(IPointFilter filter){
        pointFilters.add(filter);
        return  this;
    }

    /**
     *
     * @param filter
     * @param rePrimary  重要的程度，值越小权重越高 0~无穷大
     * @return
     */
    public PointQueryConfig  addPointFilter(IPointFilter filter,int rePrimary){
            pointFilters.add(rePrimary,filter);
        return  this;
    }

    public List<IPointQueryStrategy> getPointQueryStrategys() {
        return pointQueryStrategys;
    }

    public void setPointQueryStrategys(List<IPointQueryStrategy> pointQueryStrategys) {
        this.pointQueryStrategys = pointQueryStrategys;
    }

    public LinkedList<IPointFilter> getPointFilters() {
        return pointFilters;
    }

    public void setPointFilters(LinkedList<IPointFilter> pointFilters) {
        this.pointFilters = pointFilters;
    }
}
