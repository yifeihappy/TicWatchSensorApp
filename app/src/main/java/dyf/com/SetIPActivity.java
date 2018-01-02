package dyf.com;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yifeihappy.ticwatchsensorapp.R;

/**
 * Created by yifeihappy on 2017/10/10.
 */
public class SetIPActivity extends Activity {
    private Button ipBtn = null;
    private EditText ipEdt = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String IP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_layout);
        ipBtn = (Button)findViewById(R.id.buttonOK);
        ipEdt = (EditText)findViewById(R.id.editTextIP);
        preferences = getSharedPreferences("IP", Context.MODE_PRIVATE);
        editor = preferences.edit();
        IP = preferences.getString("IP","192.168.0.10");
        ipEdt.setText(IP);
        ipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IP = ipEdt.getText().toString();
                editor.putString("IP",IP);
                editor.commit();
                SockThread.setIP(IP);
                finish();
            }
        });
    }
}
