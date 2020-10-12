package com.ruqi.appserver.ruqi.geomesa.db.updateListener;

import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import org.opengis.feature.simple.SimpleFeature;

/**
 * 具体请对照表 {@link GeoTable#getRecommendRecordSimpleType(String, boolean)}
 * 记录表，变更cityCode
 */
public class RecommendRecordDataUpdater implements GeoDbHandler.IUpdateDataListener {

    @Override
    public void updateData(SimpleFeature oldData, SimpleFeature newData) {
        oldData.setAttribute(GeoTable.KEY_CITY_CODE, newData.getAttribute(GeoTable.KEY_CITY_CODE));
    }
}
