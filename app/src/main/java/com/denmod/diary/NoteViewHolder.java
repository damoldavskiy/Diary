package com.denmod.diary;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private NoteAdapter adapter;
    private Note note;
    private TextView nameView;

    NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewActivity.class);
            intent.putExtra(MainActivity.NOTE, note);
            ((Activity)v.getContext()).startActivityForResult(intent, MainActivity.VIEW_RESULT);
            adapter.setSelected(note);
        });

        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.note_context);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.rename:
                        Dialogs.InputDialog(v.getContext(), R.string.dialog_rename_note, note.getName(), name -> {
                            if (name.length() == 0 || name.equals(note.getName()))
                                return;
                            if (!note.rename(name)) {
                                Toast.makeText(v.getContext(), R.string.toast_exists, Toast.LENGTH_LONG).show();
                                return;
                            }
                            note.rename(name);
                            nameView.setText(note.getName());
                        });
                        break;
                    case R.id.delete:
                        note.delete();
                        int position = adapter.getItems().indexOf(note);
                        adapter.getItems().remove(position);
                        adapter.notifyItemRemoved(position);
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        nameView = itemView.findViewById(R.id.name);

    }

    void bind(NoteAdapter adapter, int index) {
        this.adapter = adapter;
        this.note = (Note)adapter.getItems().get(index);
        nameView.setText(note.getName());
    }
}
