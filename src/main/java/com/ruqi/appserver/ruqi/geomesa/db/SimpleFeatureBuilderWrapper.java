package com.ruqi.appserver.ruqi.geomesa.db;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFeatureBuilderWrapper extends SimpleFeatureBuilder {
    private static Logger logger = LoggerFactory.getLogger(SimpleFeatureBuilderWrapper.class);
     private SimpleFeatureType featureTypeCopy;
    public SimpleFeatureBuilderWrapper(SimpleFeatureType featureType) {
        super(featureType);
        this.featureTypeCopy=featureType;
    }

    public SimpleFeatureBuilderWrapper(SimpleFeatureType featureType, FeatureFactory factory) {
        super(featureType, factory);
        this.featureTypeCopy=featureType;
    }

    @Override
    public void set(String name, Object value) {
        int index = featureTypeCopy.indexOf(name);
        if (index == -1) {
           logger.error("no"+name+" attr exist in ["+featureTypeCopy.getTypeName()+"]  SFT ,please check in GeoMesaDataWrapper class  ");
           return ;
        }
        super.set(name, value);
    }
}
