package uyox.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by eikebehrends on 22.07.14.
 */
public class SimpleAlertDialog {

    private AlertDialog dialog;
    private DialogInterface.OnClickListener customListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    };

    public SimpleAlertDialog(Context context, String btnTxt, String msg, String title) {
        AlertDialog.Builder builder = genBuilder(context, btnTxt, msg, title);
        this.dialog = builder.create();
        dialog.show();
    }

    public SimpleAlertDialog(Context context, String btnTxt, String msg, String title,
                             DialogInterface.OnClickListener customListener) {
        this.customListener = customListener;
        AlertDialog.Builder builder = genBuilder(context, btnTxt, msg, title);
        this.dialog = builder.create();
        dialog.show();
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    private AlertDialog.Builder genBuilder(Context context, String btnTxt, String msg, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
            .setTitle(title)
            .setNegativeButton(btnTxt, customListener);;
        return builder;
    }
}
