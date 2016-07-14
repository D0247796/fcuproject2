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
//    //Server端
//    private class TCP_SocketServerThread extends Thread {
//
//        String cleint_msg;
//        @Override
//        public void run() {
//            ServerSocket TCP_server_serversocket =null;
//            Socket TCP_server_socket =null;
//            DataInputStream TCP_server_din = null;
//            DataOutputStream TCP_server_dout =null;
//
//            try{
//                TCP_server_serversocket= new ServerSocket(8888);
//            }
//            catch(Exception e){
//                e.printStackTrace();;
//            }
//            while(true){
//                try{
//                    TCP_server_socket=TCP_server_serversocket.accept();
////                    client_ip=s.getInetAddress().toString();
//                    TCP_server_din = new DataInputStream(TCP_server_socket.getInputStream());
//                    TCP_server_dout = new DataOutputStream(TCP_server_socket.getOutputStream());
//
//                    cleint_msg = TCP_server_din.readUTF(); //這是裡傳來的訊息
//                    if(cleint_msg.equals("User_Client")){
//                        client_ip = TCP_server_socket.getInetAddress();
//
//                    }
//                    if(myThreadConnected!=null) {
//                        byte[] bluetoothmsg = cleint_msg.toString().getBytes();
//                        myThreadConnected.write(bluetoothmsg);//執行緒output訊息
//                    }
//
//
//
//                    TCP_server_dout.writeUTF("Car_Server");
//                }
//                catch(Exception e){
//                    e.getStackTrace();
//                }
//                finally{
//                    try{
//                        if(TCP_server_dout !=null){
//                            TCP_server_dout.close();
//                        }
//                        if(TCP_server_din !=null){
//                            TCP_server_din.close();
//                        }
//                        if(TCP_server_socket != null){
//                            TCP_server_socket.close();
//                        }
//                    }
//                    catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//
//    }
//    //Client端
//    private class TCP_SocketClientThread extends Thread {
//
//        int msg;
//        public TCP_SocketClientThread(int msg){
//            this.msg=msg;
//
//        }
//        @Override
//        public void run() {
//            Socket TCP_client_socket = null;
////            String ip=et_account.getText().toString();
//            DataOutputStream dout = null;
//            DataInputStream din =null;
//            try{
////                s= new Socket("192.168.1.106",8880);
//                InetAddress ip=client_ip;
//                TCP_client_socket = new Socket(ip,8880);
//                dout = new DataOutputStream(TCP_client_socket.getOutputStream());
//                din = new DataInputStream(TCP_client_socket.getInputStream());
//                dout.writeInt(msg);
//                server_msg = din.readUTF(); //這是裡傳來的訊息
//
//                TCP_client_socket.close();
//
//
//            }
//            catch (Exception e){
//
//            }
//            finally {
//                try{
//                    if(dout !=null){
//                        dout.close();
//                    }
//                    if(din !=null){
//                        din.close();
//                    }
//                    if(TCP_client_socket != null){
//                        TCP_client_socket.close();
//                    }
//                }catch (Exception e){
//                    e.getStackTrace();
//                }
//            }
//
//
//        }
//
//
//    }
//    //藍芽
//    public void SetupPairedDevices() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//取得藍牙的初始Adapter
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices(); //取得配對過的資料
//        if (pairedDevices.size() > 0) { //如果配對的資料大於0
//            pairedDeviceArrayList = new ArrayList<BluetoothDevice>(); //new bluetooth arraylist來存放BLE的設備資料
//            for (BluetoothDevice device : pairedDevices) {
//                pairedDeviceArrayList.add(device); //在pairedDeviceArrayList增加資料
//            }
//            //建立可以對應listview的容器，並且將pairedDeviceArrayList的資料導入
////            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, pairedDeviceArrayList);
////            AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
////            dialog_list.setTitle("附近藍芽裝置");
////            dialog_list.setItems(pairedDeviceAdapter, new DialogInterface.OnClickListener(){
////                @Override
////                public void onClick(DialogInterface dialog, int which) {
////                 //取得選取position項目的藍牙設備資料
////
////                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device); //設定藍牙連線的執行緒
////                    myThreadConnectBTdevice.start(); //啟動此執行緒
////                }
////            });
////            dialog_list.show();
//            lv_bluetoothdevice.setAdapter(pairedDeviceAdapter);//設定導入listview顯示畫面
//            lv_bluetoothdevice.setOnItemClickListener(new AdapterView.OnItemClickListener() { //設定當觸發listview時
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);//取得選取position項目的藍牙設備資料
//                    tv_bluetoothstate.setText("start ThreadConnectBTdevice");//設定textview2的文字
//                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device); //設定藍牙連線的執行緒
//                    myThreadConnectBTdevice.start(); //啟動此執行緒
//                }
//            });
//        }
//    }
//    ThreadConnectBTdevice myThreadConnectBTdevice;
//
//    public class ThreadConnectBTdevice extends Thread {
//
//        public BluetoothSocket bluetoothSocket = null; //建立bluetooth通訊
//        public final BluetoothDevice bluetoothDevice;  //建立藍牙設備
//
//        public ThreadConnectBTdevice(BluetoothDevice device) { //初始化建構子
//            bluetoothDevice = device; //將傳進來的藍牙設置放入此類別的設置
//            try {
//                //建構一個溝通的socket,利用UUID
//                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));//利用UUID建立通訊鏈接
//
//            } catch (IOException e) { //如果有出錯,例外處理
//                e.printStackTrace();
//            }
//        }
//        @Override
//        public void run() { //當ThreadConnectBTdevice被觸發與執行後
//            boolean success = false;//假設藍牙連線失敗
//            try {
//                bluetoothSocket.connect();//藍牙連線
//                success = true;//狀態改成功
//            } catch (IOException e) {//有狀況發現
//                e.printStackTrace();
//                try {
//                    bluetoothSocket.close();//關閉藍牙通訊
//                } catch (IOException e1) {//例外處理
//                    e1.printStackTrace();
//                }
//            }
//            if (success) { //connect successful
//                final String msgconnected = "connect successful:\n"+ "BluetoothSocket: " + bluetoothSocket + "\n"
//                        + "BluetoothDevice: " + bluetoothDevice;
//                runOnUiThread(new Runnable() { //執行UI的執行緒更程式畫面, runnable被執行的程式
//                    public void run()
//                    {
//                        //例外拋出訊息，第一個取得activity權限,第二個參數為要傳送的資料，第三個參數是時間長短
//                        Toast.makeText(RunPage.this, msgconnected, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                myThreadConnected = new ThreadConnected(bluetoothSocket);//運用執行緒啟動藍牙讀與接受訊息
//                myThreadConnected.start();//執行
//            } else {
//                runOnUiThread(new Runnable() {//執行UI的執行緒更程式畫面, runnable被執行的程式
//                    public void run()
//                    {
//                        //例外拋出訊息，第一個取得activity權限,第二個參數為要傳送的資料，第三個參數是時間長短
//                        Toast.makeText(RunPage.this, "something wrong bluetoothSocket.connect(): ", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }
//    }
//    ThreadConnected myThreadConnected;
//    public class ThreadConnected extends Thread {
//        public final BluetoothSocket connectedBluetoothSocket;//new bluetooth socket
//        public final InputStream connectedInputStream;//new read Socket
//        public final OutputStream connectedOutputStream;//new send Socket
//
//        public ThreadConnected(BluetoothSocket socket) {//constructor
//            connectedBluetoothSocket = socket;
//            InputStream in = null;
//            OutputStream out = null;
//            try {
//                in = socket.getInputStream(); //read input
//                out = socket.getOutputStream(); //read output
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            connectedInputStream = in;//set input
//            connectedOutputStream = out;//set output
//        }
//        @Override
//        public void run() {
//            byte[] buffer = new byte[1024];//給接收String的空間
//            int bytes;//給接收String的byte
//            while (true) {
//                try {
//                    bytes = connectedInputStream.read(buffer);//如果input 讀到資料
//                    String strReceived = new String(buffer, 0, bytes);//放入strReceived
//                    if(strReceived.equals("A")){
//                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(1);
//                        TCP_SocketClientThread.start();
//                    }else if(strReceived.equals("B")){
//                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(2);
//                        TCP_SocketClientThread.start();
//                    }else if(strReceived.equals("C")){
//                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(3);
//                        TCP_SocketClientThread.start();
//                    }else if(strReceived.equals("D")){
//                        TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread(4);
//                        TCP_SocketClientThread.start();
//                    }
//                    final String msgReceived =strReceived;
//                    runOnUiThread(new Runnable(){ ////執行UI的執行緒更程式畫面, runnable被執行的程式
//                        @Override
//                        public void run() {
//
//                        }});
//                } catch (IOException e) {//例外處理
//                    e.printStackTrace();
//                }
//            }
//        }
//        public void write(byte[] buffer) {
//            try {
//                connectedOutputStream.write(buffer);//寫入output stream
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//    //UDP
//    public class UDP_Client extends Thread {
//        int udp_port = 8080;
//        byte[] buf = new byte[1000];
//        byte[] buf2 = new byte[1000];
//
//        @Override
//        public void run() {
//            try {
//
//                // Construct a datagram socket and bind it to any available port
//                DatagramSocket socket = new DatagramSocket();
//
//                String msg = et_bluetoothmsg.getText().toString();
//                // Encode this String into a sequence of bytes and store to buf.
//                buf = msg.getBytes();
//                // Construct a datagram packet for sending packets of length length to
//                // the specified port number on the specified host.
//                DatagramPacket packet = new DatagramPacket(buf, buf.length, client_ip, udp_port);
//                // Send message
//                socket.send(packet);
//
////                // Construct a DatagramPacket for receiving packet
////                DatagramPacket recPacket = new DatagramPacket(buf2, buf2.length);
////                // Receive a datagram packet from this socket
////                socket.receive(recPacket);
////                // Process message
////                InetAddress senderAddr = recPacket.getAddress();
////                int senderPort = recPacket.getPort();
////                String reply = new String("Receive message '" + new String(buf2, 0, buf2.length) +
////                        "' from address : " + senderAddr +
////                        ", port : " + senderPort);
////                System.out.println(reply);
//
//                socket.close();
//            } catch (Exception e) {
//
//            }
//        }
//
//    }
//




}
