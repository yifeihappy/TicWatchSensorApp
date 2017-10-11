package dyf.com;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yifeihappy.ticwatchsensorapp.R;

@TargetApi(22)
public class MainActivity extends Activity {

    private Button buttonSensor = null;
    private Button buttonIP = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        buttonSensor = (Button)findViewById(R.id.buttonSensorActivity);
        buttonIP = (Button)findViewById(R.id.buttonIP);
        buttonSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sensorIntent = new Intent(MainActivity.this, SensorActivity.class);
                //sensorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sensorIntent);
            }
        });
        buttonIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ipIntent = new Intent(MainActivity.this, SetIPActivity.class);
                //ipIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ipIntent);
            }
        });
    }
}
