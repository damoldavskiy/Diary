package com.denmod.diary;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.Serializable;

public class Note implements Element, Serializable {

    private String name;
    private Group group;

    public Note(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void rename(String value) {
        try {
            NotesFileSystem.rename(this, value);
            name = value;
        } catch (IOException ignored) { }
    }

    public void write() {
        try {
            NotesFileSystem.write(this);
        } catch (IOException ignored) { }
    }

    public boolean writeText(String content) {
        try {
            NotesFileSystem.writeText(this, content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean delete() {
        try {
            boolean flag = NotesFileSystem.delete(this);
            if (flag)
                group.remove(this);
            return flag;
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean deletePhoto() {
        return NotesFileSystem.deletePhoto(this);
    }

    public String read() {
        try {
            return NotesFileSystem.readText(this);
        } catch (IOException e) {
            return null;
        }
    }

    public Bitmap readPhoto() {
        try {
            return NotesFileSystem.readPhoto(this);
        } catch (IOException e) {
            return null;
        }
    }
}
