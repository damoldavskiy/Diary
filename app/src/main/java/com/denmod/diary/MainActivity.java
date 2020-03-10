package com.denmod.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE = "NOTE";
    public static final String ACTION = "ACTION";
    public static final String OPEN_IMMEDIATELY = "OPEN_IMMEDIATELY";

    public static final int VIEW_RESULT = 1;

    List<Group> list;
    RecyclerView recycler;
    NoteAdapter adapter;
    boolean blocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());

        try {
            NotesFileSystem.setRoot(new java.io.File(Environment.getExternalStorageDirectory(), "Diary"));
        } catch (IOException e) {
            Log.e("Error", "Set path: " + e.getMessage());
        }

        Note note = (Note)getIntent().getSerializableExtra(MainActivity.NOTE);
        if (note != null) { // Started from widget
            if (!note.getGroup().exists())
                note.getGroup().write();

            String name = note.getName();
            int number = 0;
            while (note.exists())
                note.setName(name + " " + ++number);
            note.write();
        }

        list = NotesFileSystem.readGroups();
        adapter = new NoteAdapter(this, list);

        recycler = findViewById(R.id.list);
        recycler.setAdapter(adapter);

        if (note != null) { // List and note created, opening
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra(MainActivity.NOTE, note);
            intent.putExtra(MainActivity.OPEN_IMMEDIATELY, true);
            startActivityForResult(intent, MainActivity.VIEW_RESULT);
            adapter.setSelected(note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_group:
                if (blocked) {
                    Toast.makeText(this, R.string.toast_block, Toast.LENGTH_LONG).show();
                    return true;
                }

                Dialogs.InputDialog(this, R.string.dialog_new_group, "", name -> {
                    if (name.length() == 0)
                        return;

                    Group group = new Group(name);
                    group.write();
                    list.add(group);
                    adapter.notifyItemInserted(list.size() - 1);
                });
                return true;
            case R.id.about:
                Dialogs.AboutDialog(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.toast_block, Toast.LENGTH_LONG).show();
            blocked = true;
        } else
            recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            if (requestCode == VIEW_RESULT) {
                Note note = (Note)data.getSerializableExtra(NOTE);
                int position = adapter.getItems().indexOf(adapter.getSelected());
                int action = data.getIntExtra(ACTION, 0);
                // Note and its group given are serialized, so we should modify ones from adapter
                switch (action) {
                    case R.id.rename:
                        adapter.getSelected().setName(note.getName());
                        adapter.notifyItemChanged(position);
                        break;
                    case R.id.delete:
                        adapter.getSelected().getGroup().remove(note);
                        adapter.getItems().remove(position);
                        adapter.notifyItemRemoved(position);
                        break;
                }
            }
    }
}
