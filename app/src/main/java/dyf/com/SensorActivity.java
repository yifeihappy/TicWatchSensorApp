package dyf.com;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yifeihappy.ticwatchsensorapp.R;

/**
 * Created by yifeihappy on 2017/10/10.
 */

public class SensorActivity extends Activity  implements SensorEventListener{

    private TextView textViewTimeStamp = null;
    public SensorManager sensorManager = null;
    private Handler handlerUI = null;
    private SockThread sockThread = null;
    private StringBuffer strBufferMsg = null;
    String IP = null;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        preferences = getSharedPreferences("IP", Context.MODE_PRIVATE);
        IP = preferences.getString("IP", null);
        if(IP == null) {
            finish();
        }

        //保持屏幕不暗
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textViewTimeStamp = (TextView)findViewById(R.id.textViewTimestamp);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //更新本activity layout 的handler
        handlerUI = new UIHandler();
        sockThread = new SockThread(sensorManager,this, handlerUI, IP);
        new Thread(sockThread).start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        //long eventTime = sensorEvent.timestamp;
        long eventTime = System.currentTimeMillis();
        Message msgUI = Message.obtain();
        Message msgSocket = Message.obtain();
        msgSocket.obj = null;
        Bundle bundle = new Bundle();
        //更新UI timestamp
        bundle.putString("t", ""+eventTime);
        msgUI.setData(bundle);
        handlerUI.sendMessage(msgUI);

        if(sensorEvent.values.length != 0)
        {
            strBufferMsg = new StringBuffer();
            strBufferMsg.append(sensorType + "," + eventTime + "," + sensorEvent.values.length);
            for(int i=0; i<sensorEvent.values.length; i++)
            {
                strBufferMsg.append("," + sensorEvent.values[i]);
            }
            strBufferMsg.append("\r\n");
            msgSocket.what = sensorType;
            msgSocket.obj = strBufferMsg.toString();
        }

        if(sockThread.sendHandler != null && !sockThread.s.isClosed() && msgSocket.obj != null)
        {
            sockThread.sendHandler.sendMessage(msgSocket);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class UIHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case -101:
                    Toast.makeText(SensorActivity.this, "send data to server failed!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -100:
                    Toast.makeText(SensorActivity.this, "connect to server failed!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    Bundle bundle = msg.getData();
                    textViewTimeStamp.setText("T:"+bundle.getString("t"));

            }


        }
    }
}
