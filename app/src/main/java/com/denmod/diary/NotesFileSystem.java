package com.denmod.diary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NotesFileSystem {

    private static File root;

    static void setRoot(File root) throws IOException {
        NotesFileSystem.root = root;
        root.mkdirs();
        getList().createNewFile();
    }

    static File getPath(Group group) {
        return new File(root, group.getName());
    }

    static File getPath(Note note) {
        return new File(getPath(note.getGroup()), note.getName());
    }

    static File getGroupPath(String name) {
        return new File(root, name);
    }

    static File getNotePath(Group group, String name) {
        return new File(getPath(group), name);
    }

    static File getTextPath(Note note) {
        return new File(getPath(note), "data.txt");
    }

    static File getPhotoPath(Note note) {
        return new File(getPath(note), "photo.jpg");
    }

    static File getList(File path) {
        return new File(path, "list.txt");
    }

    static File getList() {
        return getList(root);
    }

    static File getList(Group group) {
        return getList(getPath(group));
    }

    static ArrayList<String> readGroupNames() throws IOException {
        return FileSystem.readLines(getList());
    }

    static ArrayList<String> readNoteNames(Group group) throws IOException {
        return FileSystem.readLines(getList(group));
    }

    static void addToList(Group group) throws IOException {
        FileSystem.appendLine(getList(), group.getName());
    }

    static void addToList(Note note) throws IOException {
        FileSystem.appendLine(getList(note.getGroup()), note.getName());
    }

    static void deleteFromList(Group group) throws IOException {
        ArrayList<String> groups = readGroupNames();
        groups.remove(group.getName());
        FileSystem.writeLines(getList(), groups);
    }

    static void deleteFromList(Note note) throws IOException {
        ArrayList<String> notes = readNoteNames(note.getGroup());
        notes.remove(note.getName());
        FileSystem.writeLines(getList(note.getGroup()), notes);
    }

    static void renameInList(Group group, String newName) throws IOException {
        ArrayList<String> groups = readGroupNames();
        groups.set(groups.indexOf(group.getName()), newName);
        FileSystem.writeLines(getList(), groups);
    }

    static void renameInList(Note note, String newName) throws IOException {
        ArrayList<String> notes = readNoteNames(note.getGroup());
        notes.set(notes.indexOf(note.getName()), newName);
        FileSystem.writeLines(getList(note.getGroup()), notes);
    }

    public static ArrayList<Group> readGroups() {
        try {
            if (!getList().exists())
                return null;

            ArrayList<String> groupDirs = readGroupNames();
            ArrayList<Group> groups = new ArrayList<>();

            for (int i = 0; i < groupDirs.size(); ++i) {
                File groupDir = new File(root, groupDirs.get(i));

                if (groupDir.isDirectory()) {
                    if (!getList(groupDir).exists())
                        continue;

                    ArrayList<String> noteDirs = FileSystem.readLines(getList(groupDir));
                    ArrayList<Note> notes = new ArrayList<>();

                    for (int j = 0; j < noteDirs.size(); ++j) {
                        File noteDir = new File(groupDir, noteDirs.get(j));

                        if (noteDir.isDirectory())
                            notes.add(new Note(noteDir.getName()));
                    }
                    groups.add(new Group(groupDir.getName(), notes));
                }
            }

            return groups;
        } catch (IOException e) {
            return null;
        }
    }
}
