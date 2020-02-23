package com.denmod.diary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NotesFileSystem {

    private static File root;

    public static File getPath(Group group) {
        return new File(root, group.getName());
    }

    public static File getPath(Note note) {
        return new File(getPath(note.getGroup()), note.getName());
    }

    public static File getTextPath(Note note) {
        return new File(getPath(note), "data.txt");
    }

    public static File getPhotoPath(Note note) {
        return new File(getPath(note), "photo.jpg");
    }

    public static void write(Group group) throws IOException {
        getPath(group).mkdirs();
        getList(getPath(group)).createNewFile();
        appendLine(getList(root), group.getName());
    }

    public static void write(Note note) throws IOException {
        getPath(note).mkdirs();
        appendLine(getList(getPath(note.getGroup())), note.getName());
    }

    public static void writeText(Note note, String content) throws IOException {
        FileOutputStream stream = new FileOutputStream(getTextPath(note));
        stream.write(content.getBytes());
    }

    public static String readText(Note note) throws IOException {
        FileInputStream stream = new FileInputStream(getTextPath(note));
        StringBuilder content = new StringBuilder();

        byte[] buffer = new byte[1024];
        while (stream.read(buffer) != -1)
            content.append(new String(buffer));

        return content.toString();
    }

    public static Bitmap readPhoto(Note note) throws IOException {
        FileInputStream stream = new FileInputStream(getPhotoPath(note));
        return BitmapFactory.decodeStream(stream);
    }

    public static boolean delete(Group group) throws IOException {
        ArrayList<String> groups = readLines(getList(root));
        groups.remove(group.getName());
        writeLines(getList(root), groups);
        return deleteRecursively(getPath(group));
    }

    public static boolean delete(Note note) throws IOException {
        ArrayList<String> notes = readLines(getList(getPath(note.getGroup())));
        notes.remove(note.getName());
        writeLines(getList(getPath(note)), notes);
        return deleteRecursively(getPath(note));
    }

    public static boolean deleteText(Note note) {
        return getTextPath(note).delete();
    }

    public static boolean deletePhoto(Note note) {
        return getPhotoPath(note).delete();
    }

    public static boolean rename(Group group, String name) throws IOException{
        ArrayList<String> groups = readLines(getList(root));
        int index = groups.indexOf(group.getName());
        groups.set(index, name);
        writeLines(getList(root), groups);
        return getPath(group).renameTo(new File(root, name));
    }

    public static boolean rename(Note note, String name) throws IOException {
        ArrayList<String> notes = readLines(getList(getPath(note.getGroup())));
        int index = notes.indexOf(note.getName());
        notes.set(index, name);
        writeLines(getList(getPath(note.getGroup())), notes);
        return getPath(note).renameTo(new File(getPath(note.getGroup()), name));
    }

    public static void setPath(File path) throws IOException {
        NotesFileSystem.root = path;
        root.mkdirs();
        if (!getList(root).exists())
            getList(root).createNewFile();
    }

    public static ArrayList<Group> readGroups() {
        try {
            if (!getList(root).exists()) {
                Log.e("Error", "Read groups: no Diary/list.txt");
                return null;
            }

            ArrayList<String> groupDirs = readLines(getList(root));
            ArrayList<Group> groups = new ArrayList<>();

            for (int i = 0; i < groupDirs.size(); ++i) {
                File groupDir = new File(root, groupDirs.get(i));

                if (groupDir.isDirectory()) {
                    if (!getList(groupDir).exists())
                        continue;

                    ArrayList<String> noteDirs = readLines(getList(groupDir));
                    ArrayList<Note> notes = new ArrayList<>();

                    for (int j = 0; j < noteDirs.size(); ++j) {
                        File noteDir = new File(groupDir, noteDirs.get(j));

                        if (noteDir.isDirectory()) {
                            notes.add(new Note(noteDir.getName()));
                        }
                    }
                    groups.add(new Group(groupDir.getName(), notes));
                }
            }

            return groups;
        } catch (IOException e) {
            Log.e("Error", "Read groups: " + e.getMessage());
            return null;
        }
    }

    public static boolean deleteRecursively(File path) {
        boolean flag = true;
        if (path.isDirectory())
            for (File file : path.listFiles())
                flag &= deleteRecursively(file);
        return flag & path.delete();
    }

    public static ArrayList<String> readLines(File path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        ArrayList<String> result = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null)
            result.add(line);

        return result;
    }

    public static void writeLines(File path, ArrayList<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public static void appendLines(File path, ArrayList<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public static void appendLine(File path, String line) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        list.add(line);
        appendLines(path, list);
    }

    public static File getList(File path) {
        return new File(path, "list.txt");
    }
}
