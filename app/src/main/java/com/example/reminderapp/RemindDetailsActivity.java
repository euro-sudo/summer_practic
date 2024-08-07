package com.example.reminderapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import data.DatabaseHandler;
import model.Remind;

public class RemindDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RemindDetailsActivity";
    private static final int IMAGE_VIEW_MAX_SIZE_DP = 250;

    private TextView noteText;
    private ScrollView scrollBack;
    private LinearLayout linearLayout;
    private DatabaseHandler db;
    private int remindId;
    private int backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_details);

        noteText = findViewById(R.id.noteText);
        linearLayout = findViewById(R.id.linearLayout);
        scrollBack = findViewById(R.id.scrollBack);
        db = new DatabaseHandler(this);

        remindId = getIntent().getIntExtra("REMIND_ID", -1);
        if (remindId != -1) {
            loadRemindDetails(remindId);
        }
    }


    private void loadRemindDetails(int remindId) {
        Remind remind = db.getRemind(remindId);
        noteText.setText(remind.getText());
        noteText.setTextSize(remind.getTextSize());
        noteText.setBackgroundColor(remind.getBackColor());
        backgroundColor = remind.getBackColor();

        setNoteBackgroundColor(backgroundColor);


        List<String> photoPaths = remind.getPhotos();
        for (String path : photoPaths) {
            addImageView(Uri.parse(path));
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

    private void setNoteBackgroundColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(30 * getResources().getDisplayMetrics().density);
        findViewById(R.id.scrollBack).setBackground(drawable);
    }

    public void back(View v) {
        finish();
    }

    public void deleteRemind(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_remind_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    db.deleteRemind(remindId);
                    Toast.makeText(RemindDetailsActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
