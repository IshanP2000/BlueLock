package com.test.logintesttwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    /******************************/
    private BluetoothSocket socket;
    //UUID uuid = UUID.fromString("ae14f5e2-9eb6-4015-8457-824d76384ba0");
    BluetoothDevice bt_device = null;

    final byte delimiter = 35;
    int readBufferPosition = 0;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int BLUETOOTH_SCAN = 2;

    private BluetoothAdapter bluetoothAdapter;

    public ImageButton unlock_button;
    public ImageButton lock_button;

    private Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> PairedDevices;
    /*********************************/
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        /**********************************
         Add Bluetooth Functionality here
         *************************************/
        unlock_button = (ImageButton) findViewById(R.id.imageView5);
        lock_button = (ImageButton) findViewById(R.id.imageView4);

        final Handler handler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(
                    new String[] { android.Manifest.permission.BLUETOOTH_CONNECT },
                    REQUEST_ENABLE_BT);
        }

        pairedDevices = bluetoothAdapter.getBondedDevices();
        PairedDevices = new ArrayList<>(pairedDevices);

        for (int i = 0; i < PairedDevices.size(); i++) {

            String name = PairedDevices.get(i).getName();

            //The name in quotes must match your device
            if(name.equals("raspberrypi"))
            {
                bt_device = PairedDevices.get(i);
                break;
            }
        }

        //Check if Bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final class workerThread implements Runnable {

            private String message;

            public workerThread(String msg) {message = msg;}

            public void run() {

                sendMessage(message);

                while(!Thread.currentThread().isInterrupted()) {

                    int bytesAvailable;
                    boolean workDone = false;

                    try {

                        final InputStream inputStream;
                        inputStream = socket.getInputStream();
                        bytesAvailable = inputStream.available();

                        if(bytesAvailable > 0)
                        {

                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("Bytes received from", "Raspberry Pi");
                            byte[] readBuffer = new byte[1024];
                            inputStream.read(packetBytes);

                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    workDone = true;
                                    break;
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }

                            if (workDone == true){
                                socket.close();
                                break;
                            }

                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        }

        //Unlock Listener
        unlock_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Entered listener event!!!");
                (new Thread(new workerThread("Unlock"))).start();

            }
        });

        //Lock Listener
        lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new Thread(new workerThread("Lock"))).start();

            }
        });
        /***********************************/


    }

    /**********************************/
    public void sendMessage(String send_message) {

        UUID uuid = UUID.fromString("ae14f5e2-9eb6-4015-8457-824d76384ba0");

        try {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(
                        new String[] { android.Manifest.permission.BLUETOOTH_SCAN },
                        BLUETOOTH_SCAN);

            }
            socket = bt_device.createRfcommSocketToServiceRecord(uuid);
            if (!socket.isConnected()) {
                socket.connect();
            }

            String message = send_message;

            OutputStream btOutputStream = socket.getOutputStream();
            btOutputStream.write(message.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Request was cancelled, but permission granted (?)");
                } else {
                    System.out.println("Permission granted");
                }
            }
            case BLUETOOTH_SCAN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Request was cancelled, but permission granted (?)");
                } else {
                    System.out.println("Permission granted");
                }
            }
            break;
        }
    }
    /*****************************************/

    public void signout(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}