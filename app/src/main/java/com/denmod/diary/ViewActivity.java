package com.denmod.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class ViewActivity extends AppCompatActivity {

    public static final int PHOTO_CAMERA = 1;
    public static final int PHOTO_GALLERY = 2;

    Note note;
    ImageView photo;
    EditText content;
    boolean editing;
    boolean zoom;

    MenuItem edit;
    MenuItem end;
    MenuItem attach;
    MenuItem detach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editing = getIntent().getBooleanExtra(MainActivity.OPEN_IMMEDIATELY, false);
        getIntent().removeExtra(MainActivity.OPEN_IMMEDIATELY);

        note = (Note)getIntent().getSerializableExtra(MainActivity.NOTE);
        photo = findViewById(R.id.photo);
        photo.setOnLongClickListener(v -> {
            if (zoom) {
                photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                photo.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
            } else {
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photo.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            photo.requestLayout();
            zoom = !zoom;
            return true;
        });
        content = findViewById(R.id.content);
        content.setOnLongClickListener(v -> {
            if (!editing) {
                setEditing(true);
                updateMenu();
                return true;
            }
            return false;
        });
        content.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager inputMethodManager = (InputMethodManager)ViewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus)
                inputMethodManager.showSoftInput(content, InputMethodManager.SHOW_FORCED);
            else
                inputMethodManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
        });

        photo.getLayoutParams().height = 500;

        updateTitle();
        setBitmap(note.readPhoto());
        setEditing(editing);
        content.setText(note.readText());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editing)
            saveText();
        closeKeyboard();
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
                closeKeyboard();
                return true;
            case R.id.edit:
                setEditing(true);
                break;
            case R.id.end:
                setEditing(false);
                saveText();
                break;
            case R.id.attach:
                Dialogs.PhotoDialog(this, option -> {
                    if (option == R.string.dialog_camera) {
                        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(NotesFileSystem.getPhotoPath(note)));
                        startActivityForResult(imageIntent, PHOTO_CAMERA);
                    } else {
                        Intent imageIntent = new Intent(Intent.ACTION_PICK);
                        imageIntent.setType("image/*");
                        startActivityForResult(imageIntent, PHOTO_GALLERY);
                    }
                });
                break;
            case R.id.detach:
                setBitmap(null);
                note.deletePhoto();
                break;
            case R.id.rename:
                Dialogs.InputDialog(this, R.string.dialog_rename_note, note.getName(), name -> {
                    if (name.length() == 0 || name.equals(note.getName()))
                        return;
                    note.rename(name);
                    updateTitle();
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.NOTE, note);
                    intent.putExtra(MainActivity.ACTION, R.id.rename);
                    setResult(RESULT_OK, intent);
                });
                break;
            case R.id.delete:
                setEditing(false);
                note.delete();
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOTE, note);
                intent.putExtra(MainActivity.ACTION, R.id.delete);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.send:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                if (photo.getMeasuredHeight() != 0)
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(NotesFileSystem.getPhotoPath(note)));
                else
                    Log.e("ViewActivity.onOptionsItemSelected", "Photo not found");
                sendIntent.putExtra(Intent.EXTRA_TEXT, note.getName() + System.lineSeparator() + content.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
                break;
        }
        updateMenu();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PHOTO_CAMERA:
                setBitmap(note.readPhoto());
                break;
            case PHOTO_GALLERY:
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    setBitmap(bitmap);
                    note.writePhoto(bitmap);
                } catch (IOException e) {
                    Log.e("ViewActivity.onActivityResult", e.getMessage());
                }
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

    void updateTitle()
    {
        setTitle("# " + note.getName());
    }

    void saveText() {
        note.writeText(content.getText().toString());
    }

    void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)ViewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
    }
}
