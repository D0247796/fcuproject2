package lincyu.fcuproject2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;

public class StartPage extends AppCompatActivity {
    int change=0 ,test=0;
    TextView tv_state,tv_ip;

    InetAddress client_ip;
    String  server_msg="";
    ListView lv_bluetoothdevice;
    //藍芽
    Boolean Bloothconnect = false;
    BluetoothAdapter bluetoothAdapter;//建立藍芽設備
    ArrayList<BluetoothDevice> pairedDeviceArrayList;//new bluetooth arraylist存放BLE的設備資料
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;//new 藍芽設備容器
    Thread TCP_SocketServerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        tv_state=(TextView)findViewById(R.id.tv_state);
        tv_ip=(TextView)findViewById(R.id.tv_IP);

        lv_bluetoothdevice=(ListView)findViewById(R.id.lv_bluetoothdevice);



        tv_ip.setText(getIpAddress());//拿取自己IP
        TCP_SocketServerThread = new Thread(new TCP_SocketServerThread());
        TCP_SocketServerThread.start();
        //藍芽
        SetupPairedDevices();


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

        switch (id){
            case R.id.action_settings:
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle("操作說明");
                ad.setMessage("方向鍵控制\n開關決定是否傳影音過去\n當發生警告事項時，警告燈會變\nMenu中可查詢車子身在的溫濕度");
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface di, int i) {
                            }
                        };
                ad.setPositiveButton("確定", listener);
                ad.show();
                break;



            case R.id.action_notes:
                String ip="";
                try {
                    String clientip = client_ip.toString();
                   ip = clientip.substring(1,clientip.length());
                }catch (Exception e){};

                    Intent intent = new Intent();
                    intent.putExtra("IP", ip);
                    intent.setClass(StartPage.this, Note.class);
                    startActivity(intent);

                break;


        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    //Server端
    private class TCP_SocketServerThread extends Thread {

        String cleint_msg;
        @Override
        public void run() {
            ServerSocket TCP_server_serversocket =null;
            Socket TCP_server_socket =null;
            DataInputStream TCP_server_din = null;
            DataOutputStream TCP_server_dout =null;

            try{
                TCP_server_serversocket= new ServerSocket(8888);
            }
            catch(Exception e){
                e.printStackTrace();;
            }
            while(true){
                try{
                    TCP_server_socket=TCP_server_serversocket.accept();
                    TCP_server_din = new DataInputStream(TCP_server_socket.getInputStream());
                    TCP_server_dout = new DataOutputStream(TCP_server_socket.getOutputStream());

                    cleint_msg = TCP_server_din.readUTF(); //這是裡傳來的訊息
                    TCP_server_dout.writeUTF("Car_Server");
                    //切換畫面
//                    if(cleint_msg.equals("User_Client") && change==0){
//                        change=1;
//                        client_ip = TCP_server_socket.getInetAddress();
//                        Intent intent = new Intent();
//                        intent.setClass(StartPage.this,RunPage.class);
//                        startActivity(intent);
//
//                    }
                    if(myThreadConnected!=null) {
                        byte[] bluetoothmsg = cleint_msg.toString().getBytes();
                        myThreadConnected.write(bluetoothmsg);//執行緒output訊息
                    }

                    //UI更新
                    StartPage.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            tv_state.setText(cleint_msg);
                        }
                    });



                }
                catch(Exception e){
                    e.getStackTrace();
                }
                finally{
                    try{
                        if(TCP_server_dout !=null){
                            TCP_server_dout.close();
                        }
                        if(TCP_server_din !=null){
                            TCP_server_din.close();
                        }
                        if(TCP_server_socket != null){
                            TCP_server_socket.close();
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }


    }
    //Client端
    private class TCP_SocketClientThread extends Thread {

        int msg;
        public TCP_SocketClientThread(int msg){
            this.msg=msg;

        }
        @Override
        public void run() {
            Socket TCP_client_socket = null;
//            String ip=et_account.getText().toString();
            DataOutputStream dout = null;
            DataInputStream din =null;
            try{
//                s= new Socket("192.168.1.106",8880);
                InetAddress ip=client_ip;
                TCP_client_socket = new Socket(ip,8880);
                dout = new DataOutputStream(TCP_client_socket.getOutputStream());
                din = new DataInputStream(TCP_client_socket.getInputStream());
                dout.writeInt(msg);
                server_msg = din.readUTF(); //這是裡傳來的訊息
                //UI更新
                StartPage.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_state.setText(server_msg);
                    }
                });
                TCP_client_socket.close();


            }
            catch (Exception e){
                StartPage.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_state.setText("發送訊息失敗");
                    }
                });
            }
            finally {
                try{
                    if(dout !=null){
                        dout.close();
                    }
                    if(din !=null){
                        din.close();
                    }
                    if(TCP_client_socket != null){
                        TCP_client_socket.close();
                    }
                }catch (Exception e){
                    e.getStackTrace();
                }
            }


        }


    }
    //拿取 IP
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    //藍芽
    public void SetupPairedDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//取得藍牙的初始Adapter
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices(); //取得配對過的資料
        if (pairedDevices.size() > 0) { //如果配對的資料大於0
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>(); //new bluetooth arraylist來存放BLE的設備資料
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device); //在pairedDeviceArrayList增加資料
            }
            //建立可以對應listview的容器，並且將pairedDeviceArrayList的資料導入
