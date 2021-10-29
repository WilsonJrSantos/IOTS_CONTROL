package com.iots.control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class Menu extends AppCompatActivity {
    ImageView temp;
    ImageView luz;
    ImageView segur;
    ImageView calid;
    ImageView configuracion;
    ImageView ayuda;
    static String MQTTHOST = "tcp://192.168.0.10:1883";
  static String USERNAME = "admin";
    static String PASSWORD = "public";
    String topicStrtodosub = "temp";
    MqttAndroidClient client;
    TextView subtodo;
    Button info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        temp = (ImageView) findViewById(R.id.btntemphum);
        luz = (ImageView) findViewById(R.id.btnluz);
        segur = (ImageView) findViewById(R.id.btnseguridad);

        configuracion = (ImageView) findViewById(R.id.btnconfig);
        ayuda = (ImageView) findViewById(R.id.btnayuda);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu2 = new Intent(Menu.this, Temperatura.class);
                startActivity(menu2);
            }
        });
        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu3 = new Intent(Menu.this, MainActivity.class);
                startActivity(menu3);
            }
        });
        segur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu4 = new Intent(Menu.this, Talanquera.class);
                startActivity(menu4);
            }
        });

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu8 = new Intent(Menu.this, Configuracion.class);
                startActivity(menu8);
            }
        });
        ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menu9 = new Intent(Menu.this, Ayuda.class);
                startActivity(menu9);
            }
        });
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(Menu.this, "Conexión Global Establecida!",
                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(Menu.this, "Conexión Global Fallida!",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws
                    Exception {
                subtodo.setText(new String(message.getPayload()) + " " + topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setSubscription() {
        try {
            client.subscribe(topicStrtodosub, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub(View v) {
        String topic = "Pir";
        String message = "apagar";
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}