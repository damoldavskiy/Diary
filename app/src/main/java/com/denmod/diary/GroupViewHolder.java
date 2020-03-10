package com.denmod.diary;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupViewHolder extends RecyclerView.ViewHolder {

    private NoteAdapter adapter;
    private Group group;
    private TextView nameView;

    GroupViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            int index = adapter.getItems().indexOf(group);
            if (group.isExpanded()) {
                for (int i = 0; i < group.size(); ++i)
                    adapter.getItems().remove(index + 1);
                adapter.notifyItemRangeRemoved(index + 1, group.size());
            } else {
                for (int i = 0; i < group.size(); ++i)
                    adapter.getItems().add(index + i + 1, group.get(i));
                adapter.notifyItemRangeInserted(index + 1, group.size());
            }
            group.setExpanded(!group.isExpanded());
            updateTitle();
        });

        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            int index = adapter.getItems().indexOf(group);

            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.group_context);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.new_note:
                        Dialogs.InputDialog(v.getContext(), R.string.dialog_new_note, "", name -> {
                            if (name.length() == 0)
                                return;

                            Note note = new Note(name);
                            group.add(note);
                            note.write();
                            if (group.isExpanded()) {
                                adapter.getItems().add(index + group.size(), note);
                                adapter.notifyItemInserted(index + group.size());
                            }

                            Intent intent = new Intent(v.getContext(), ViewActivity.class);
                            intent.putExtra(MainActivity.NOTE, note);
                            ((Activity)v.getContext()).startActivityForResult(intent, MainActivity.VIEW_RESULT);
                            adapter.setSelected(note);
                        });
                        break;
                    case R.id.new_note_date:
                        String defaultName = Utility.getDate();
                        Dialogs.InputDialog(v.getContext(), R.string.dialog_new_note, defaultName ,name -> {
                            if (name.length() == 0)
                                return;

                            Note note = new Note(name);
                            group.add(note);
                            note.write();
                            if (group.isExpanded()) {
                                adapter.getItems().add(index + group.size(), note);
                                adapter.notifyItemInserted(index + group.size());
                            }
                        });
                        break;
                    case R.id.rename:
                        Dialogs.InputDialog(v.getContext(), R.string.dialog_rename_group, group.getName(), name -> {
                            group.rename(name);
                            updateTitle();
                        });
                        break;
                    case R.id.delete:
                        Dialogs.SureDialog(v.getContext(), (result -> {
                            if (!result)
                                return;
                            if (group.isExpanded()) {
                                for (int i = 0; i < group.size(); ++i)
                                    adapter.getItems().remove(index + 1);
                                adapter.notifyItemRangeRemoved(index + 1, group.size());
                            }
                            group.delete();
                            adapter.getItems().remove(index);
                            adapter.notifyItemRemoved(index);
                        }));
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
        this.group = (Group)adapter.getItems().get(index);
        updateTitle();
    }

    void updateTitle() {
        nameView.setText(group.isExpanded() ? "- " + group.getName() : "+ " + group.getName());
    }
}
