package uyox.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.nio.charset.Charset;


public class WriteTagActivity extends Activity {
    private static String TAG = MainActivity.class.getSimpleName();
    private ContentDirectoryBrowser contentDirectoryBrowser;
    private boolean audioIsChecked;
    private boolean readyForWriting = false;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        Intent i = getIntent();
        contentDirectoryBrowser = (ContentDirectoryBrowser)i.getSerializableExtra("contentDirectoryBrowser");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void writeTagClicked(View view){
        readyForWriting = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ready for writing!")
                .setTitle("Write Tag")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readyForWriting = false;
                        dialog.cancel();
                    }
                });;

        // 3. Get the AlertDialog from create()
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onNewIntent(Intent intent) { //
        if (readyForWriting) {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                writeTag(intent);
            }
        }
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

        disableForegroundMode();
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

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    public NdefRecord getRecord(String type){
        String payload = "";
        if(type.equals("Title")){
            payload = ((EditText) findViewById(R.id.title)).getText().toString();
        } else if(type.equals("Album")){
            payload = ((EditText) findViewById(R.id.album)).getText().toString();
        } else if(type.equals("Artist")){
            payload = ((EditText) findViewById(R.id.artist)).getText().toString();
        } else if(type.equals("Type")){
            payload = audioIsChecked ? "Audio" : "Video";
        } else if(type.equals("URL")){
            TextView urlTextView = (TextView) findViewById(R.id.url_found);
            String url = (String) urlTextView.getText();
            payload = (url == null || url.equals(""))? "None" : url;
        }
        return new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA ,
                type.getBytes(Charset.forName("US-ASCII")),
                new byte[0], payload.getBytes(Charset.forName("US-ASCII")));
    }

    public boolean writeTag(Intent intent){
        Log.d("Test", "test");
        vibrate();
        NdefRecord appRecord = NdefRecord.createApplicationRecord(this.getPackageName());
        NdefRecord titleRecord = getRecord("Title");
        NdefRecord typeRecord = getRecord("Type");
        NdefRecord urlRecord = getRecord("URL");
        NdefMessage message = null;
        if (audioIsChecked){
            message = new NdefMessage(new NdefRecord[] {appRecord, titleRecord, typeRecord,
                    urlRecord, getRecord("Album"), getRecord("Artist")});
        } else {
            message = new NdefMessage(new NdefRecord[] {appRecord, titleRecord, typeRecord, urlRecord});
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if(!ndef.isWritable()) {
                    Log.d(TAG, "not writeable");
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    dialog.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Uyox Tag is ready to Role!")
                            .setTitle("Success!")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    readyForWriting = false;
                                    dialog.cancel();
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                    return true;
                } catch (Exception e){
                    Log.d(TAG, "Exception");
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        Log.d(TAG, "successfully formatted");
                        return true;
                    } catch (Exception e) {
                        Log.d(TAG, "Exception during formatting");
                        return false;
                    }
                } else {
                    Log.d(TAG, "format ist null");
                    return false;
                }
            }
        } catch(Exception e) {
            Log.d(TAG, "Exception during getTag()");
        }
        return false;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.audio:
                if (checked) {
                    audioIsChecked = true;
                    showAudioForm();
                    break;
                }

            case R.id.video:
                if (checked) {
                    audioIsChecked = false;
                    showVideoform();
                    break;
                }
        }
    }

    public void searchBtnClicked(View view){
        String url = null;
        if(audioIsChecked){
            String title = ((EditText) findViewById(R.id.title)).getText().toString();
            String album = ((EditText) findViewById(R.id.album)).getText().toString();
            String artist = ((EditText) findViewById(R.id.artist)).getText().toString();
            url = contentDirectoryBrowser.searchAudio("All that she wants", null, null);
        } else {
            String title = ((EditText) findViewById(R.id.title)).getText().toString();
            url = contentDirectoryBrowser.searchVideo(title);
        }

        TextView urlTextView = (TextView) findViewById(R.id.url_found);

        if(url != null){
            urlTextView.setText(url);
        } else {
            urlTextView.setHint("No URL found");
        }
    }

    private void showAudioForm(){
        EditText title = (EditText) findViewById(R.id.title);
        EditText album = (EditText) findViewById(R.id.album);
        EditText artist = (EditText) findViewById(R.id.artist);

        title.setVisibility(View.VISIBLE);
        album.setVisibility(View.VISIBLE);
        artist.setVisibility(View.VISIBLE);

        showButtons();
    }

    private void showVideoform(){
        EditText title = (EditText) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        showButtons();
    }

    private void showButtons(){
        Button writeBtn = (Button) findViewById(R.id.write_tag);
        Button searchBtn = (Button) findViewById(R.id.search_url);
        TextView urlTextView = (TextView) findViewById(R.id.url_found);

        writeBtn.setVisibility(View.VISIBLE);
        urlTextView.setVisibility(View.VISIBLE);
        searchBtn.setVisibility(View.VISIBLE);
    }
}
