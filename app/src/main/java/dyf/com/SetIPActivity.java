package dyf.com;

import android.app.Activity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_layout);
        ipBtn = (Button)findViewById(R.id.buttonOK);
        ipEdt = (EditText)findViewById(R.id.editTextIP);

        ipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SockThread.IP = ipEdt.getText().toString();
                finish();
            }
        });
    }
}
