package com.denmod.diary;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Element, Serializable {

    private String name;
    private ArrayList<Note> notes;
    private boolean expanded; // TODO Makes Group View-Model

    public Group(String name) {
        this(name, new ArrayList<>());
    }

    public Group(String name, ArrayList<Note> notes) {
        this.name = name;
        this.notes = notes;

        for (Note note : notes)
            note.setGroup(this);
    }

    public String getName() {
        return name;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean value) {
        expanded = value;
    }

    public void add(Note note) {
        notes.add(note);
        note.setGroup(this);
    }

    public void remove(Note note) {
        notes.remove(note);
        note.setGroup(null);
    }

    public Note get(int position) {
        return notes.get(position);
    }

    public int size() {
        return notes.size();
    }

    public void write() {
        try {
            NotesFileSystem.write(this);
        } catch (IOException ignored) { }
    }

    public void rename(String name) {
        try {
            NotesFileSystem.rename(this, name);
            this.name = name;
        } catch (IOException e) { }
    }

    public void delete() {
        try {
            NotesFileSystem.delete(this);
        } catch (IOException e) { }
    }
}
