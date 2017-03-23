package youtu.serialtest;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import com.km1930.dynamicbicycleclient.serialndk.SerialManager;

public class MainActivity extends AppCompatActivity implements SerialManager.SerialSpeedChangeListener {

    private SerialManager mSerialManager;
    private String TAG = this.getClass().getSimpleName();
    private TextView tv_speed;
    private TextClock time1;
    private TextClock time2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mSerialManager = SerialManager.getInstance();
        mSerialManager.openSerial();
        mSerialManager.setSerialSpeedChangeListener(this);

        // y M d D h m s S f F t g G T z E a b y r
        // 设置12时制显示格式
        time1.setFormat12Hour("hh:MM:ss");
        // 设置24时制显示格式
        time2.setFormat24Hour(
                "\tyy\tyyyy"
               + "\tm\tmm\tM\tMM\tMMM\tMMMM"
               + "\td\tdd"
               + "\th\thh\tH\tHH"
               + "\ts\tss"
               + "\tz\tzz"
               + "\ta "
               + "\tE \tEEEE"

        );

        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String MAC = wm.getConnectionInfo().getMacAddress();

        System.out.println("LLLLLLLLLLLLLLLLLLLLLLL:" + MAC);

    }


    @Override
    public void onSerialSpeedChanged(final short speed) {
        Log.d(TAG, "串口速度：" + speed);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_speed.setText("" + speed);
            }
        });
        mSerialManager.writeSerial(speed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSerialManager != null) {
            mSerialManager.openSerial();
        }
    }

    private void initView() {
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        time1 = (TextClock) findViewById(R.id.time1);
        time2 = (TextClock) findViewById(R.id.time2);

    }
}
