package com.joseuji.smartcampus.Activities;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.AndroidLocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
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
import com.joseuji.smartcampus.Models.Ubicacion;
import com.joseuji.smartcampus.R;
import com.joseuji.smartcampus.Utils.SmartCampusLayers;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
    private ToggleButton tbBuildings, tbFloors;
    private Controller controller;
    private EditText etSearch;
    private Button btSearch,btUbication,btRute,btDeleteRute;
    private ToggleButton tgFloor0,tgFloorS, tgFloor1,tgFloor2,tgFloor3,tgFloor4,tgFloor5,tgFloor6;
    private LinearLayout linearLayoutFloors;
    ArcGISMap map;



    //Variables para rutas
    private GraphicsOverlay mGraphicsOverlay;
    private Point mStart;
    private Point mEnd;
    private int defaultFloor = 0;   // Route computation needs x, y and z (floor)
    // 1 for trying indoor, to try outdoor change it to 0

    //Variables para Layer Show attributes
    private Callout mCallout;
    private ServiceFeatureTable mServiceFeatureTable;

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
        btSearch= findViewById(R.id.btSearch);btUbication= findViewById(R.id.btUbication);btRute= findViewById(R.id.btRute);
        btDeleteRute= findViewById(R.id.btDelete);
        tgFloorS=findViewById(R.id.tgFloorS);tgFloor0=findViewById(R.id.tgFloor0);tgFloor1=findViewById(R.id.tgFloor1);tgFloor2=findViewById(R.id.tgFloor2);
        tgFloor3=findViewById(R.id.tgFloor3);tgFloor4=findViewById(R.id.tgFloor4);tgFloor5=findViewById(R.id.tgFloor5);tgFloor6=findViewById(R.id.tgFloor6);
        linearLayoutFloors=findViewById(R.id.linearLayoutFloors);
        controller = new Controller();
        retrofitServices=controller.start();
        //----------------------------------------------------------------------------------
        //                          LLAMADA A LOS MÉTODOS
        //----------------------------------------------------------------------------------
        setupMap(39.994444,-0.068889);//configuración del mapa
        setupLocationDisplay();//ubicación

        //addLayers
        SmartCampusLayers.baseBuildings(mMapView);

        //Se muestran los planos y la info de la planta cero al iniciar la Aplicación
        SmartCampusLayers.quePlanta(mMapView,4);//plano
        SmartCampusLayers.quePlanta(mMapView,5);//Interior Spaces


        //----------------------------------------------------------------------------------
        /**************************************************************************************************
         * *                                   BOTONES
         * *********************************************************************************************/
        //Ubica el punto inical con la ubicación actual
        btUbication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mStart=mLocationDisplay.getMapLocation();
            }
        });

        //Se calcula la ruta teniendo un punto start y end
        btRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mStart!=null && mEnd!=null)
                {

                     findRoute();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Se necesita el punto inicial y final para encontrar la ruta", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Borramos la ruta generada poniendo los valore a cero
        btDeleteRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se limpian rutas y puntos en el mapa
                mEnd=null;
                mStart=null;
                mGraphicsOverlay.getGraphics().clear();
            }
        });

        //Capa que muestra el nombre de los edificios y destaca estos con un color
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

        //Activa o desactiva la capa y botones de pisos
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
        //----------------------------------------------------------------------------------------
        //                                     BÚSCADOR
        //----------------------------------------------------------------------------------------
        //edit text, se introduce el sitio a buscar
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Point busqueda= Consultas.getPuntoBusqueda(retrofitServices, getApplicationContext(), String.valueOf(etSearch.getText()));
                if(busqueda==null)
                {
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //realizamos la búsqueda del texto introducido en etSearch.
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    //Punto de búsqueda
                    Point  busqueda = Consultas.getPuntoBusqueda(retrofitServices, getApplicationContext(), String.valueOf(etSearch.getText()));
                    //Marca final de la ruta
                    setEndMarker(busqueda, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(40, 119, 226), Color.RED);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Se limpian rutas y puntos en el mapa
                    mEnd=null;
                    mStart=null;
                    mGraphicsOverlay.getGraphics().clear();

                }

            }

        });

        //----------------------------------------------------------------------------------------
        //                                      PISOS
        //----------------------------------------------------------------------------------------
        //Botones de los pisos, los cuales muestran el plano de edificación y el plano con las aulas... del interior

        tgFloorS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,1);//plano
                    SmartCampusLayers.quePlanta(mMapView,2);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloor1.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,4);//plano
                    SmartCampusLayers.quePlanta(mMapView,5);//Interior Spaces

                    tgFloorS.setChecked(false);tgFloor1.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,7);//plano
                    SmartCampusLayers.quePlanta(mMapView,8);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,10);//plano
                    SmartCampusLayers.quePlanta(mMapView,11);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloorS.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,13);//plano
                    SmartCampusLayers.quePlanta(mMapView,14);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloor2.setChecked(false);
                    tgFloorS.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,16);//plano
                    SmartCampusLayers.quePlanta(mMapView,17);//Interior Spacess

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloorS.setChecked(false);tgFloor5.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,19);//plano
                    SmartCampusLayers.quePlanta(mMapView,20);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloorS.setChecked(false);
                    tgFloor6.setChecked(false);
                }
            }
        });
        tgFloor6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SmartCampusLayers.deleteFloors();//borramos las capas de los otros pisos
                    SmartCampusLayers.quePlanta(mMapView,22);//plano
                    SmartCampusLayers.quePlanta(mMapView,23);//Interior Spaces

                    tgFloor0.setChecked(false);tgFloorS.setChecked(false);tgFloor2.setChecked(false);
                    tgFloor3.setChecked(false);tgFloor4.setChecked(false);tgFloor5.setChecked(false);
                    tgFloorS.setChecked(false);
                }
            }
        });

        // Rutas
        createGraphicsOverlay();
        setupOAuthManager();

        //Mostrar características del mapa

        mCallout=mMapView.getCallout();
        // get the callout that shows attributes
        mCallout = mMapView.getCallout();
        // create the service feature table
        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.sample_service_url));
        // create the feature layer using the service feature table
        final FeatureLayer featureLayer = new FeatureLayer(mServiceFeatureTable);
        // add the layer to the map
        map.getOperationalLayers().add(featureLayer);



    }
    /**************************************************************************************************
     * *                                   MÉTODOS
     * *********************************************************************************************/
    //Párametros y características del mapa (por defecto)
    private void setupMap(double latitude, double longitude)
    {
        if (mMapView != null) {

//             If online basemap is desirable, uncomment the following lines
            //Basemap.Type basemapType = Basemap.Type.DARK_GRAY_CANVAS_VECTOR;
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
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

    //Mostramos el punto de la ubicación actual según la red que este conectada
    private void setupLocationDisplay()
    {
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

    private void createGraphicsOverlay()
    {
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }

    public void setMapMarker(Point location, SimpleMarkerSymbol.Style style, int markerColor, int outlineColor) {
        float markerSize = 10.0f;
        float markerOutlineThickness = 2.0f;
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(style, markerColor, markerSize);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, outlineColor, markerOutlineThickness));
        Graphic pointGraphic = new Graphic(location, pointSymbol);
        mGraphicsOverlay.getGraphics().add(pointGraphic);
    }

    private void setStartMarker(Point location) {
        //mGraphicsOverlay.getGraphics().clear();
        setMapMarker(location, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(226, 119, 40), Color.BLUE);
        mStart = location;
        //findRoute();
        //mEnd = null;
    }

    public  void setEndMarker(Point location, SimpleMarkerSymbol.Style diamond, int rgb, int red) {
        mGraphicsOverlay.getGraphics().clear();
        //setMapMarker(location, SimpleMarkerSymbol.Style.SQUARE, Color.rgb(40, 119, 226), Color.RED);
        setMapMarker(location,SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(40, 119, 226), Color.RED);
        mEnd = location;
        mMapView.setViewpointCenterAsync(location);//Centra el mapa en la ubicación destino
        //findRoute();
    }

    private void mapClicked(Point location) {
        location = new Point(location.getX(), location.getY(), getZFromLevel(defaultFloor));
        //setStartMarker(location);

        if (mStart == null) {
            // Start is not set, set it to a tapped location
            setStartMarker(location);
            //mostrarInfo();
        }else if (mStart!=null && mEnd!=null){
            mGraphicsOverlay.getGraphics().clear();
            setEndMarker(mEnd, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(40, 119, 226), Color.RED);
            setStartMarker(location);

        }
        else if (mStart!=null && mEnd == null)
        {
            mGraphicsOverlay.getGraphics().clear();
            setEndMarker(location, SimpleMarkerSymbol.Style.DIAMOND, Color.rgb(40, 119, 226), Color.RED);
            setStartMarker(location);

        }/*
        else
        {
            // Both locations are set; re-set the start to the tapped location
            setStartMarker(location);
            findRoute();
        }*/
    }

    public static float getZFromLevel(int level) {
        return level*4 + 4;
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
        final RouteTask solveRouteTask = new RouteTask(getApplicationContext(), "http://smartcampus.sg.uji.es:6080/arcgis/rest/services/Network/GermanNet/NAServer/Route");
        solveRouteTask.loadAsync();
        solveRouteTask.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (solveRouteTask.getLoadStatus() == LoadStatus.LOADED) {
                    final ListenableFuture<RouteParameters> routeParamsFuture = solveRouteTask.createDefaultParametersAsync();
                    routeParamsFuture.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RouteParameters routeParameters = routeParamsFuture.get();
                                List<Stop> stops = new ArrayList<>();
                                stops.add(new Stop(mStart));
                                stops.add(new Stop(mEnd));
                                routeParameters.setStops(stops);

                                final ListenableFuture<RouteResult> routeResultFuture = solveRouteTask.solveRouteAsync(routeParameters);
                                routeResultFuture.addDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            RouteResult routeResult = routeResultFuture.get();
                                            Route firstRoute = routeResult.getRoutes().get(0);

                                            Polyline routePolyline = firstRoute.getRouteGeometry();
                                            SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 4.0f);
                                            Graphic routeGraphic = new Graphic(routePolyline, routeSymbol);
                                            mGraphicsOverlay.getGraphics().add(routeGraphic);

                                            long lengthInKm = Math.round(firstRoute.getTotalLength() / 1000);
                                            long lengthInmeters = Math.round(firstRoute.getTotalLength());
                                            long timeInMinutes = Math.round(firstRoute.getTravelTime()*60);

                                            Toast.makeText(getApplicationContext(),
                                                    "Distancia Total: " + lengthInmeters+ " metros", Toast.LENGTH_LONG)
                                                    .show();

                                        } catch (InterruptedException | ExecutionException e) {
                                            showError("Solve RouteTask failed " + e.getMessage());
                                        }
                                    }
                                });

                            } catch (InterruptedException | ExecutionException e) {
                                showError("Cannot create RouteTask parameters " + e.getMessage());
                            }
                        }
                    });
                } else {
                    showError("Unable to load RouteTask " + solveRouteTask.getLoadStatus().toString());
                }
            }
        });
    }

}
