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
    private Ubicacion ubicacion;
    private TextView textView;
    private ToggleButton tbBuildings, tbFloors;
    private Controller controller;
    private EditText etSearch;
    private Button btSearch;
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
        btSearch= findViewById(R.id.btSearch);
        tgFloorS=findViewById(R.id.tgFloorS);tgFloor0=findViewById(R.id.tgFloor0);tgFloor1=findViewById(R.id.tgFloor1);tgFloor2=findViewById(R.id.tgFloor2);
        tgFloor3=findViewById(R.id.tgFloor3);tgFloor4=findViewById(R.id.tgFloor4);tgFloor5=findViewById(R.id.tgFloor5);tgFloor6=findViewById(R.id.tgFloor6);
        textView= findViewById(R.id.tvTexto);
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
                    //SmartCampusLayers.buildings(mMapView);
                    findRoute();
                }
                else
                {
                    tbBuildings.setBackgroundDrawable(getDrawable(R.drawable.buildings_off));
                    //SmartCampusLayers.deleteBuildings();
                    findRoute();
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
        //----------------------------------------------------------------------------------------
        //                                     BÚSCADOR
        //----------------------------------------------------------------------------------------
        //edit text, de esta forma se espera a que la consulta en cola se realice
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
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Marca el punto inicial de la ruta, según la ubicación.
//                setStartMarker(mLocationDisplay.getMapLocation());


                try {
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
    private void setupMap(double latitude, double longitude) {
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

private void mostrarInfo() {
    // set an on touch listener to listen for click events
    mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // remove any existing callouts
            if (mCallout.isShowing()) {
                mCallout.dismiss();
            }
            // get the point that was clicked and convert it to a point in map coordinates
            final Point clickPoint = mMapView
                    .screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
            // create a selection tolerance
            int tolerance = 10;
            double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
            // use tolerance to create an envelope to query
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                    clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, map.getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            // request all available attribute fields
            final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable
                    .queryFeaturesAsync(query, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
            // add done loading listener to fire when the selection returns
            future.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        //call get on the future to get the result
                        FeatureQueryResult result = future.get();
                        // create an Iterator
                        Iterator<Feature> iterator = result.iterator();
                        // create a TextView to display field values
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine(false);
                        calloutContent.setVerticalScrollBarEnabled(true);
                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        calloutContent.setMovementMethod(new ScrollingMovementMethod());
                        calloutContent.setLines(5);
                        // cycle through selections
                        int counter = 0;
                        Feature feature;
                        while (iterator.hasNext()) {
                            feature = iterator.next();
                            // create a Map of all available attributes as name value pairs
                            Map<String, Object> attr = feature.getAttributes();
                            Set<String> keys = attr.keySet();
                            for (String key : keys) {
                                Object value = attr.get(key);
                                // format observed field value as date
                                if (value instanceof GregorianCalendar) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                    value = simpleDateFormat.format(((GregorianCalendar) value).getTime());
                                }
                                // append name value pairs to TextView
                                calloutContent.append(key + " | " + value + "\n");
                            }
                            counter++;
                            // center the mapview on selected feature
                            Envelope envelope = feature.getGeometry().getExtent();
                            mMapView.setViewpointGeometryAsync(envelope, 200);
                            // show CallOut
                            mCallout.setLocation(clickPoint);
                            mCallout.setContent(calloutContent);
                            mCallout.show();
                        }
                    } catch (Exception e) {
                        Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                    }
                }
            });
            return super.onSingleTapConfirmed(e);
        }
    });

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


   /* private void findRoute() {
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
    }*/

}
