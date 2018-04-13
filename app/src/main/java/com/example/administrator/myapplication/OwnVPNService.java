package com.example.administrator.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.VpnService;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class OwnVPNService extends VpnService implements Handler.Callback, Runnable{
    private static final String TAG = "OwnVPNService";
    private String mServerAddress;
    private String mServerPort;
    @SuppressWarnings("unused")
    private PendingIntent mConfigureIntent;
    private byte[] mSharedSecret;
    private Handler mHandler;
    private Thread mThread;
    private ParcelFileDescriptor mInterface;
    private String mParameters;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startReceiving();

        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }
        // Information about server
        mServerAddress = "10.10.10.157";
        mServerPort = "1723";
//        mServerAddress = "127.0.0.1";
//        mServerPort = "8087";
        mSharedSecret = "test".getBytes();

        // Start a new session by creating a new thread.
        mThread = new Thread(this, "OwnVPNService");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (mThread != null) {
            mThread.interrupt();
        }

        stopReceiving();
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        try {

            Log.i(TAG, "开始...");

            // If anything needs to be obtained using the network, get it now.
            // This greatly reduces the complexity of seamless handover, which
            // tries to recreate the tunnel without shutting down everything.
            // In this demo, all we need to know is the server address.
            InetSocketAddress server = new InetSocketAddress(
                    mServerAddress, Integer.parseInt(mServerPort));

            // We try to create the tunnel for several times. The better way
            // is to work with ConnectivityManager, such as trying only when
            // the network is available. Here we just use a counter to keep
            // things simple.
            for (int attempt = 0; attempt < 5; ++attempt) {

                sendStatus(0);
                mHandler.sendEmptyMessage(R.string.connecting);
                // Reset the counter if we were connected.
                if (run(server)) {
                    attempt = 0;
                }
                // Sleep for a while. This also checks if we got interrupted.
                Thread.sleep(3000);
            }
            Log.i(TAG, "已经放弃");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "得到： " + e.toString()+"  "+e.getCause()+"  "+e.getStackTrace());
        } finally {
            try {
                mInterface.close();
            } catch (Exception e) {
                // ignore
            }
            mInterface = null;
            mParameters = null;
            sendStatus(2);
            mHandler.sendEmptyMessage(R.string.disconnected);
            Log.i(TAG, "正在退出");
        }
    }

    private boolean run(InetSocketAddress server) throws Exception {
        DatagramChannel tunnel = null;
        boolean connected = false;
        try {
            // Create a DatagramChannel as the VPN tunnel.
            tunnel = DatagramChannel.open();
            // Protect the tunnel before connecting to avoid loopback.
            if (!protect(tunnel.socket())) {
                throw new IllegalStateException("无法保护tunnel");
            }
            // Connect to the server.
            tunnel.connect(server);
            // For simplicity, we use the same thread for both reading and
            // writing. Here we put the tunnel into non-blocking mode.
            tunnel.configureBlocking(false);
            // Authenticate and configure the virtual network interface.
            handshake(tunnel);
            // Now we are connected. Set the flag and show the message.
            connected = true;
            sendStatus(1);
            mHandler.sendEmptyMessage(R.string.connected);
            // Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
            // Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
            // Allocate the buffer for a single packet.
            ByteBuffer packet = ByteBuffer.allocate(32767);
            // We use a timer to determine the status of the tunnel. It
            // works on both sides. A positive value means sending, and
            // any other means receiving. We start with receiving.
            int timer = 0;
            // We keep forwarding packets till something goes wrong.
            //noinspection InfiniteLoopStatement
            while (true) {
                // Assume that we did not make any progress in this iteration.
                boolean idle = true;
                // Read the outgoing packet from the input stream.
                int length = in.read(packet.array());
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    packet.limit(length);
                    tunnel.write(packet);
                    packet.clear();
                    // There might be more outgoing packets.
                    idle = false;
                    // If we were receiving, switch to sending.
                    if (timer < 1) {
                        timer = 1;
                    }
                }
                // Read the incoming packet from the tunnel.
                length = tunnel.read(packet);
                if (length > 0) {
                    // Ignore control messages, which start with zero.
                    if (packet.get(0) != 0) {
                        // Write the incoming packet to the output stream.
                        out.write(packet.array(), 0, length);
                    }
                    packet.clear();
                    // There might be more incoming packets.
                    idle = false;
                    // If we were sending, switch to receiving.
                    if (timer > 0) {
                        timer = 0;
                    }
                }
                // If we are idle or waiting for the network, sleep for a
                // fraction of time to avoid busy looping.
                if (idle) {
                    Thread.sleep(100);
                    // Increase the timer. This is inaccurate but good enough,
                    // since everything is operated in non-blocking mode.
                    timer += (timer > 0) ? 100 : -100;
                    // We are receiving for a long time but not sending.
                    if (timer < -15000) {
                        // Send empty control messages.
                        packet.put((byte) 0).limit(1);
                        for (int i = 0; i < 3; ++i) {
                            packet.position(0);
                            tunnel.write(packet);
                        }
                        packet.clear();
                        // Switch to sending.
                        timer = 1;
                    }
                    // We are sending for a long time but not receiving.
                    if (timer > 20000) {
                        throw new IllegalStateException("超时A");
                    }
                }
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "得到A： " + e.toString()+"  "+e.getCause()+" "+e.getStackTrace());
        } finally {
            try {
                assert tunnel != null;
                tunnel.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return connected;
    }

    private void handshake(DatagramChannel tunnel) throws Exception {
        // To build a secured tunnel, we should perform mutual authentication
        // and exchange session keys for encryption. To keep things simple in
        // this demo, we just send the shared secret in plaintext and wait
        // for the server to send the parameters.
        // Allocate the buffer for handshaking.
        ByteBuffer packet = ByteBuffer.allocate(1024);
        // Control messages always start with zero.
        packet.put((byte) 0).put(mSharedSecret).flip();
        // Send the secret several times in case of packet loss.
        for (int i = 0; i < 3; ++i) {
            packet.position(0);
            tunnel.write(packet);
        }
        packet.clear();
        // Wait for the parameters within a limited time.
        for (int i = 0; i < 50; ++i) {
            Thread.sleep(100);
            // Normally we should not receive random packets.
            int length = tunnel.read(packet);
            if (length > 0 && packet.get(0) == 0) {
                configure(new String(packet.array(), 1, length - 1).trim());
                return;
            }
        }
        throw new IllegalStateException("超时B");
    }

    private void configure(String parameters) throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null && parameters.equals(mParameters)) {
            Log.i(TAG, "使用上一个接口");
            return;
        }
        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        Log.i(TAG,"配置参数： "+parameters);
        for (String parameter : parameters.split(" ")) {
            String[] fields = parameter.split(",");
            try {
                switch (fields[0].charAt(0)) {
                    case 'm':
                        builder.setMtu(Short.parseShort(fields[1]));
                        break;
                    case 'a':
                        builder.addAddress(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'r':
                        builder.addRoute(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'd':
                        builder.addDnsServer(fields[1]);
                        break;
                    case 's':
                        builder.addSearchDomain(fields[1]);
                        break;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("错误的参数: " + parameter);
            }
        }
        // Close the old interface since the parameters have been changed.
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }
        // Create a new interface using the builder and save the parameters.
        mInterface = builder.setSession(mServerAddress)
                .setConfigureIntent(mConfigureIntent)
                .establish();
        mParameters = parameters;
        Log.i(TAG, "新接口: " + parameters);
    }

    public void stopVpn() {

        if (mThread != null) {
            mThread.interrupt();
        }

        stopReceiving();
        stopSelf();
    }

    public void sendStatus(int status) {

        Intent intent = new Intent("status");

        // Adding some data
        intent.putExtra("status_code", status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // Handling the received Intents
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            boolean status = intent.getBooleanExtra("stop_code", false);
            if(status) {
                stopVpn();
            }
        }

    };

    private void startReceiving() {
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("stopvpn"));
    }

    public void stopReceiving() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
    }
}
