package com.ruqi.appserver.ruqi.geomesa.db.updateListener;

import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Date;

/**
 *
 * 具体请对照表 {@link GeoTable#getRecommendPointSimpleType(String)}
 */
public class RecommendPointUpdater implements GeoDbHandler.IUpdateDataListener {

    @Override
    public void updateData(SimpleFeature oldData, SimpleFeature newData) {
        int updateTime = (int) oldData.getAttribute(GeoTable.KEY_UPDATE_COUNT);
        int oldChannel= (int) newData.getAttribute(GeoTable.KEY_CHANNEL);
        int newChannel= (int) oldData.getAttribute(GeoTable.KEY_CHANNEL);
        oldData.setAttribute(GeoTable.KEY_CHANNEL,newChannel|oldChannel);
        oldData.setAttribute(GeoTable.KEY_UPDATE_COUNT,updateTime+1);
        oldData.setAttribute(GeoTable.KEY_DATE,new Date(System.currentTimeMillis()));
        oldData.setAttribute(GeoTable.KEY_TITLE,newData.getAttribute(GeoTable.KEY_TITLE));
        oldData.setAttribute(GeoTable.KEY_ADDRESS,newData.getAttribute(GeoTable.KEY_ADDRESS));
    }
}
