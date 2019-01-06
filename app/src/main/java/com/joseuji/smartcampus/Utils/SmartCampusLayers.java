package com.joseuji.smartcampus.Utils;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.joseuji.smartcampus.Activities.MainActivity;
import com.joseuji.smartcampus.R;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class SmartCampusLayers
{
    private static ArcGISMap buildings, floors;
    private static FeatureLayer featureLayerBuildings;
    private static FeatureLayer featureLayerFloors;
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
    public static void deleteFloors()
    {
        floors.getOperationalLayers().remove(featureLayerFloors);
    }


    //http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/BuildingInteriorbyFloorMovil/MapServer- de esta forma se sabe el layerFloor
    public static void quePlanta(MapView mMapView, int layerFloor)
    {

        String url = "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/BuildingInteriorbyFloorMovil/MapServer/"+layerFloor;
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        featureLayerFloors = new FeatureLayer(serviceFeatureTable);
        floors = mMapView.getMap();
        floors.getOperationalLayers().add(featureLayerFloors);
    }

}
