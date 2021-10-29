package com.iots.control;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;

import android.os.Bundle;

public class Temperatura extends AppCompatActivity {
    private String string = "1";
    private String string1 = "1";
    Button volver1;
    Button salir1;
    ImageButton venti;
    static String MQTTHOST = "tcp://192.168.0.10:1883";
    static String USERNAME = "usuariowimod";
    static String PASSWORD = "usuariowimod12";
    String topicStrtempsub = "temp";
    String topicHum = "hum";
    String topicventi="Alerta1";

    MqttAndroidClient client;
    TextView subtemp, humedad, estadohumedad, estadotemepratura;
    private Myapplication myapplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatura);
        subtemp = (TextView) findViewById(R.id.subtemp);
        volver1 = (Button) findViewById(R.id.btnvolver1);
        salir1 = (Button) findViewById(R.id.btnsalir1);
        humedad = (TextView) findViewById(R.id.humedad);
        venti=(ImageButton) findViewById(R.id.ventilador);

        humedad.setText("0.00" + "%");
        subtemp.setText("0.00" + "C");
        estadohumedad = (TextView) findViewById(R.id.estadohumedad);
        estadotemepratura = (TextView) findViewById(R.id.estadotemperatura1);
        volver1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        venti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Drawable d1 = venti.getBackground();
                    String message = "calor";
                    String message2 = "frio";
                    try {
                        Toast.makeText(Temperatura.this, "Mensaje Publicado", Toast.LENGTH_SHORT).show();
                        if (d1.getConstantState() == getResources().getDrawable(R.drawable.aireoff).getConstantState()) {
                            client.publish(topicventi, message.getBytes(), 0, false);
                            venti.setBackground(ActivityCompat.getDrawable(Temperatura.this, R.drawable.aireon));
                        } else if (d1.getConstantState() == getResources().getDrawable(R.drawable.aireon).getConstantState()) {
                            client.publish(topicventi, message2.getBytes(), 0, false);
                            venti.setBackground(ActivityCompat.getDrawable(Temperatura.this, R.drawable.aireoff));
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
                    Toast.makeText(Temperatura.this, "Conexión Global Establecida!",
                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(Temperatura.this, "Conexión Global Fallida!",
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
                String dato = new String(message.getPayload());
                Double imprimir = Double.parseDouble(dato);

                if (topic.equals(topicStrtempsub)) {
                    subtemp.setText(dato + " °C");
                    if (imprimir >= 24 && imprimir <= 34) {
                        estadotemepratura.setText("Temperatura Ambiente");
                    } else if (imprimir > 34) {
                        estadotemepratura.setText("Aire Acondicionado activado");
                    } else if (imprimir < 24) {
                        estadotemepratura.setText("Calefaccion Activado");
                    }
                } else if (topic.equals(topicHum)) {
                    // humedad.setText(new Double(String.valueOf(message.getPayload()))+"");
                    humedad.setText(dato + " %");


                    if (imprimir > 75) {
                        estadohumedad.setText("Alta Humedad");
                    } else if (imprimir < 15) {
                        estadohumedad.setText("Baja Humedad");
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void setSubscription() {
        try {
            client.subscribe(topicStrtempsub, 0);
            client.subscribe(topicHum, 0);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
