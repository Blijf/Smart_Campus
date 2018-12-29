package com.joseuji.smartcampus.Activities;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.joseuji.smartcampus.ClientWeb.Consultas;
import com.joseuji.smartcampus.ClientWeb.Controller;
import com.joseuji.smartcampus.ClientWeb.RetrofitServices;
import com.joseuji.smartcampus.Models.Asignaturas;
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.Models.Ubicaciones;
import com.joseuji.smartcampus.R;
import com.joseuji.smartcampus.Utils.SmartCampusLayers;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    //Variables para rutas
    private GraphicsOverlay mGraphicsOverlay;
    private Point mStart;
    private Point mEnd;//=  new Point(4864935.06075, -7686.1280499994755,0.0);

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

        //Se muestran los planos y la info de la planta cero al iniciar la Aplicación
        SmartCampusLayers.quePlanta(mMapView,4);//plano
        SmartCampusLayers.quePlanta(mMapView,5);//Interior Spaces
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
                Consultas.getUbicaciones(retrofitServices, getApplicationContext(), String.valueOf(etSearch.getText()));
                Point busqueda=new Point(Consultas.latitud, Consultas.longitud,Consultas.altitud); //Punto buscado por el usuario

                //Se marca en el mapa y se
                setMapMarker(busqueda, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(226, 119, 40), Color.BLUE);
                mEnd=busqueda;

                Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
                int levelOfDetail = 17;
                map = new ArcGISMap(basemapType, mEnd.getY(), mEnd.getX(), levelOfDetail);
                mMapView.setMap(map);
            }

        });

        //----------------------------------------------------------------------------------------
        //                                      PISOS
        //----------------------------------------------------------------------------------------
        btFloorS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                SmartCampusLayers.quePlanta(mMapView,1);//plano
                SmartCampusLayers.quePlanta(mMapView,2);//Interior Spaces
            }
        });
        btFloor0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos


                SmartCampusLayers.quePlanta(mMapView,4);//plano
                SmartCampusLayers.quePlanta(mMapView,5);//Interior Spaces
            }
        });
        btFloor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos

                SmartCampusLayers.quePlanta(mMapView,7);//plano
                SmartCampusLayers.quePlanta(mMapView,8);//Interior Spaces
            }
        });
        btFloor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                SmartCampusLayers.quePlanta(mMapView,10);//plano
                SmartCampusLayers.quePlanta(mMapView,11);//Interior Spaces
            }
        });
        btFloor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos

                SmartCampusLayers.quePlanta(mMapView,13);//plano
                SmartCampusLayers.quePlanta(mMapView,14);//Interior Spaces
            }
        });
        btFloor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos

                SmartCampusLayers.quePlanta(mMapView,16);//plano
                SmartCampusLayers.quePlanta(mMapView,17);//Interior Spaces
            }
        });
        btFloor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                SmartCampusLayers.quePlanta(mMapView,19);//plano
                SmartCampusLayers.quePlanta(mMapView,20);//Interior Spaces
            }
        });
        btFloor6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                SmartCampusLayers.quePlanta(mMapView,22);//plano
                SmartCampusLayers.quePlanta(mMapView,23);//Interior Spaces
            }
        });


        // Rutas
        createGraphicsOverlay();
        setupOAuthManager();

    }
    /**************************************************************************************************
     * *                                   MÉTODOS
     * *********************************************************************************************/
    private void setupMap() {
        if (mMapView != null) {

//             If online basemap is desirable, uncomment the following lines
            //Basemap.Type basemapType = Basemap.Type.DARK_GRAY_CANVAS_VECTOR;
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;

            double latitude=39.994444;
            double longitude = -0.068889;

            int levelOfDetail = 17;
            map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);

            // Rutas en pantalla
            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
                @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                    android.graphics.Point screenPoint = new android.graphics.Point(
                            Math.round(e.getX()),
                            Math.round(e.getY()));
                    Point mapPoint = mMapView.screenToLocation(screenPoint);
                    mapClicked(mapPoint);
                    return super.onSingleTapConfirmed(e);
                }
            });

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


    /**************************************************************************************************
                                  MÉTODOS PARA RUTAS
     * *********************************************************************************************/

    private void createGraphicsOverlay() {
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }

    private void setMapMarker(Point location, SimpleMarkerSymbol.Style style, int markerColor, int outlineColor) {
        float markerSize = 8.0f;
        float markerOutlineThickness = 2.0f;
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(style, markerColor, markerSize);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, outlineColor, markerOutlineThickness));
        Graphic pointGraphic = new Graphic(location, pointSymbol);
        mGraphicsOverlay.getGraphics().add(pointGraphic);
    }

    private void setStartMarker(Point location) {
        mGraphicsOverlay.getGraphics().clear();
        setMapMarker(location, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(226, 119, 40), Color.BLUE);
        mStart = location;
        //mEnd = null;
    }

    private void setEndMarker(Point location) {
        setMapMarker(location, SimpleMarkerSymbol.Style.SQUARE, Color.rgb(40, 119, 226), Color.RED);
        mEnd = location;
        //findRoute();
    }

    private void mapClicked(Point location) {
        if (mStart == null) {
            // Start is not set, set it to a tapped location
            setStartMarker(location);
            findRoute();
        } else if (mEnd == null) {
            // End is not set, set it to the tapped location then find the route
            //location=new Point(Consultas.latitud, Consultas.longitud,Consultas.altitud);
            setEndMarker(location);
            //findRoute();
            //mEnd=new Point(Consultas.latitud, Consultas.longitud,Consultas.altitud);
        /*} else {
            // Both locations are set; re-set the start to the tapped location
            setStartMarker(location);
            findRoute();*/
        }
    }

    private void setupOAuthManager() {
        String clientId = getResources().getString(R.string.client_id);
        String redirectUrl = getResources().getString(R.string.redirect_url);

        try {
            OAuthConfiguration oAuthConfiguration = new OAuthConfiguration("https://www.arcgis.com", clientId, redirectUrl);
            DefaultAuthenticationChallengeHandler authenticationChallengeHandler = new DefaultAuthenticationChallengeHandler(this);
            AuthenticationManager.setAuthenticationChallengeHandler(authenticationChallengeHandler);
            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);
        } catch (MalformedURLException e) {
            showError(e.getMessage());
        }
    }
    private void showError(String message) {
        Log.d("FindRoute", message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void findRoute() {
        // Code from the next step goes here

        String routeServiceURI = getResources().getString(R.string.routing_url);
        //final RouteTask solveRouteTask = new RouteTask(getApplicationContext(), routeServiceURI);
        //final RouteTask solveRouteTask = new RouteTask(getApplicationContext(), "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/Network/GermanNet/NAServer/Route");
        final RouteTask solveRouteTask = new RouteTask(getApplicationContext(), "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/Network/GermanNet/NAServer/Route");


        solveRouteTask.loadAsync();
        solveRouteTask.addDoneLoadingListener(() -> {
            // Code from the next step goes here
            if (solveRouteTask.getLoadStatus() == LoadStatus.LOADED) {
                final ListenableFuture<RouteParameters> routeParamsFuture = solveRouteTask.createDefaultParametersAsync();
                routeParamsFuture.addDoneListener(() -> {
                    try {
                        RouteParameters routeParameters = routeParamsFuture.get();
                        List<Stop> stops = new ArrayList<>();
                        stops.add(new Stop(mStart));
                        stops.add(new Stop(mEnd));
                        routeParameters.setStops(stops);
                        // Code from the next step goes here
                        final ListenableFuture<RouteResult> routeResultFuture = solveRouteTask.solveRouteAsync(routeParameters);
                        //routeResultFuture.addDoneListener(() -> {
                        routeResultFuture.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        RouteResult routeResult = routeResultFuture.get();
                                        Route firstRoute = routeResult.getRoutes().get(0);
                                        // Code from the next step goes here

                                        Polyline routePolyline = firstRoute.getRouteGeometry();
                                        SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 4.0f);
                                        Graphic routeGraphic = new Graphic(routePolyline, routeSymbol);
                                        mGraphicsOverlay.getGraphics().add(routeGraphic);

                                    } catch (InterruptedException | ExecutionException e) {
                                        showError("Solve RouteTask failed " + e.getMessage());
                                    }
                                }
                        });


                    } catch (InterruptedException | ExecutionException e) {
                        showError("Cannot create RouteTask parameters " + e.getMessage());
                    }
                });
            } else {
                showError("Unable to load RouteTask " + solveRouteTask.getLoadStatus().toString());
            }

        });
    }

}
