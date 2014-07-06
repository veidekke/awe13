package com.example.ami_project.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    /** Called when the activity is first created. */

    private static String TAG = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        Log.d(TAG, "onPause");

        disableForegroundMode();
    }

    @Override
    public void onNewIntent(Intent intent) { //
        Log.d(TAG, "onNewIntent");

        // check for NFC related actions
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            TextView textView = (TextView) findViewById(R.id.title);
            textView.setText("Hello NFC tag!");
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages != null) {
                Log.d(TAG, "Found " + messages.length + " NDEF messages");

                vibrate(); // signal found messages :-)

                // parse to records
                for (int i = 0; i < messages.length; i++) {
                    try {
                        List<Record> records = new org.ndeftools.Message((NdefMessage)messages[i]);

                        Log.d(TAG, "Found " + records.size() + " records in message " + i);

                        for(int k = 0; k < records.size(); k++) {
                            Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());

                            Record record = records.get(k);
                            if(record instanceof AndroidApplicationRecord) {
                                AndroidApplicationRecord aar = (AndroidApplicationRecord)record;
                                Log.d(TAG, "Package is " + aar.getPackageName());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Problem parsing message", e);
                    }

                }
            }
        } else {
            // ignore
        }
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }
    /**
     * Activate device vibrator for 500 ms
     * */

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

}
