package lincyu.fcuproject2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class AddNote extends AppCompatActivity {
    EditText et_name,et_content;
    ArrayList<String> titlelist;
    SQLiteDatabase db;

    String start_title,ip="";
    int notepos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        et_content=(EditText)findViewById(R.id.et_content);
        et_name=(EditText)findViewById(R.id.et_name);

        Intent intent = getIntent();
        notepos = intent.getIntExtra("NOTEPOS", -1);
        ip = intent.getStringExtra("IP");


    }
    @Override
    protected void onResume() {
        super.onResume();
        DBOpenHelper openhelper = new DBOpenHelper(this);
        db = openhelper.getWritableDatabase();

        titlelist = NoteDB.getTitleList(db);

        if (notepos != -1) {
            start_title = titlelist.get(notepos);
            et_name.setText(start_title);
            et_content.setText(NoteDB.getBody(db, start_title));
        } else {
            et_name.setText("");
            et_content.setText("");
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(notepos!=-1) {
            NoteDB.delNote(db, start_title);
        }

        String title = et_name.getText().toString();
        boolean title_exist =isTitleExist("title");
        if (title.length() == 0) {
            Toast.makeText(this, "標題不能為空白，便條無儲存",
                    Toast.LENGTH_LONG).show();
        }
        else if(title_exist==true){
            NoteDB.addNote(db, et_name.getText().toString()+"重複",
                    et_content.getText().toString());
            TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread( et_name.getText().toString()+"重複", et_content.getText().toString());
            TCP_SocketClientThread.start();
        }
        else {

            NoteDB.addNote(db, et_name.getText().toString(),
                    et_content.getText().toString());
            TCP_SocketClientThread TCP_SocketClientThread = new TCP_SocketClientThread( et_name.getText().toString(), et_content.getText().toString());
            TCP_SocketClientThread.start();



        }
    }

    boolean isTitleExist(String title) {
        for (int i = 0; i < titlelist.size(); i++)
            if (title.equalsIgnoreCase(titlelist.get(i)))
                return true;
        return false;
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
                AddNote.this.runOnUiThread(new Runnable() {

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
                AddNote.this.runOnUiThread(new Runnable() {

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
