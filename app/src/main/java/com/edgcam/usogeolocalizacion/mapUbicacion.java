package com.edgcam.usogeolocalizacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class mapUbicacion extends AppCompatActivity {

    private double latitud;
    private double longitud;
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_ubicacion);

        Intent intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0);
        longitud = intent.getDoubleExtra("longitud", 0);

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = "https://www.openstreetmap.org/export/embed.html?bbox=" +
                (longitud-0.01) + "," + (latitud-0.01) + "," + (longitud+0.01) + "," + (latitud+0.01) + "&amp;layer=mapnik&amp;marker=" + latitud + "," + longitud;
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}