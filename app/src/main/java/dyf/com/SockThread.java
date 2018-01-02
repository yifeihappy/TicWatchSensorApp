package dyf.com;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import dyf.com.SensorActivity;

/**
 * Created by yifeihappy on 2017/10/10.
 */

public class SockThread implements Runnable {
    public static String IP = null;
    public static int PORT = 30000;
    private SensorActivity sensorActivity = null;
    private SensorManager sensorManager = null;
    private Handler handlerUI = null;
    public Socket s = null;
    public OutputStream os = null;
    public BufferedReader br = null;
    private String sensorTypeStr = null;//The sensor types of this  device.
    private int SAMPLINGPERIODUS = 3;//default samping period 3
    public Handler sendHandler = null;//send sensor data by socket
    public static boolean sendFailB = false;
    public static boolean connectFailB = false;
    public static boolean monitorServerFailB = false;

    SockThread(SensorManager sensorManager, SensorActivity sensorActivity, Handler handlerUI, String ip)
    {
        this.sensorActivity = sensorActivity;
        this.handlerUI = handlerUI;
        this.sensorManager = sensorManager;
        this.IP = ip;

        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuffer strBufferSensor = new StringBuffer();
        strBufferSensor.append("SENSORSTYPE");
        for(Sensor sensor : listSensor)
        {
            strBufferSensor.append("," + sensor.getType() + ":" + sensor.getName());
        }
        strBufferSensor.append("\n");
        sensorTypeStr = strBufferSensor.toString();
    }
    public static void setIP(String ip) {
        IP = ip;
    }

    @Override
    public void run() {

        try
        {
            s = new Socket();
            s.connect(new InetSocketAddress(IP, PORT), 6000);
            os = s.getOutputStream();
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            try
            {
                os.write(sensorTypeStr.getBytes("utf-8"));
            }
            catch (IOException e)
            {
                if(!SockThread.sendFailB) {
                    Message msgUI = Message.obtain();
                    msgUI.what = -101;
                    handlerUI.sendMessage(msgUI);
                    SockThread.sendFailB = true;
                }
            }
            //监听来自server的消息
            new Thread()
            {
                @Override
                public void run() {
                    super.run();

                    String content = null;
                    try
                    {
                        while ((content = br.readLine()) != null) {
                            String[] contentArr = content.split(",");
                            if (contentArr[0].equals("SAMPLINGPERIODUS")) {
                                //unregister sensorEvenlisten
                                sensorManager.unregisterListener(sensorActivity);
                                SAMPLINGPERIODUS = Integer.parseInt(contentArr[1]);
                            } else if (contentArr[0].equals("SENSORSTYPE")) {
                                for (int i = 1; i < contentArr.length; i++) {
                                    //register sensor
                                    sensorManager.registerListener(sensorActivity, sensorManager.getDefaultSensor(Integer.parseInt(contentArr[i])), SAMPLINGPERIODUS);
                                }
                            }
                        }
                    }catch (IOException e)
                    {
                        if(!SockThread.monitorServerFailB) {
                            Message msgUI = Message.obtain();
                            msgUI.what = -102;
                            handlerUI.sendMessage(msgUI);
                            SockThread.monitorServerFailB = true;
                        }
                    }
                }
            }.start();

            Looper.prepare();
            sendHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    try
                    {
                        os.write(msg.obj.toString().getBytes("utf-8"));
                    }
                    catch (IOException e)
                    {
                        if(!SockThread.sendFailB) {
                            Message msgUI = Message.obtain();
                            msgUI.what = -101;
                            handlerUI.sendMessage(msgUI);
                            SockThread.sendFailB = true;
                        }

                    }
                }
            };
            Looper.loop();

        }
        catch (IOException e)
        {
            if(!SockThread.connectFailB) {
                Message msgUI = Message.obtain();
                msgUI.what = -100;
                handlerUI.sendMessage(msgUI);
                SockThread.connectFailB = true;
            }
        }
    }

}

