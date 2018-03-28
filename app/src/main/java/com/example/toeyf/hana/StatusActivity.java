package com.example.toeyf.hana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity
    {
    private android.support.v7.widget.Toolbar mToolbar;
    private CardView saveChangeButton;
    private EditText statusInput;
    private DatabaseReference changeStatusRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("เปลี่ยนสถานะของคุณ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveChangeButton = (CardView) findViewById(R.id.send_friend_request_button);
        statusInput = (EditText) findViewById(R.id.status_input);
        loadingbar = new ProgressDialog(this);

        String old_status = getIntent().getExtras().get("user_status").toString();
        statusInput.setText(old_status);

        saveChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String new_status = statusInput.getText().toString();

                changeProfileStatus(new_status);
            }
        });

    }

        private void changeProfileStatus(String new_status)
        {
            if(TextUtils.isEmpty(new_status))
            {
                Toast.makeText(StatusActivity.this,"กรุณากรอกสถานะของคุณ",
                                                                Toast.LENGTH_SHORT).show();
            }

            else
                {
                    loadingbar.setMessage("รอสักครู่ กำลังทำการเปลี่ยนสถานะ");
                    loadingbar.show();

                    changeStatusRef.child("user_status").setValue(new_status)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                  if(task.isSuccessful())
                                  {
                                      Intent settinsIntent = new Intent(StatusActivity.this, SettingActivity.class);
                                      startActivity(settinsIntent);

                                      Toast.makeText(StatusActivity.this,"ทำการอัพเดทสถานะของคุณเรียบร้อย",
                                                                                        Toast.LENGTH_LONG).show();
                                  }
                                  else
                                      {
                                          Toast.makeText(StatusActivity.this,"เกิดความผิดพลาด กรุณาลองใหม่อีกครั้ง",
                                                                                         Toast.LENGTH_LONG).show();
                                      }
                                }
                            });
                }
        }
    }
