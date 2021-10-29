package com.iots.control;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity implements MqttCallback, IMqttActionListener {

    MqttAndroidClient client;
    TextView subText;
    String dato;
    ImageButton l1, l2, l3, l4;
    String topico1 = "pir";
    String topico2 = "led1";
    String topico3 = "lamp3";
    String topico4 = "lampp4";
    String topic1 = "Pir";
    String topic2 = "Led1";
    String topic3 = "lamp3";
    String topic4 = "lamp4";
    static String MQTTHOST = "tcp://192.168.0.10:1883";
    static String USERNAME = "usuariowimod";
    static String PASSWORD = "usuariowimod12";
    private Myapplication myapplication;
    private String string = "1";
    private String string1 = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText = (TextView) findViewById(R.id.texview1);
        l1 = (ImageButton) findViewById(R.id.lamp1);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicar1();
            }
        });
        l2 = (ImageButton) findViewById(R.id.lamp2);
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicar2();
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
                    Toast.makeText(MainActivity.this, "Conexión Global Establecida!",
                            Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Conexión Global Fallida!",
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
                dato = new String(message.getPayload());
                String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
                String str1 = new String(message.getPayload());
                subText.setText(dato);
                String message1 = "Lampara 2 encendido";
                String message2 = "Lampara 2 apagado";
                String sx = subText.getText().toString();


                    if (sx.equals("movimiento")) {
                        subText.setText(message1);
                        Toast.makeText(MainActivity.this, "Lampara 1 encendido", Toast.LENGTH_SHORT).show();
                        l1.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.on));
                    } else if (sx.equals("vacio")) {
                        subText.setText(message2);
                        Toast.makeText(MainActivity.this, "Lampara 1 Apagado", Toast.LENGTH_SHORT).show();
                        l1.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.off));
                    }

                    else if (sx.equals("ON")) {
                        subText.setText(message1);
                        Toast.makeText(MainActivity.this, "Lampara 2 encendido", Toast.LENGTH_SHORT).show();
                        l2.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.on));
                    } else if (sx.equals("OFF")) {
                        subText.setText(message2);
                        Toast.makeText(MainActivity.this, "Lampara 2 Apagado", Toast.LENGTH_SHORT).show();
                        l2.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.off));
                    }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

    }

    private void publicar2() {
        Drawable d2 = l2.getBackground();

        String message1 = "ON";
        String message2 = "OFF";
        try {
            Toast.makeText(this, "Mensaje Publicado", Toast.LENGTH_SHORT).show();
            if (d2.getConstantState() == getResources().getDrawable(R.drawable.off).getConstantState()) {
                client.publish(topic2, message1.getBytes(), 0, false);
                l2.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.on));
            } else if (d2.getConstantState() == getResources().getDrawable(R.drawable.on).getConstantState()) {
                client.publish(topic2, message2.getBytes(), 0, false);
                l2.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.off));
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void publicar1() {
        Drawable d1 = l1.getBackground();
        String message = "movimiento";
        String message2 = "vacio";
        try {
            Toast.makeText(this, "Published Message", Toast.LENGTH_SHORT).show();
            if (d1.getConstantState() == getResources().getDrawable(R.drawable.off).getConstantState()) {
                client.publish(topic1, message.getBytes(), 0, false);
                l1.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.on));
            } else if (d1.getConstantState() == getResources().getDrawable(R.drawable.on).getConstantState()) {
                client.publish(topic1, message2.getBytes(), 0, false);
                l1.setBackground(ActivityCompat.getDrawable(MainActivity.this, R.drawable.off));
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription() {

        try {

            client.subscribe(topico1, 0);
            client.subscribe(topico2, 1);
            client.subscribe(topico3, 2);
            client.subscribe(topico4, 3);
            client.subscribe(topic1, 0);
            client.subscribe(topic2, 1);
            client.subscribe(topic3, 2);
            client.subscribe(topic4, 3);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess(IMqttToken asyncActionToken) {

    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
