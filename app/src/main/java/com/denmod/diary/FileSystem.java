package com.denmod.diary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileSystem {
    public static void writeText(File path, String content) throws IOException {
        FileOutputStream stream = new FileOutputStream(path);
        stream.write(content.getBytes());
        stream.close();
    }

    public static void writeImage(File path, Bitmap bitmap) throws IOException {
        FileOutputStream stream = new FileOutputStream(path);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.close();
    }

    public static String readText(File path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.lineSeparator());
        }

        return builder.toString();
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

    public static boolean deleteRecursively(File path) {
        boolean flag = true;
        if (path.isDirectory())
            for (File file : path.listFiles())
                flag &= deleteRecursively(file);
        return flag & path.delete();
    }

    public static Bitmap readImage(File path) throws IOException {
        FileInputStream stream = new FileInputStream(path);
        return BitmapFactory.decodeStream(stream);
    }
}
