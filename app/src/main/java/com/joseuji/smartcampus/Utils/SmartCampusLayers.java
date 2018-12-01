package com.joseuji.smartcampus.Utils;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class SmartCampusLayers
{
    private static ArcGISMap buildings;
    private static FeatureLayer featureLayerBuildings;
    public static void baseBuildings(MapView mMapView)
    {
        String url = "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/BaseBuildings/MapServer/0";
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        ArcGISMap map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);
    }

    public static void buildings(MapView mMapView)
    {
        String url = "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/Buildings/MapServer/0";
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        featureLayerBuildings= new FeatureLayer(serviceFeatureTable);
        buildings = mMapView.getMap();
        buildings.getOperationalLayers().add(featureLayerBuildings);
    }

    public static void deleteBuildings()
    {
        buildings.getOperationalLayers().remove(featureLayerBuildings);
    }
}
