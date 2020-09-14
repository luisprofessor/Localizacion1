package com.example.localizacion1;

import android.Manifest;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static androidx.core.app.ActivityCompat.requestPermissions;


public class MainActivityViewModel extends AndroidViewModel implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private MutableLiveData<String> texto;
    private Context context;
    private GoogleApiClient googleApiClient;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<String> getTexto() {
        if (texto == null) {
            texto = new MutableLiveData<>();
        }
        return texto;
    }

    public void obtenerLectura() {
        LocationManager lm = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        LocationProvider locationProvider = lm.getProvider(LocationManager.GPS_PROVIDER);
        texto.setValue(locationProvider.getName() + " " + locationProvider.getAccuracy());

    }

    public void hacerLectura() {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        IntentFilter intentFilter=new IntentFilter("com.example.localizacion1.HACER");
        context.registerReceiver(new RepectorProximidad(),intentFilter);

        float lat = -33.123f;
        float longi = -66.123f;
        LocationManager lm = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        Intent intent = new Intent("com.example.localizacion1.HACER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, intent, 0);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.addProximityAlert(lat, longi, 500, -1, pendingIntent);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    texto.postValue("Latitud "+location.getLatitude()+" Longitud: "+location.getLongitude());

                }else {
                    texto.postValue("No hay lectura");
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

        texto.setValue("Se ha suspendido la conexion");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        texto.setValue("La conexion ha fallado");
    }
}
