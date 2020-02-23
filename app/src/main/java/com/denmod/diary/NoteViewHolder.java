package com.denmod.diary;

import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private NoteAdapter adapter;
    private Note note;
//    private int index;
    private TextView nameView;

    NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewActivity.class);
            intent.putExtra(MainActivity.NOTE, note);
            v.getContext().startActivity(intent);
        });

        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            int index = adapter.getItems().indexOf(note);

            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.note_context);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.rename:
                        Dialogs.InputDialog(v.getContext(), R.string.dialog_rename_note, note.getName(), name -> {
                            note.rename(name);
                            nameView.setText(note.getName());
                        });
                        break;
                    case R.id.delete:
                        note.delete();
                        adapter.getItems().remove(index);
                        adapter.notifyItemRemoved(index);
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
//        this.index = index;
        nameView.setText(note.getName());
    }
}
