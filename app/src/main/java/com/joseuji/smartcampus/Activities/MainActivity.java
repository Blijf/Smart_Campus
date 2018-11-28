package com.joseuji.smartcampus.Activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.joseuji.smartcampus.ClientWeb.Consultas;
import com.joseuji.smartcampus.ClientWeb.Controller;
import com.joseuji.smartcampus.ClientWeb.RetrofitServices;
import com.joseuji.smartcampus.Models.Ubicaciones;
import com.joseuji.smartcampus.R;
import com.joseuji.smartcampus.Utils.SmartCampusLayers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    /**************************************************************************************************
     * *                                    VARIABLES
     * *********************************************************************************************/
    private RetrofitServices retrofitServices;
    private MapView mMapView;
    private LocationDisplay mLocationDisplay;
    List<Ubicaciones>ubicaciones;
    Controller controller;

    /**************************************************************************************************
     * *                                   ONCREATE()
     * *********************************************************************************************/
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
        setupMap();//configuración del mapa
        setupLocationDisplay();//ubicación

        //addLayers
        SmartCampusLayers.baseLayer(mMapView);

        //----------------------------------------------------------------------------------
        //                              CONSULTAS
        //----------------------------------------------------------------------------------
        //ubicaciones=Consultas.getUbicaciones(retrofitServices,getApplicationContext());
        //----------------------------------------------------------------------------------

    }
    /**************************************************************************************************
     * *                                   MÉTODOS
     * *********************************************************************************************/
    private void setupMap() {
        if (mMapView != null) {

//             If online basemap is desirable, uncomment the following lines
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            double latitude=39.994444;
            double longitude = -0.068889;

            int levelOfDetail = 15;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);

            mMapView.setMap(map);
        }
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();

        // If a different (from default) location source is required, uncomment and make proper changes to the following line
        mLocationDisplay.setLocationDataSource( new AndroidLocationDataSource( this, LocationManager.NETWORK_PROVIDER, 50L, 1 ) );

        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
                if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null)
                {
                    return;
                }
                int requestPermissionsCode = 2;
                String[] requestPermissions = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE};

                if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[2]) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
                }
                else {
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

    /**************************************************************************************************
* *                             MÉTODOS PROPIOS DE ANDROID
     * *********************************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            mLocationDisplay.startAsync();
        }
        else
        {
            Toast.makeText(MainActivity.this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        ubicaciones=Consultas.getUbicaciones(retrofitServices,getApplicationContext());

        //volvemos a reubicar la ubicación actual tras quitar de primer plano la app(fijarse en ciclo de vida)
        setupLocationDisplay();
        mMapView.resume();
    }
    @Override
    protected void onDestroy()
    {
        mMapView.dispose();
        super.onDestroy();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.pause();
    }
}
