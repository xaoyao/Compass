package com.liu.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private ImageView ivCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivCompass= (ImageView) findViewById(R.id.iv_compass);
        //获传感器管理器
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        //地磁传感器
        Sensor magneticSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //加速度传感器
        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //注册侦听
        sensorManager.registerListener(listener,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sensorManager!=null){
            //销毁时解除注册
            sensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener=new SensorEventListener() {
        float[] magneticValues=new float[3];
        float[] accelerometerValues=new float[3];

        private float lastRotateDegree;

        @Override
        public void onSensorChanged(SensorEvent event) {
            //判断当前是什么传感器
            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                //获取地磁传感器数据
                magneticValues=event.values.clone();
            }else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                //获取加速度传感器数据
                accelerometerValues=event.values.clone();
            }

            float[] R=new float[9];
            float[] values=new float[3];
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
            //计算旋转数据
            SensorManager.getOrientation(R,values);
            //计算背景的旋转角度
            float rotateDegree= - (float) Math.toDegrees(values[0]);
            //每次旋转大于1
            if(Math.abs(rotateDegree-lastRotateDegree)>1){
                //旋转补间动画
                RotateAnimation rotateAnimation=new RotateAnimation(lastRotateDegree,rotateDegree,
                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setFillAfter(true);
                //旋转背景
                ivCompass.startAnimation(rotateAnimation);
                lastRotateDegree=rotateDegree;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
