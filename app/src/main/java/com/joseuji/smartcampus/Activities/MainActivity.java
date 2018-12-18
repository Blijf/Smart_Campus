package com.joseuji.smartcampus.Activities;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.joseuji.smartcampus.ClientWeb.Consultas;
import com.joseuji.smartcampus.ClientWeb.Controller;
import com.joseuji.smartcampus.ClientWeb.RetrofitServices;
import com.joseuji.smartcampus.Models.Asignaturas;
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;
import com.joseuji.smartcampus.R;
import com.joseuji.smartcampus.Utils.SmartCampusLayers;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Ubicacion ubicacion;
    private TextView textView;
    private ToggleButton tbBuildings, tbFloors;
    private Controller controller;
    private EditText etSearch;
    private Button btSearch,btFloorS, btFloor0, btFloor1,btFloor2,btFloor3,btFloor4,btFloor5,btFloor6;
    private LinearLayout linearLayoutFloors;
    ArcGISMap map;

    /**************************************************************************************************
     * *                                   ONCREATE()
     * ********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        //----------------------------------------------------------------------------------
        //                          INICIALIZACIÓN DE LAS VARIABLES
        //----------------------------------------------------------------------------------
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        tbBuildings= findViewById(R.id.tgBuildings);
        tbFloors= findViewById(R.id.tgFloors);
        etSearch= findViewById(R.id.etSearch);
        btSearch= findViewById(R.id.btSearch);
        btFloorS=findViewById(R.id.tgFloorS);btFloor0=findViewById(R.id.tgFloor0);btFloor1=findViewById(R.id.tgFloor1);btFloor2=findViewById(R.id.tgFloor2);
        btFloor3=findViewById(R.id.tgFloor3);btFloor4=findViewById(R.id.tgFloor4);btFloor5=findViewById(R.id.tgFloor5);btFloor6=findViewById(R.id.tgFloor6);
        textView= findViewById(R.id.tvTexto);
        linearLayoutFloors=findViewById(R.id.linearLayoutFloors);
        controller = new Controller();
        retrofitServices=controller.start();
        //----------------------------------------------------------------------------------
        //                          LLAMADA A LOS MÉTODOS
        //----------------------------------------------------------------------------------
        setupMap();//configuración del mapa
        setupLocationDisplay();//ubicación

        //addLayers
        SmartCampusLayers.baseBuildings(mMapView);
//      SmartCampusLayers.addFloorRooms(mMapView);
//      SmartCampusLayers.addFloorInfo(map, mMapView);
        //----------------------------------------------------------------------------------
        //                              CONSULTAS
        //----------------------------------------------------------------------------------
//        Consultas.getUbicaciones(retrofitServices,getApplicationContext());
//        Consultas.getAsignaturas(retrofitServices,getApplicationContext());


        //----------------------------------------------------------------------------------
        /**************************************************************************************************
         * *                                   BOTONES
         * *********************************************************************************************/
        tbBuildings.setText(null);
        tbBuildings.setTextOn(null);
        tbBuildings.setTextOff(null);
        tbBuildings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    tbBuildings.setBackgroundDrawable(getDrawable(R.drawable.buildings_on));
                    SmartCampusLayers.buildings(mMapView);
                }
                else
                {
                    tbBuildings.setBackgroundDrawable(getDrawable(R.drawable.buildings_off));
                    SmartCampusLayers.deleteBuildings();
                }
            }
        });
        tbFloors.setText(null);
        tbFloors.setTextOn(null);
        tbFloors.setTextOff(null);
        tbFloors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    tbFloors.setBackgroundDrawable(getDrawable(R.drawable.floors_on));
//                    SmartCampusLayers.addFloorPlanes(mMapView);
                    SmartCampusLayers.quePlanta(mMapView,3);
                    linearLayoutFloors.setVisibility(View.VISIBLE);

                }
                else
                {
                    tbFloors.setBackgroundDrawable(getDrawable(R.drawable.floors_off));
                    SmartCampusLayers.deleteFloors();
                    linearLayoutFloors.setVisibility(View.GONE);
                }
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                    for (int i=0; i<=Consultas.ubicaciones.getContent().size(); i++)
                    {
                        ubicacion= Consultas.ubicaciones.getContent().get(i);

                        if(ubicacion.getDescripcion().contains(etSearch.getText())||ubicacion.getEdificio().contains(etSearch.getText()))
                        {
                            textView.setText("existe");
                        }
                        else
                        {
                            textView.setText("No existe");
                        }
                    }
            }

        });

        //----------------------------------------------------------------------------------------
        //                                      PISOS
        //----------------------------------------------------------------------------------------
        btFloorS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,1);//plano
                SmartCampusLayers.quePlanta(mMapView,2);//Interior Spaces
            }
        });
        btFloor0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,4);//plano
                SmartCampusLayers.quePlanta(mMapView,5);//Interior Spaces
            }
        });
        btFloor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,7);//plano
                SmartCampusLayers.quePlanta(mMapView,8);//Interior Spaces
            }
        });
        btFloor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,10);//plano
                SmartCampusLayers.quePlanta(mMapView,11);//Interior Spaces
            }
        });
        btFloor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,13);//plano
                SmartCampusLayers.quePlanta(mMapView,14);//Interior Spaces
            }
        });
        btFloor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,16);//plano
                SmartCampusLayers.quePlanta(mMapView,17);//Interior Spaces
            }
        });
        btFloor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,19);//plano
                SmartCampusLayers.quePlanta(mMapView,20);//Interior Spaces
            }
        });
        btFloor6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.quePlanta(mMapView,22);//plano
                SmartCampusLayers.quePlanta(mMapView,23);//Interior Spaces
            }
        });

    }
    /**************************************************************************************************
     * *                                   MÉTODOS
     * *********************************************************************************************/
    private void setupMap() {
        if (mMapView != null) {

//             If online basemap is desirable, uncomment the following lines
            Basemap.Type basemapType = Basemap.Type.DARK_GRAY_CANVAS_VECTOR;

            double latitude=39.994444;
            double longitude = -0.068889;

            int levelOfDetail = 17;
            map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
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
        Consultas.getUbicaciones(retrofitServices,getApplicationContext());

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
