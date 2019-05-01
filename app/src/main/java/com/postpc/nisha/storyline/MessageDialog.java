//package com.postpc.nisha.storyline;
//
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.util.concurrent.Callable;
//
///**
// * pop up message dialog
// */
//public class MessageDialog {
//
//    public void showDialog(Activity activity, String msg, String btnText, final Callable<Void> helper){
//        final Dialog dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog_message);
//
//        TextView text = dialog.findViewById(R.id.text_dialog);
//        text.setText(msg);
//
//        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
//        dialogButton.setText(btnText);
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try { helper.call(); }
//                catch (Exception e) { e.printStackTrace(); }
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//
//    }
//}