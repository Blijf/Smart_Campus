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
    private static ArcGISMap buildings;
    private static FeatureLayer featureLayerBuildings;
    private static FeatureLayer featureLayerFloors;
    private static FeatureCollectionLayer featureCollectionLayerFloor;
    public static int floorNum=0;
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
        buildings.getOperationalLayers().remove(featureLayerFloors);
    }



    public static void quePlanta(MapView mMapView, int planta)
    {

        String url = "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/BuildingInteriorbyFloorMovil/MapServer/"+planta;
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        featureLayerFloors = new FeatureLayer(serviceFeatureTable);
        ArcGISMap map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayerFloors);
    }
    //---------------------------------------------------------------------------------------------
    public static  void  addFloorPlanes(final MapView mMapView)
    {
        //create query parameters
        QueryParameters queryParams = new QueryParameters();
        // 1=1 will give all the features from the table
        queryParams.setWhereClause("FLOOR='"+floorNum+"'");

        FeatureTable featureTable = new ServiceFeatureTable("http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/UJIBuildingInteriorNew/MapServer/0");


        final ListenableFuture<FeatureQueryResult> queryResult = featureTable.queryFeaturesAsync(queryParams);
        queryResult.addDoneListener(new Runnable() {
            @Override public void run() {
                try {
                    //create a feature collection table from the query results
                    FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(queryResult.get());

                    //create a feature collection from the above feature collection table
                    FeatureCollection featureCollection = new FeatureCollection();
                    featureCollection.getTables().add(featureCollectionTable);

                    //create a feature collection layer
                    featureCollectionLayerFloor= new FeatureCollectionLayer(featureCollection);

                    //add the layer to the operational layers array
                    mMapView.getMap().getOperationalLayers().add(featureCollectionLayerFloor);
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "Error in FeatureQueryResult: " + e.getMessage());
                }
            }
        });
    }

    public static  void  addFloorRooms(final MapView mMapView)
    {
        //create query parameters
        QueryParameters queryParams = new QueryParameters();
        // 1=1 will give all the features from the table
        queryParams.setWhereClause("FLOOR='"+floorNum+"'");

        FeatureTable featureTable = new ServiceFeatureTable("http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/UJIBuildingInterior/MapServer/1");


        final ListenableFuture<FeatureQueryResult> queryResult = featureTable.queryFeaturesAsync(queryParams);
        queryResult.addDoneListener(new Runnable() {
            @Override public void run() {
                try {
                    //create a feature collection table from the query results
                    FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(queryResult.get());

                    //create a feature collection from the above feature collection table
                    FeatureCollection featureCollection = new FeatureCollection();
                    featureCollection.getTables().add(featureCollectionTable);


                    //create a feature collection layer
                    FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(featureCollection);

                    //add the layer to the operational layers array
                    mMapView.getMap().getOperationalLayers().add(featureCollectionLayer);
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "Error in FeatureQueryResult : " + e.getMessage());
                }
            }
        });
    }
    public static  void addFloorInfo(final ArcGISMap map, final MapView mMapView)
    {
        final FeatureLayer mFeaturelayer;
        ServiceFeatureTable mServiceFeatureTable;

        mMapView.setMap(map);

        mServiceFeatureTable = new ServiceFeatureTable("http://smartcampus.sg.uji.es:6080/arcgis/rest/services/SmartCampus/UJIBuildingInterior/MapServer/1");
        // create the feature layer using the service feature table
        mFeaturelayer = new FeatureLayer(mServiceFeatureTable);
        mFeaturelayer.setOpacity(0.8f);
        //override the renderer
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        mFeaturelayer.setRenderer(new SimpleRenderer(fillSymbol));

        // add the layer to the map
        map.getOperationalLayers().add(mFeaturelayer);

        // zoom to a view point of the USA
//        mMapView.setViewpointCenterAsync(new Point(-11000000, 5000000, SpatialReferences.getWebMercator()), 100000000);

        // clear any previous selections
        mFeaturelayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();

        query.setWhereClause("FLOOR='1'");

        // call select features
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        // add done loading listener to fire when the selection returns
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // call get on the future to get the result
                    FeatureQueryResult result = future.get();

                    // check there are some results
                    if (result.iterator().hasNext()) {

                        // get the extend of the first feature in the result to zoom to
                        Feature feature;
                        feature = result.iterator().next();
                        Envelope envelope = feature.getGeometry().getExtent();
                        mMapView.setViewpointGeometryAsync(envelope, 10);

                        //Select the feature
                        mFeaturelayer.selectFeature(feature);

                        mMapView.getMap().getOperationalLayers().add(mFeaturelayer);

                    } else {
//                        Toast.makeText(MainActivity.this, "No states found with name: " + searchString, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "Feature search failed for: " + searchString + ". Error=" + e.getMessage(),Toast.LENGTH_SHORT).show();
//                    Log.e(getResources().getString(R.string.app_name), "Feature search failed for: " + searchString + ". Error=" + e.getMessage());
                }
            }
        });
    }
}
