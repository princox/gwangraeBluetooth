package sensor.hlab.gwangraebluetooth;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import sensor.hlab.gwangraebluetooth.BLE.BlunoLibrary;

import static sensor.hlab.gwangraebluetooth.BLE.BlunoLibrary.connectionStateEnum.isConnected;

public class MainActivity extends BlunoLibrary {
    private static final String TAG = MainActivity.class.getName();
    private TextView textView1;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView);

        blePermissionSetting();

        findViewById(R.id.button).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                buttonScanOnClickProcess();
            }
        });
//        buttonScanOnClickProcess();    연결화면 띄우는 것


    }
//
//    Button.OnClickListener mClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            buttonScanOnClickProcess();
//        }
//    };

    @Override
    public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {
        if (theconnectionStateEnum == isConnected) {
            Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        onResumeProcess();
    }



    @Override
    public void onSerialReceived(String theString) {
        if ( theString != null ) {
            textView1.setText(theString);
        }
//        serialSend("");
// TODO:serialSend라는 메소드가 BLE로 데이터 보내는 메소드임!! - 알람 설정 해놓을때 serialSend메소드 쓰면 됨.
    }
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
               onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
              onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }


    public void blePermissionSetting() {

        new TedPermission(getApplicationContext()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                Log.d(TAG, "블루투스가 연결되었습니다.");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {

                Log.d(TAG, "블루투스 연결에 실패하였습니다.");
            }
        })
                .setDeniedMessage("Bluetooth를 동작시키세요.")
                .setPermissions(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION    )
                .check();


        onCreateProcess();												//onCreate Process by BlunoLibrary
        serialBegin(115200);											//set the Uart Baudrate on BLE chip to 115200
    }
}
