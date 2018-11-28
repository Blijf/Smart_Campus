package com.joseuji.smartcampus.Utils;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class SmartCampusLayers
{
    public static void baseLayer(MapView mMapView)
    {
        String url = "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/BaseBuildings/MapServer/0";
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        ArcGISMap map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);
    }
}