//            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, pairedDeviceArrayList);
//            AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
//            dialog_list.setTitle("附近藍芽裝置");
//            dialog_list.setItems(pairedDeviceAdapter, new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                 //取得選取position項目的藍牙設備資料
//
//                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device); //設定藍牙連線的執行緒
//                    myThreadConnectBTdevice.start(); //啟動此執行緒
//                }
//            });
//            dialog_list.show();
            lv_bluetoothdevice.setAdapter(pairedDeviceAdapter);//設定導入listview顯示畫面
            lv_bluetoothdevice.setOnItemClickListener(new AdapterView.OnItemClickListener() { //設定當觸發listview時
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);//取得選取position項目的藍牙設備資料

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device); //設定藍牙連線的執行緒
                    myThreadConnectBTdevice.start(); //啟動此執行緒
                }
            });
        }
    }
    ThreadConnectBTdevice myThreadConnectBTdevice;

    public class ThreadConnectBTdevice extends Thread {

        public BluetoothSocket bluetoothSocket = null; //建立bluetooth通訊
        public final BluetoothDevice bluetoothDevice;  //建立藍牙設備

        public ThreadConnectBTdevice(BluetoothDevice device) { //初始化建構子
            bluetoothDevice = device; //將傳進來的藍牙設置放入此類別的設置
            try {
                //建構一個溝通的socket,利用UUID
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));//利用UUID建立通訊鏈接


            } catch (IOException e) { //如果有出錯,例外處理
                e.printStackTrace();
            }
        }
        @Override
        public void run() { //當ThreadConnectBTdevice被觸發與執行後
            boolean success = false;//假設藍牙連線失敗
            try {
                bluetoothSocket.connect();//藍牙連線
                success = true;//狀態改成功
            } catch (IOException e) {//有狀況發現
                e.printStackTrace();
                try {
                    bluetoothSocket.close();//關閉藍牙通訊
                } catch (IOException e1) {//例外處理
                    e1.printStackTrace();
                }
            }
            if (success) { //connect successful
                final String msgconnected = "connect successful:\n"+ "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;
                runOnUiThread(new Runnable() { //執行UI的執行緒更程式畫面, runnable被執行的程式
                    public void run()
                    {
                        //例外拋出訊息，第一個取得activity權限,第二個參數為要傳送的資料，第三個參數是時間長短
                        Toast.makeText(StartPage.this, msgconnected, Toast.LENGTH_SHORT).show();
                    }
                });
                myThreadConnected = new ThreadConnected(bluetoothSocket);//運用執行緒啟動藍牙讀與接受訊息
                myThreadConnected.start();//執行
            } else {
                runOnUiThread(new Runnable() {//執行UI的執行緒更程式畫面, runnable被執行的程式
                    public void run()
                    {
                        //例外拋出訊息，第一個取得activity權限,第二個參數為要傳送的資料，第三個參數是時間長短
                        Toast.makeText(StartPage.this, "something wrong bluetoothSocket.connect(): ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
    ThreadConnected myThreadConnected=null;
    public class ThreadConnected extends Thread {
        public final BluetoothSocket connectedBluetoothSocket;//new bluetooth socket
        public final InputStream connectedInputStream;//new read Socket
        public final OutputStream connectedOutputStream;//new send Socket

        public ThreadConnected(BluetoothSocket socket) {//constructor
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream(); //read input
                out = socket.getOutputStream(); //read output
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;//set input
            connectedOutputStream = out;//set output
        }
        @Override
        public void run() {
            byte[] buffer = new byte[1024];//給接收String的空間
            int bytes;//給接收String的byte
            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);//如果input 讀到資料
                    String strReceived = new String(buffer, 0, bytes);//放入strReceived
                    if(strReceived.equals("A")){
                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(1);
                        TCP_SocketClientThread.start();
                    }else if(strReceived.equals("B")){
                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(2);
                        TCP_SocketClientThread.start();
                    }else if(strReceived.equals("C")){
                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(3);
                        TCP_SocketClientThread.start();
                    }else if(strReceived.equals("D")){
                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(4);
                        TCP_SocketClientThread.start();
                    }
                    final String msgReceived =strReceived;
                    runOnUiThread(new Runnable(){ ////執行UI的執行緒更程式畫面, runnable被執行的程式
                        @Override
                        public void run() {
                        }});
                } catch (IOException e) {//例外處理
                    e.printStackTrace();
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);//寫入output stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
