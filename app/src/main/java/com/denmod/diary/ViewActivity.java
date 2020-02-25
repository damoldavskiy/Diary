package com.denmod.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewActivity extends AppCompatActivity {

    public static final int PHOTO = 1;

    Note note;
    ImageView photo;
    EditText content;
    boolean editing;

    MenuItem edit;
    MenuItem end;
    MenuItem attach;
    MenuItem detach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = (Note)getIntent().getSerializableExtra(MainActivity.NOTE);
        photo = findViewById(R.id.photo);
        content = findViewById(R.id.content);

        setTitle("# " + note.getName());
        setBitmap(note.readPhoto());
        setEditing(editing);
        content.setText(note.read());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editing)
            saveText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view, menu);

        attach = menu.findItem(R.id.attach);
        detach = menu.findItem(R.id.detach);
        edit = menu.findItem(R.id.edit);
        end = menu.findItem(R.id.end);
        updateMenu();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit:
                setEditing(true);
                break;
            case R.id.end:
                setEditing(false);
                saveText();
                break;
            case R.id.attach:
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(NotesFileSystem.getPhotoPath(note)));
                startActivityForResult(imageIntent, PHOTO);
                break;
            case R.id.detach:
                setBitmap(null);
                note.deletePhoto();
                break;
            case R.id.send:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, note.getName() + "\n" + content.getText().toString());
                startActivity(Intent.createChooser(sendIntent, null));
                break;
        }
        updateMenu();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO:
                if (resultCode == RESULT_OK)
                    setBitmap(note.readPhoto());
                break;
        }
        updateMenu();
    }

    void setBitmap(Bitmap bitmap)
    {
        if (bitmap == null)
            photo.setVisibility(View.GONE);
        else
            photo.setVisibility(View.VISIBLE);

        photo.setImageBitmap(bitmap);
    }

    void setEditing(boolean value) {
        content.setFocusable(value);
        content.setFocusableInTouchMode(value);
        content.setCursorVisible(value);
        editing = value;

        if (value)
            content.requestFocus();
    }

    void updateMenu()
    {
        if (detach != null)
            detach.setVisible(photo.getVisibility() == View.VISIBLE);
        if (edit != null)
            edit.setVisible(!editing);
        if (end != null)
            end.setVisible(editing);
    }

    void saveText() {
        note.writeText(content.getText().toString());
    }
}
