package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.List;

import data.DatabaseHandler;
import model.Remind;


public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private DatabaseHandler db;
    private static final int MAX_TEXT_LENGTH = 20;
    private LayoutInflater inflater;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = getLayoutInflater();
        linearLayout = findViewById(R.id.linearLayout);
        db = new DatabaseHandler(this);

        loadReminds();
    }

    private void loadReminds() {
        List<Remind> reminds = db.getAllReminds();
        linearLayout.removeAllViews();

        int marginInPx = (int) (15 * getResources().getDisplayMetrics().density);;

        for (Remind remind : reminds) {
            Button remindButton = createRemindButton(remind, marginInPx);
            remindButton.setOnClickListener(v -> openRemindDetails(remind.getId()));
            linearLayout.addView(remindButton);
        }
    }

    private Button createRemindButton(Remind remind, int margin) {
        Button button = (Button) inflater.inflate(R.layout.remind_button, linearLayout, false);

        button.setText(remind.getText().length() > 20 ? remind.getText().substring(0, 20) + "..." : remind.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin, margin, margin);
        button.setLayoutParams(params);
        return button;
    }

    public void addRemind(View v) {
        Intent intent = new Intent(this, AddRemindActivity.class);
        startActivity(intent);
    }

    private void openRemindDetails(int remindId) {
        Intent intent = new Intent(this, RemindDetailsActivity.class);
        intent.putExtra("REMIND_ID", remindId);
        startActivity(intent);
    }

    public void deleteAllReminds(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_all_reminds_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAllReminds();
                        loadReminds();
                        Toast.makeText(MainActivity.this, R.string.all_notes_deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}