package com.denmod.diary;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Element, Serializable {

    private String name;
    private ArrayList<Note> notes;
    private boolean expanded; // TODO Makes Group View-Model

    Group(String name) {
        this(name, new ArrayList<>());
    }

    Group(String name, ArrayList<Note> notes) {
        this.name = name;
        this.notes = notes;

        for (Note note : notes)
            note.setGroup(this);
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    boolean isExpanded() {
        return expanded;
    }

    void setExpanded(boolean value) {
        expanded = value;
    }

    void add(Note note) {
        notes.add(note);
        note.setGroup(this);
    }

    void remove(Note note) {
        notes.remove(note);
//        note.setGroup(null);
    }

    Note get(int position) {
        return notes.get(position);
    }

    int size() {
        return notes.size();
    }

    void write() {
        try {
            NotesFileSystem.addToList(this);
            NotesFileSystem.getPath(this).mkdirs();
            NotesFileSystem.getList(this).createNewFile();
        } catch (IOException e) {
            Log.e("Group.write", e.getMessage());
        }
    }

    void rename(String name) {
        try {
            NotesFileSystem.renameInList(this, name);
            NotesFileSystem.getPath(this).renameTo(NotesFileSystem.getGroupPath(name));
            this.name = name;
        } catch (IOException e) {
            Log.e("Group.rename", e.getMessage());
        }
    }

    void delete() {
        try {
            NotesFileSystem.deleteFromList(this);
            FileSystem.deleteRecursively(NotesFileSystem.getPath(this));
            for (Note note : notes)
                note.setGroup(null);
        } catch (IOException e) {
            Log.e("Note.delete", e.getMessage());
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Group))
            return false;
        Group other = (Group)obj;
        return other.name.equals(name);
    }
}
