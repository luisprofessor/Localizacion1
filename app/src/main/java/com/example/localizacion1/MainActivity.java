package com.example.localizacion1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class MainActivity extends AppCompatActivity {
    private TextView salida;
    private MainActivityViewModel mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerPermisos();
        inicializar();

    }

    public void inicializar(){
        salida=findViewById(R.id.tvSalida);
        mv= ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainActivityViewModel.class);
        mv.getTexto().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                salida.setText(s);
            }
        });
    }

    public void leer(View v){

       // mv.obtenerLectura();
        mv.hacerLectura();
    }

    public void obtenerPermisos(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }
    }
}