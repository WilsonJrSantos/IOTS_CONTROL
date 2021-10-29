package com.iots.control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Talanquera extends AppCompatActivity {
    private String string = "1";
    private String string1 = "1";
    Button volver1;
    Button salir1;
    ImageButton talan;
    static String MQTTHOST = "tcp://192.168.0.10:1883";
    static String USERNAME = "usuariowimod";
    static String PASSWORD = "usuariowimod12";
    String topicservo = "Servo";

    MqttAndroidClient client;
    TextView subtemp, humedad, estadohumedad, estadotemepratura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talanquera);
        volver1 = (Button) findViewById(R.id.btnvolver1);
        salir1 = (Button) findViewById(R.id.btnsalir1);
         talan= (ImageButton) findViewById(R.id.talanquera);
        volver1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        talan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d1 = talan.getBackground();
                String message = "Abierta";
                String message2 = "Cerrada";
                try {

                    if (d1.getConstantState() == getResources().getDrawable(R.drawable.talanoff).getConstantState()) {
                        client.publish(topicservo, message.getBytes(), 0, false);
                        talan.setBackground(ActivityCompat.getDrawable(Talanquera.this, R.drawable.talanon));
                        Toast.makeText(Talanquera.this, "Mensaje Publicado", Toast.LENGTH_SHORT).show();
                    } else if (d1.getConstantState() == getResources().getDrawable(R.drawable.talanon).getConstantState()) {
                        client.publish(topicservo, message2.getBytes(), 0, false);
                        talan.setBackground(ActivityCompat.getDrawable(Talanquera.this, R.drawable.talanoff));
                        Toast.makeText(Talanquera.this, "Mensaje Publicado", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        salir1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                    Toast.makeText(Talanquera.this, "Conexión Global Establecida!",
                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(Talanquera.this, "Conexión Global Fallida!",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setSubscription() {
        try {
            client.subscribe("", 0);
            client.subscribe("", 0);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

