package lincyu.fcuproject2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class RunPage extends AppCompatActivity {
    InetAddress client_ip =null;
    //訊息
    String server_msg;
    //藍芽
    Boolean Bloothconnect = false;
    BluetoothAdapter bluetoothAdapter;//建立藍芽設備
    ArrayList<BluetoothDevice> pairedDeviceArrayList;//new bluetooth arraylist存放BLE的設備資料
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;//new 藍芽設備容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_page);
    }




}
