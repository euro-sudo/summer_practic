package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.List;
import data.DatabaseHandler;
import model.Remind;

public class AddRemindActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final float DEFAULT_TEXT_SIZE = 18f;
    private static final int MAX_TEXT_SIZE = 72;
    private static final int IMAGE_VIEW_MAX_SIZE_DP = 250;

    private EditText noteText;
    private LinearLayout linearLayout;
    private List<Uri> photoUris = new ArrayList<>();
    private int backgroundColor = Color.WHITE;
    private float textSize = DEFAULT_TEXT_SIZE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);

        noteText = findViewById(R.id.noteText);
        linearLayout = findViewById(R.id.linearLayout);
    }

    public void back(View v) {
        finish();
    }

    public void changeColor(View v) {
        backgroundColor = Color.argb(255, (int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        setRemindBackgroundColor(backgroundColor);
    }

    private void setRemindBackgroundColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);

        drawable.setCornerRadius(30 * getResources().getDisplayMetrics().density);
        findViewById(R.id.scrollBack).setBackground(drawable);
    }

    public void changeTextSize(View v) {
        textSize = (textSize < MAX_TEXT_SIZE) ? textSize + 6 : DEFAULT_TEXT_SIZE;
        noteText.setTextSize(textSize);
    }

    public void addPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            photoUris.add(imageUri);
            addImageView(imageUri);
        } else {
            Toast.makeText(this, R.string.failed_to_pick_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageView(Uri imageUri) {
        ImageView photoView = new ImageView(this);
        Glide.with(this)
                .load(imageUri)
                .transform(new RoundedCorners(30))
                .into(photoView);
        photoView.setLayoutParams(createLayoutParams());
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photoView.setAdjustViewBounds(true);
        linearLayout.addView(photoView);
    }

    private ViewGroup.LayoutParams createLayoutParams() {
        int size = (int) (IMAGE_VIEW_MAX_SIZE_DP * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.setMargins(15, 15, 15, 15);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL; // Add this line
        return layoutParams;
    }


    public void saveRemind(View v) {
        String text = noteText.getText().toString();

        if (text.isEmpty()) {
            Toast.makeText(this, R.string.note_text_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHandler db = new DatabaseHandler(this);

        List<String> photoPaths = new ArrayList<>();
        for (Uri uri : photoUris) {
            photoPaths.add(uri.toString());
        }

        Remind remind = new Remind(text, backgroundColor, textSize, photoPaths);
        db.addRemind(remind);

        Toast.makeText(this, R.string.reminder_saved, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }
}