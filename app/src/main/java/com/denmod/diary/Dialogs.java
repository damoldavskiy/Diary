package com.denmod.diary;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.function.Consumer;

public class Dialogs {
    public static void InputDialog(Context context, int title, String value, Consumer<String> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.input, null);
        final EditText input = view.findViewById(R.id.input);

        input.append(value);

        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.dialog_accept, (dialog, which) -> action.accept(input.getText().toString()))
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel())
                .create();

        input.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                action.accept(input.getText().toString());
                alertDialog.dismiss();
            }
            return false;
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public static void AboutDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder
                .setTitle(R.string.dialog_about)
                .setMessage(R.string.app_description)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.cancel())
                .create();

        alertDialog.show();
    }

    public static void PhotoDialog(Context context, Consumer<Integer> action) {
        TypedArray options = context.getResources().obtainTypedArray(R.array.photo_options);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder
                .setTitle(R.string.dialog_photo_option)
                .setItems(R.array.photo_options, (dialog, which) -> {
                    action.accept(options.getResourceId(which, -1));
                    options.recycle();
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel())
                .create();

        alertDialog.show();
    }

    public static void SureDialog(Context context, java.util.function.Consumer<Boolean> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder
                .setTitle(R.string.dialog_sure)
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> action.accept(true))
                .setNegativeButton(R.string.dialog_no, (dialog, which) -> action.accept(false))
                .create();

        alertDialog.show();
    }
}
