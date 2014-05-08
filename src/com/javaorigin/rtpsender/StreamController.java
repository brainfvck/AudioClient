package com.javaorigin.rtpsender;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.Log;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.*;

import java.util.*;

/**
 * Created by Administrator on 2014/4/9.
 */
public class StreamController {
    private DatagramSocket clientSocket;
    private DatagramPacket udpPacket;
    private byte[] buffer = new byte[200];

    private AudioManager manager;
    private AudioGroup group;
    private AudioStream audioStream;

    private InetAddress SERVER;
    private int AUDIOPORT;
    private int DATAPORT;


    public StreamController(Context context) {
        try {
            SERVER = InetAddress.getByName("112.124.49.141");
            AUDIOPORT = 22222;
            DATAPORT = 11111;
        }
        catch(Exception e)
        {
            Log.e("SERVER address",e.toString());
        }


        //initialize the manager , stream , group
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        manager.setSpeakerphoneOn(true);
        manager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        try {
            audioStream = new AudioStream(getLocalIpAddress());
            audioStream.associate(SERVER, AUDIOPORT);
        }
        catch(Exception e)
        {
            Log.e("audio stream",e.toString());
        }
        audioStream.setMode(AudioStream.MODE_RECEIVE_ONLY);
        audioStream.setCodec(AudioCodec.PCMU);

        Log.i("local address",audioStream.getLocalAddress().getHostAddress());

        group = new AudioGroup();
        group.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);

        audioStream.join(group);

        //initialize the client socket
        try {
            clientSocket = new DatagramSocket(10117);
            udpPacket = new DatagramPacket(buffer,buffer.length);
        }
        catch(Exception e)
        {
            Log.e("client initialization",e.toString());
        }
    }

    public void speak() {
        try {
            //ask the permission to speak
            clientSocket.send(new DatagramPacket("Speak".getBytes(), "Speak".getBytes().length,SERVER,DATAPORT));

            clientSocket.receive(udpPacket);

            String receive = new String(udpPacket.getData(),"UTF-8");
            Log.i("upd received",receive);
            if (receive.startsWith("OK"))
            {
                Log.i("state","speak");
                audioStream.join(null);
                audioStream.setMode(AudioStream.MODE_SEND_ONLY);
                audioStream.join(group);
            }
            else
            {
                Log.i("state","busy");
            }
        }
        catch(Exception e)
        {
            Log.e("SPEAK",e.toString());
        }
    }
    public void stop() {
        audioStream.join(null);
        audioStream.setMode(AudioStream.MODE_RECEIVE_ONLY);
        audioStream.join(group);
        //manager.setSpeakerphoneOn(true);
        try {
            clientSocket.send(new DatagramPacket("Stop".getBytes(), "Stop".getBytes().length, SERVER, DATAPORT));
        }
        catch(Exception e )
        {
            Log.e("STOP ", e.toString());
        }
    }

    public void listen()
    {
        audioStream.join(null);
        audioStream.setMode(AudioStream.MODE_RECEIVE_ONLY);
        audioStream.join(group);
    }

    public int getReceiveStreamPort()
    {
        return audioStream.getLocalPort();
    }

    public InetAddress getLocalIpAddress() {

        try {
            ArrayList<NetworkInterface> infList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface inf : infList) {
                ArrayList<InetAddress> addressList = Collections.list(inf.getInetAddresses());
                for (InetAddress address : addressList) {
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(address.getHostAddress())) {
                        return InetAddress.getByAddress(address.getAddress());
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e("get ip",e.toString());
        }
        return null;
    }

}

