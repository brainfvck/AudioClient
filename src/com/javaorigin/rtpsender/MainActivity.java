package com.javaorigin.rtpsender;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    Button speak;
    Button listen;
    Button stop;
    Button add;
    StreamController controller;
    TextView displayInfo;
    EditText remoteIP;
    EditText remotePort;
    int number = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        speak = (Button) findViewById(R.id.button);
        listen = (Button) findViewById(R.id.button3);
        stop = (Button) findViewById(R.id.button2);
        add = (Button) findViewById(R.id.button4);
        displayInfo = (TextView) findViewById(R.id.textView);
        remoteIP = (EditText) findViewById(R.id.editText);
        remotePort = (EditText) findViewById(R.id.editText2);


        controller = new StreamController(this);
        InetAddress localAddress = controller.getLocalIpAddress();
        displayInfo.setText(localAddress.getHostAddress()+":"+Integer.toString(controller.getReceiveStreamPort()));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                }
                catch(Exception e)
                {

                }
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.speak();
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                }
                catch(Exception e){
                    controller.listen();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.stop();
            }
        });
    }


}
