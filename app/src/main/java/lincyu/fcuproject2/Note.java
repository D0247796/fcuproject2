package lincyu.fcuproject2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Note extends AppCompatActivity {
    InetAddress client_ip;
    String ip="";
    Button btn_addnote;
    ListView lv_notes;
    SQLiteDatabase db;
    ArrayList<String> titlelist;
    int note_state=1;
    String Server_title="",Server_content="";
    Thread TCP_SocketServerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        btn_addnote = (Button)findViewById(R.id.btn_addnote);
        btn_addnote.setOnClickListener(btn_addnote_CL);

        lv_notes = (ListView)findViewById(R.id.lv_notes);
        lv_notes.setOnItemClickListener(write_note);
        lv_notes.setOnItemLongClickListener(delet_note);

        Intent intent=getIntent();
        ip=intent.getStringExtra("IP");

         TCP_SocketServerThread = new Thread(new TCP_SocketServerThread());
        TCP_SocketServerThread.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }
    @Override
    protected void onResume() {
        super.onResume();

        DBOpenHelper openhelper = new DBOpenHelper(this);
        db = openhelper.getWritableDatabase();

        titlelist = NoteDB.getTitleList(db);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, titlelist);
        lv_notes.setAdapter(adapter);
    }

    View.OnClickListener btn_addnote_CL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Note.this,
                    AddNote.class);
            intent.putExtra("NOTEPOS", -1);
            intent.putExtra("IP", ip);
            startActivity(intent);
        }
    };
    AdapterView.OnItemClickListener write_note = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> av, View v,
                                int position, long id) {
            Intent intent = new Intent();
            intent.setClass(Note.this,
                    AddNote.class);
            intent.putExtra("NOTEPOS", position);
            startActivity(intent);
        }
    };
    AdapterView.OnItemLongClickListener delet_note = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> av, View v,
                                       int position, long id) {
            String title = titlelist.get(position);
            TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread("delete",title);
            TCP_SocketClientThread.start();
            NoteDB.delNote(db, title);
            titlelist = NoteDB.getTitleList(db);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (Note.this,
                            android.R.layout.simple_list_item_1, titlelist);
            lv_notes.setAdapter(adapter);
            return false;
        }

    };
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
                TCP_server_serversocket= new ServerSocket(6600);
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
                    TCP_server_dout.writeUTF("Note_Server");
                    if(note_state==1){
                        Server_title=cleint_msg;
                        note_state++;
                    }
                    else if(note_state==2 && Server_title.equals("delete")){
                        Server_content=cleint_msg;
                        //UI更新
                        Note.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                NoteDB.delNote(db, Server_content);
                                titlelist = NoteDB.getTitleList(db);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                        (Note.this,
                                                android.R.layout.simple_list_item_1, titlelist);
                                lv_notes.setAdapter(adapter);
                                note_state=1;
                            }
                        });
                    }
                    else if(note_state==2){
                        Server_content=cleint_msg;
                        NoteDB.addNote(db,Server_title, Server_content);
                        //UI更新
                        Note.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    DBOpenHelper openhelper = new DBOpenHelper(Note.this);
                                    db = openhelper.getWritableDatabase();

                                    titlelist = NoteDB.getTitleList(db);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                            (Note.this, android.R.layout.simple_list_item_1, titlelist);
                                    lv_notes.setAdapter(adapter);
                                    note_state=1;
                                }catch (Exception e){}


                            }
                        });


                    }


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

        String title,content;
        public TCP_SocketClientThread(String title ,String content){
            this.title=title;
            this.content=content;

        }
        @Override
        public void run() {
            Socket TCP_client_socket = null;
//            String ip=et_account.getText().toString();
            DataOutputStream dout = null;
            DataInputStream din =null;
            try{


                TCP_client_socket = new Socket(InetAddress.getByName(ip),6666);
                dout = new DataOutputStream(TCP_client_socket.getOutputStream());
                din = new DataInputStream(TCP_client_socket.getInputStream());
                dout.writeUTF(title);
                String server_msg = din.readUTF(); //這是裡傳來的訊息

                TCP_client_socket.close();


            }
            catch (Exception e){
                Note.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

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
            try{


                TCP_client_socket = new Socket(InetAddress.getByName(ip),6666);
                dout = new DataOutputStream(TCP_client_socket.getOutputStream());
                din = new DataInputStream(TCP_client_socket.getInputStream());
                dout.writeUTF(content);
                String server_msg = din.readUTF(); //這是裡傳來的訊息

                TCP_client_socket.close();


            }
            catch (Exception e){
                Note.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

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
}
