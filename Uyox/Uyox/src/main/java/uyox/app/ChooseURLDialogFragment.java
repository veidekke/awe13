package uyox.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sbrink on 22.07.2014.
 */
public class ChooseURLDialogFragment extends DialogFragment {

    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] urls = (String[]) getArguments().get("urls");
        builder.setTitle("Choose a URL")
                .setItems(urls, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // URL has been chosen
                        if (activity instanceof WriteTagActivity)
                            // Show URL in UI
                            ((WriteTagActivity) activity).showUrl(urls[which]);
                        else if (activity instanceof MainActivity) {
                            Map<String, String> mediaInfos = new HashMap<String, String>();
                            mediaInfos.put("URL", urls[which]);
                            ((MainActivity) activity).playMedia(mediaInfos);
                        }
                    }
                });
        return builder.create();
    }
}
