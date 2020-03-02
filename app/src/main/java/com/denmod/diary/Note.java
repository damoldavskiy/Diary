package com.denmod.diary;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Serializable;

public class Note implements Element, Serializable {

    private String name;
    private Group group;

    Note(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Group getGroup() {
        return group;
    }

    void setGroup(Group group) {
        this.group = group;
    }

    void rename(String name) {
        try {
            NotesFileSystem.renameInList(this, name);
            NotesFileSystem.getPath(this).renameTo(NotesFileSystem.getNotePath(getGroup(), name));
            this.name = name;
        } catch (IOException e) {
            Log.e("Note.rename", e.getMessage());
        }
    }

    void write() {
        try {
            NotesFileSystem.addToList(this);
            NotesFileSystem.getPath(this).mkdirs();
        } catch (IOException e) {
            Log.e("Note.write", e.getMessage());
        }
    }

    void writeText(String content) {
        try {
            FileSystem.writeText(NotesFileSystem.getTextPath(this), content);
        } catch (IOException e) {
            Log.e("Note.writeText", e.getMessage());
        }
    }

    void writePhoto(Bitmap bitmap) {
        try {
            FileSystem.writeImage(NotesFileSystem.getPhotoPath(this), bitmap);
        } catch (IOException e) {
            Log.e("Note.writePhoto", e.getMessage());
        }
    }

    void delete() {
        try {
            NotesFileSystem.deleteFromList(this);
            FileSystem.deleteRecursively(NotesFileSystem.getPath(this));
            group.remove(this);
        } catch (IOException e) {
            Log.e("Note.delete", e.getMessage());
        }
    }

    void deletePhoto() {
        if (!NotesFileSystem.getPhotoPath(this).delete());
            Log.e("Note.deletePhoto", "Photo not deleted");
    }

    String readText() {
        try {
            return FileSystem.readText(NotesFileSystem.getTextPath(this));
        } catch (IOException e) {
            Log.e("Note.readText", e.getMessage());
            return null;
        }
    }

    Bitmap readPhoto() {
        try {
            return FileSystem.readImage(NotesFileSystem.getPhotoPath(this));
        } catch (IOException e) {
            Log.e("Note.readPhoto", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Note))
            return false;
        Note other = (Note)obj;
        return other.group.equals(group) && other.name.equals(name);
    }
}
