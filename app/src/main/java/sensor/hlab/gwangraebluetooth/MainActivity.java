package sensor.hlab.gwangraebluetooth;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sensor.hlab.gwangraebluetooth.BLE.BlunoLibrary;

import static android.R.attr.data;
import static sensor.hlab.gwangraebluetooth.BLE.BlunoLibrary.connectionStateEnum.isConnected;

public class MainActivity extends BlunoLibrary {
    public static int i = 0;
    private static final String TAG = MainActivity.class.getName();
    private TextView textView1;
    private LineChart lineChart;
    public ArrayList<Entry> entries = new ArrayList<>();
    Button button2;
    public LineChart graph;
    public LineDataSet dataSet;
    public LineData lineData;
    public int keymode = 0;
    String name, height, weight, age = "";
    ArrayList<String> heartrate = new ArrayList<>();
    public String heartrate2 = "";

    private String filename = "gwangrae.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText editText1 = (EditText) findViewById(R.id.editText1);
        final EditText editText2 = (EditText) findViewById(R.id.editText2);
        final EditText editText3 = (EditText) findViewById(R.id.editText3);
        final EditText editText4 = (EditText) findViewById(R.id.editText4);


        textView1 = (TextView) findViewById(R.id.textView);
        graph = (LineChart) findViewById(R.id.chart);

        blePermissionSetting();




//        myExternalFile = new File(getExternalFilesDir(filepath), filename);
//
//        try {
//            FileOutputStream fos = new FileOutputStream(myExternalFile);
//            fos.write(msg.getBytes());
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }





        findViewById(R.id.button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonScanOnClickProcess();
            }
        });


        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keymode == 1) {
                    keymode = 0;
                    button2.setText("운동 시작");
                    if(writeToSD(heartrate2) ){
                        Log.e("파일 저장 성공", "file");
                    } else {
                        Log.e("파일 저장 실패", "file");
                    }

                } else {
                    keymode = 1;
                    button2.setText("운동 종료");
                    editText1.setInputType(0);
                    editText2.setInputType(0);
                    editText3.setInputType(0);
                    editText4.setInputType(0);

                    name = editText1.getText().toString();
                    age = editText2.getText().toString();
                    height = editText3.getText().toString();
                    weight = editText4.getText().toString();
                }
            }
        });
        entries.add(new Entry(0, 0f));

        dataSet = new LineDataSet(entries, "# of Calls");
        lineData = new LineData(dataSet);
        graph.setData(lineData);
        graph.invalidate();

    }
    // End of onCreate


    public void writeFile(String text){

        File myfile = Environment.getExternalStorageDirectory();
        File logFile = new File(myfile.getAbsolutePath()+"/", "log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    } // End of writefile



    public Boolean writeToSD(String text){
        Boolean write_successful = false;
        File root=null;
        try {
            // <span id="IL_AD8" class="IL_AD">check for</span> SDcard
            root = Environment.getExternalStorageDirectory();
            Log.i(TAG,"path.." +root.getAbsolutePath());

            //check sdcard permission
            if (root.canWrite()){
                File fileDir = new File(root.getAbsolutePath());
                fileDir.mkdirs();
                /////////
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH시 mm분");
                String strCurDate = CurDateFormat.format(date);
                String strCurTime = CurTimeFormat.format(date);
               //////////

                File file= new File(fileDir, name+"_"+age+"_"+height+"_"+weight+"_"+strCurDate+strCurTime+".csv");
                FileWriter filewriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(filewriter);
                out.write("name"+","+"Heart rate"+"\n");
                out.write(text);
                out.close();
                write_successful = true;
            }
        } catch (IOException e) {
            Log.e("ERROR:---", "Could not write file to SDCard" + e.getMessage());
            write_successful = false;
        }
        return write_successful;
    } // end of writeToSD

/////////////////////////
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
//////////////////////////
    @Override
    public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {
        if (theconnectionStateEnum == isConnected) {
            Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_SHORT).show();
            button2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        onResumeProcess();
    }


    @Override
    public void onSerialReceived(String theString) {
        if (theString != null) {
            textView1.setText(theString);

            if (keymode == 1) {
                i++;
                entries.add(new Entry(i, Integer.parseInt(theString)));
                dataSet = new LineDataSet(entries, "# of Calls(by gwangrae)");
                lineData = new LineData(dataSet);
                graph.setData(lineData);
                graph.invalidate();
                heartrate.add(theString);
                heartrate2 += name + "," + theString + "\n";
            }
        }

//        serialSend("");
// TODO:serialSend라는 메소드가 BLE로 데이터 보내는 메소드임!! - 알람 설정 해놓을때 serialSend메소드 쓰면 됨.
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
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
                .setPermissions(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


        onCreateProcess();                                                //onCreate Process by BlunoLibrary
    }
}
