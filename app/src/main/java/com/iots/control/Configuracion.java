package com.iots.control;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Configuracion extends AppCompatActivity {
    Button volver8;
    Button salir8;
    EditText localhostip;
    Button btnguardar, btnmostrar;
    static String host = "192.168.0.10"; // Cambiar Valor Por tu IP Local de la Raspberry Pi
    static String MQTTHOST = "tcp://" + host + ":1883";
    static String USERNAME = "usuariowimod";
    static String PASSWORD = "usuariowimod12";
    String topicTemp = "temp";
    String topichum = "hum";
    String topicUltrasonico="ultra";
    String topicMovimiento="pir1";
    MqttAndroidClient clientlocal;
    TextView subtemperaturalocal;
    TextView subdistancia;
    TextView subseguridadlocal;
    TextView subcalidadlocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        final Context context = this;
        final SharedPreferences sharprefs = getSharedPreferences("ArchivoSP",
                context.MODE_PRIVATE);
        localhostip = (EditText) findViewById(R.id.localhostip);
        btnguardar = (Button) findViewById(R.id.btnguardar);
        btnmostrar = (Button) findViewById(R.id.btnmostrar);
        btnmostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
                String valor = sharpref.getString("MiIPlocal", "No hay dato");
                Toast.makeText(getApplicationContext(), "IP guardada : " + valor, Toast.LENGTH_LONG).show();
            }
        });
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharpref.edit();
                editor.putString("MiIPlocal", localhostip.getText().toString());
                editor.commit();
            }
        });
        subtemperaturalocal = (TextView) findViewById(R.id.subtemperalocal);
        subdistancia = (TextView) findViewById(R.id.distancia);
        subseguridadlocal = (TextView) findViewById(R.id.subseguridadlocal);
        subcalidadlocal = (TextView) findViewById(R.id.subcalidadlocal);
        volver8 = (Button) findViewById(R.id.btnvolver8);
        salir8 = (Button) findViewById(R.id.btnsalir8);
        volver8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        salir8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        String clientId = MqttClient.generateClientId();
        clientlocal = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
                clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = clientlocal.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(Configuracion.this, "Conexión Local Establecida!",
                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(Configuracion.this, "Conexión Local Fallida!",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        clientlocal.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if (topic.equals(topicTemp)) {
                    subtemperaturalocal.setText(new String(message.getPayload()) + " °C");
                }
                if (topic.equals(topicUltrasonico)) {
                    subdistancia.setText(new String(message.getPayload()) + " cm");
                }
                if (topic.equals(topicMovimiento)) {
                    subseguridadlocal.setText(new String(message.getPayload())+" Movimientos");
                }
                if (topic.equals(topichum)){
                    subcalidadlocal.setText(new String(message.getPayload())+" %");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setSubscription() {
        try {
            clientlocal.subscribe(topicMovimiento, 0);
            clientlocal.subscribe(topicTemp, 0);
            clientlocal.subscribe(topicUltrasonico, 0);
            clientlocal.subscribe(topichum, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
