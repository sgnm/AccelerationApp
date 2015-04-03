package jp.techinstitute.ti_28.accelerationtest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private SensorManager manager;
    private TextView values;
    private float a = 0.1f;
    private float[] currentOrientationValues = {0.0f, 0.0f, 0.0f};
    private float threshold = 0.0f;
    private float max = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        values = (TextView)findViewById(R.id.value_id);
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String text = editText.getText().toString();
                threshold = Float.parseFloat(text); //float thresholdとすると、最初に書いた変数と違う変数になるのでエラーになる
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        manager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0){
            Sensor s = sensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            /*String str = "加速度センサー：" + "\nX軸：" + event.values[SensorManager.DATA_X]
                    + "\nY軸：" + event.values[SensorManager.DATA_Y]
                    + "\nZ軸：" + event.values[SensorManager.DATA_Z];*/

            currentOrientationValues[0] = (float) (a * event.values[SensorManager.DATA_X] + (1.0 - a) * currentOrientationValues[0]);
            currentOrientationValues[1] = (float) (a * event.values[SensorManager.DATA_Y] + (1.0 - a) * currentOrientationValues[1]);
            currentOrientationValues[2] = (float) (a * event.values[SensorManager.DATA_Z] + (1.0 - a) * currentOrientationValues[2]);

            //S[t]= α*Y[t-1]+(1-α)*S[t-1]; current両方とも[0]や[1]だったりするのは、前回の値が、右にあって、
            //新しくいれる値を左に書いてあるから。プログラムにもかいてあるように、[0~2]なのは、x, y, zを取りたいから。

            String str = "加速度センサー：" + "\nX軸：" + currentOrientationValues[0]
                    + "\nY軸：" + currentOrientationValues[1]
                    + "\nZ軸：" + currentOrientationValues[2];

            values.setText(str);

            float sum = (float) Math.sqrt((currentOrientationValues[0] * currentOrientationValues[0]) +
                            (currentOrientationValues[1] * currentOrientationValues[1]) +
                            (currentOrientationValues[2] * currentOrientationValues [2])) - 9.8f;
//            Log.d("SQRT", String.valueOf(sum));

            if (sum > threshold && sum < max) {
//                Log.d("TAG", String.valueOf(sum));
                Log.d("TAG", String.valueOf(max));
//                Log.d("TAG", String.valueOf(threshold));
//                Log.d("XYZ", str);
            }

            if (sum >= max){
                max = sum; //現在の値が前回より大きかったときに、maxに現在の値をいれてる。つまり最大値がでる
            }else {
                max = 0.0f; //現在の値が最大値より小さいとき、最大値をリセットする必要があるので0を入れてる
            }

            //3軸の加速度から加速度の合計を出したけど、ログが2,3個出てしまうので、
            //あとは、加速度の最大値を取得して、それを超えなければログを出さないみたいな。
            //そんでその最大値をリセットするみたいな。
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
