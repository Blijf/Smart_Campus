package com.joseuji.smartcampus.Activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.joseuji.smartcampus.ClientWeb.Consultas;
import com.joseuji.smartcampus.ClientWeb.Controller;
import com.joseuji.smartcampus.ClientWeb.RetrofitServices;
import com.joseuji.smartcampus.Models.Ubicaciones;
import com.joseuji.smartcampus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private RetrofitServices retrofitServices;
    private MapView mMapView;
    private LocationDisplay mLocationDisplay;
    List<Ubicaciones>ubicaciones;
    Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        //----------------------------------------------------------------------------------
        //                          INICIALIZACIÓN DE LAS VARIABLES
        //----------------------------------------------------------------------------------
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        controller = new Controller();
        retrofitServices=controller.start();
        //----------------------------------------------------------------------------------
        //                          LLAMADA A LOS MÉTODOS
        //----------------------------------------------------------------------------------
        setupMap();
        setupLocationDisplay();

        ubicaciones=Consultas.getUbicaciones(retrofitServices,getApplicationContext());

        //----------------------------------------------------------------------------------

    }

    //----------------------------------------------------------------------------------
    //                          MÉTODOS DE CONFIGURACIÓN DE ESRI
    //----------------------------------------------------------------------------------
    private void setupMap() {
        if (mMapView != null) {

//             If online basemap is desirable, uncomment the following lines
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            double latitude=39.994444;
            double longitude = -0.068889;

            int levelOfDetail = 15;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);

            mMapView.setMap(map);

            // The following line should work for most phones, but just to be sure...
//            String path = getBaseContext().getExternalFilesDir(null)+"/"+getBaseContext().getResources().getResourceEntryName(R.raw.uji_levels);
            String path = moveResFile();

            if (path == null){
                return;
            }

            final Geodatabase geodatabase = new Geodatabase(path);


            // There are more featureTables in the geodatabase, the following are just an example
            final int LAYER_ID_AREAS = 3;
            final int LAYER_ID_PLAN = 4;
            final int LAYER_ID_SHELVES = 5;

            geodatabase.loadAsync();
            geodatabase.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                        addLayer(LAYER_ID_AREAS);
                        addLayer(LAYER_ID_PLAN);
                        addLayer(LAYER_ID_SHELVES);
                    }
                }

                private void addLayer(int id){
                    FeatureTable featureTable = geodatabase.getGeodatabaseFeatureTableByServiceLayerId(id);
                    FeatureLayer featureLayer = new FeatureLayer(featureTable);
                    featureLayer.setVisible(true);
                    mMapView.getMap().getOperationalLayers().add(featureLayer);
                }
            });
        }
    }

    // As stated above, unnecessary for most phones
    private String moveResFile(){
        try {
            InputStream input = getApplicationContext().getResources().openRawResource(R.raw.uji_levels);
            File tempFile = File.createTempFile("geodb.dgb", null, getApplicationContext().getCacheDir());
            FileOutputStream output = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            while (true) {
                int size = input.read(buffer);
                if (size == -1) {
                    break;
                }
                output.write(buffer, 0, size);
            }

            input.close();
            output.close();

            return tempFile.getAbsolutePath();
        }
        catch (Exception e){
            return null;
        }
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();

        // If a different (from default) location source is required, uncomment and make proper changes to the following line
//        mLocationDisplay.setLocationDataSource( new AndroidLocationDataSource( this, LocationManager.NETWORK_PROVIDER, 50L, 1 ) );

        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
                if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                    return;
                }
                int requestPermissionsCode = 2;
                String[] requestPermissions = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE};

                if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[2]) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
                } else {
                    String message = String.format("Error in DataSourceStatusChangedListener: %s",
                            dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                    mLocationDisplay.startAsync();
                }
                mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
            }
        });
        mLocationDisplay.startAsync();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(MainActivity.this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ubicaciones=Consultas.getUbicaciones(retrofitServices,getApplicationContext());

    }
}
