package com.edgcam.usogeolocalizacion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    Button btnObtenerUbicacion, btnEnviarUbic, btnMostrarUbic;
    TextView tvLatitud, tvLongitud, tvDireccion;
    String latitud, longitud;
    public static final int CODIGO_UBICACION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacion);
        btnEnviarUbic = findViewById(R.id.btnEnviarUbic);
        btnMostrarUbic = findViewById(R.id.btnMostrarUbic);
        tvLatitud = findViewById(R.id.tvLatitud);
        tvLongitud = findViewById(R.id.tvLongitud);
        tvDireccion = findViewById(R.id.tvDireccion);
        
        btnObtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerUbicacion();
            }
        });

        btnEnviarUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarUbicWhatsapp();
            }
        });

        btnMostrarUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarUbicacion();
            }
        });
    }

    private void mostrarUbicacion() {
        Intent intent = new Intent(this, mapUbicacion.class);
        intent.putExtra("latitud", Double.parseDouble(latitud));
        intent.putExtra("longitud", Double.parseDouble(longitud));
        startActivity(intent);



    }

    private void enviarUbicWhatsapp() {
        String text = "“Hola, te adjunto mi ubicación: \n" +
                "https://maps.google.com/?q=”";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text + latitud + "," + longitud);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    private void obtenerUbicacion() {
        verificarPermisosUbicacion();
    }

    private void verificarPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
        } else {
            iniciarUbicacion();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_UBICACION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarUbicacion();
                return;
            }
        }

    }

    private void iniciarUbicacion() {
        LocationManager objGestorUbicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion ubicador = new Localizacion();
        ubicador.setMainActivity(this);
        final boolean gpsEnabled = objGestorUbicacion.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,}, CODIGO_UBICACION);
            return;
        }
        objGestorUbicacion.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, (LocationListener) ubicador);
        objGestorUbicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, (LocationListener) ubicador);
        Toast.makeText(MainActivity.this, "Localizacion Inicializada", Toast.LENGTH_SHORT).show();
        tvLatitud.setText("");
        tvLongitud.setText("");
        tvDireccion.setText("");
    }
    
    public class Localizacion implements LocationListener{
        MainActivity mainActivity;

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());
            tvLatitud.setText(latitud);
            tvLongitud.setText(longitud);
            this.mainActivity.obtenerDireccion(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("EstatusGPS", "GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("EstatusGPS", "GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debud", "LocationProvider.AVAILABLE");
                    break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                        break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                            break;
            }
        }
    }

    private void obtenerDireccion(Location ubicacion) {
        if (ubicacion.getLatitude() != 0.0 && ubicacion.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        ubicacion.getLatitude(), ubicacion.getLongitude(), 1);
                if(!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    tvDireccion.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}