package com.denmod.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.function.Consumer;

public class Dialogs {
    public static void InputDialog(Context context, int title, String value, Consumer<String> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.input, null);
        final EditText input = view.findViewById(R.id.input);
        input.setText(value);

        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.dialog_accept, (dialog, which) -> action.accept(input.getText().toString()))
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel())
                .create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public static void AboutDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        AlertDialog alertDialog = builder
                .setTitle(R.string.dialog_about)
                .setMessage(R.string.app_description)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> dialog.cancel())
                .create();

        alertDialog.show();
    }
}
