package com.example.servicesimpletest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_start, bt_stop, bt_bind, bt_unbind, bt_call_variable, bt_call_services;
    private TextView tv_text, tv_services;

    private MyService mService;
    private boolean isBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //버튼에 대한 참조
        bt_start = (Button)findViewById(R.id.bt_srart);
        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_bind = (Button)findViewById(R.id.bt_bind);
        bt_unbind = (Button)findViewById(R.id.bt_unbind);
        bt_call_variable = (Button)findViewById(R.id.bt_call_variable);
        tv_text = (TextView)findViewById(R.id.tv_text);

        bt_call_services = findViewById(R.id.bt_call_services);
        tv_services = findViewById(R.id.tv_services);

        //각 버튼에 대한 리스너 연결 - OnClickListener를 확장했으므로 onClick 오버라이딩 후 this사용
        bt_start.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_bind.setOnClickListener(this);
        bt_unbind.setOnClickListener(this);
        bt_call_variable.setOnClickListener(this);
        bt_call_services.setOnClickListener(this);
    }

    ServiceConnection sconn = new ServiceConnection() {
        @Override //서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            mService = myBinder.getService();

            isBind = true;
            Log.e("LOG", "onServiceConnected()");
        }

        @Override //서비스가 종료될 때 호출
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBind = false;
            Log.e("LOG", "onServiceDisconnected()");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.bt_srart :
                startService(new Intent(MainActivity.this, MyService.class)); // 서비스 시작
                break;

            case R.id.bt_stop :
                stopService(new Intent(MainActivity.this, MyService.class)); // 서비스 종료
                break;

            case R.id.bt_bind :
                if (!isBind) //해당 액티비이에서 바운딩 중이 아닐때만 호출 - 바운딩 시작
                    bindService(new Intent(MainActivity.this, MyService.class), sconn, BIND_AUTO_CREATE);
                break;

            case R.id.bt_unbind :
                if (isBind){ //해당 액티비티에서 바운딩중일때만 호출 - 바운딩 종료
                    unbindService(sconn);
                }
                break;

            case R.id.bt_call_variable :
                if (mService == null)
                    Toast.makeText(this, "mService가 null이므로 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                else if (mService != null) {
                        tv_text.setText("불러온 값 : "+mService.var);
                        Toast.makeText(this, ""+mService.toString(), Toast.LENGTH_SHORT).show();
                    }
                break;
            case R.id.bt_call_services:
                serviceList();
                break;
        }
    }

    public void serviceList(){
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(1000);
        int i;

        for(i = 0; i < rs.size(); i++){
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            Log.d("run service", "Package Name:" + rsi.service.getPackageName());
            Log.d("run service", "Class Name:" + rsi.service.getClassName());
        }
    }
}
