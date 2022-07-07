package com.example.help_m5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "ReportActivity";
    private Button submitButton;
    private Button cancelButton;
    private GoogleSignInAccount account;
    private String userEmail;
    private String reportedUserEmail;
    private String comment;
    private boolean reportUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Bundle bundle = getIntent().getExtras();
        reportedUserEmail = bundle.getString("user_email");

        account = GoogleSignIn.getLastSignedInAccount(this);
        userEmail = account.getEmail();

        EditText editText = findViewById(R.id.editTextReport);
        comment = editText.getText().toString();

        submitButton = findViewById(R.id.submit_button_report);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("send to backend");

                finish();
            }
        });

        cancelButton = findViewById(R.id.cancel_button_report);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_user:
                if (checked) {
                    reportUser = true;
                }
                else {
                    reportUser = false;
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}