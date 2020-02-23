package com.denmod.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE = "NOTE";

    List<Group> list;
    RecyclerView recycler;
    RecyclerView.Adapter adapter;

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
            NotesFileSystem.setPath(new java.io.File(Environment.getExternalStorageDirectory(), "Diary"));
        } catch (IOException e) {
            Log.e("Error", "Set path: " + e.getMessage());
        }
        list = NotesFileSystem.readGroups();

        adapter = new NoteAdapter(this, list);

        recycler = findViewById(R.id.list);
        recycler.setAdapter(adapter);
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
        recreate();
    }
}
