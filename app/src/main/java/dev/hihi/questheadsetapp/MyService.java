package dev.hihi.questheadsetapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Set;

    public class MyService extends Service {

    private static final String TAG = "MyService";
    private BluetoothChatService mBluetoothChatService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return Service.START_STICKY;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.i(TAG, "STATE_CONNECTED");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.i(TAG, "STATE_CONNECTING");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.i(TAG, "STATE_LISTEN or STATE_NONE");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i(TAG, "writeMessage: " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 2, msg.arg1);
                    Log.i(TAG, "readMessage: " + readMessage);
                    try {
                        JSONObject json = new JSONObject(readMessage);
                        String packageName = json.optString("package_name");
                        String action = json.getString("action");
                        String tag = packageName + ", " + json.optString("tag");
                        int id = json.optInt("id");

                        if ("post".equals(action)) {
                            String title = json.getString("title");
                            String text = json.getString("text");
                            QuestNotificationUtils.sendNotification(MyService.this, tag, id, title,
                                    "[" + title + "] " + text);
                        } else if ("remove".equals(action)) {
                            QuestNotificationUtils.cancelNotification(MyService.this, tag, id);
                        } else if ("push_url".equals(action)) {
                            String url = json.getString("url");
                            Intent intent = new Intent("com.oculus.vrshell.intent.action.LAUNCH");
                            intent.setPackage("com.oculus.vrshell");
                            intent.putExtra("intent_data", (Parcelable) Uri.parse("systemux://browser"));
                            intent.putExtra("uri", url);
                            intent.putExtra("blackscreen", false);
                            sendBroadcast(intent);
                        } else {
                            Log.i(TAG, "Unknown action: " + action);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.i(TAG, "MESSAGE_DEVICE_NAME: " + deviceName);
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MyService.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Make it FG service so won't be killed easily
        if (mBluetoothChatService == null) {
            mBluetoothChatService = new BluetoothChatService(this, mHandler);
            new Thread() {
                public void run() {
                    while (true) {
                        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                            if (mBluetoothChatService.getState() == BluetoothChatService.STATE_NONE ||
                                    mBluetoothChatService.getState() == BluetoothChatService.STATE_LISTEN) {
                                tryConnectServer();
                            } else {
                                Log.i(TAG, "BT state != STATE_NONE");
                            }
                        } else {
                            Log.i(TAG, "BT not enabled");
                        }
                        SystemClock.sleep(5000);
                    }
                }
            }.start();
        }
        startForeground(8964, new NotificationCompat.Builder(this, "NA").build());
    }

    private void tryConnectServer() {
        Log.i(TAG, "tryConnectServer");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String address = device.getAddress();
                Log.i(TAG, "Found paired device: " + address);
                mBluetoothChatService.connect(device, false);
            }
        } else {
            Log.i(TAG, "no paired device, bye");
            // Toast.makeText(this, "no paired device, bye", Toast.LENGTH_SHORT).show();
        }
    }
}
