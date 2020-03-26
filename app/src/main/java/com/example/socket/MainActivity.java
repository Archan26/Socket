package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Socket";
    EditText e1,e2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e1 = (EditText)findViewById(R.id.edittext);
        e2 = (EditText)findViewById(R.id.edittext2);
        Thread myThread = new Thread(new MyServer());
        myThread.start();
    }


    class MyServer implements Runnable{
        ServerSocket ss;
        Socket mysocket;
        DataInputStream dis;
        String message;
        Handler handler =new Handler();
        @Override
        public void run() {
            try {
                ss = new ServerSocket(8486);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Waiting for client",Toast.LENGTH_SHORT).show();
                    }
                });
                while (true){
                    mysocket = ss.accept();
                    dis = new DataInputStream(mysocket.getInputStream());
                    Log.d(TAG,"dis:"+dis);
                    message = dis.readUTF();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"message received from client: "+message,Toast.LENGTH_SHORT).show();
                    }
                    });
                }
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }


    public void Button_Click(View v){
        BackgroundTask b = new BackgroundTask();
        b.execute(e1.getText().toString(),e2.getText().toString());

    }

    class BackgroundTask extends AsyncTask<String,Void,String> {
        Socket s;
        DataOutputStream dos;
        String ip,message;
        @Override
        protected String doInBackground(String... strings) {
            ip = strings[0];
            message = strings[1];

            try {
                s = new Socket(ip,8486);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);
                dos.close();
                s.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
