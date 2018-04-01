package com.example.toeyf.hana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button new_account2;
    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText loginEmail;
    private EditText loginPassword;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");


        new_account2 = (Button) findViewById(R.id.new_account2);

        new_account2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent to_startPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(to_startPage);
            }
        });

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.login_Toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("เข้าสู่ระบบ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginButton = (Button) findViewById(R.id.login_button);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                loginUserPassword(email, password);
            }
        });

    }

    private void loginUserPassword(String email, String password)
    {


        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,
                    "กรุณากรอกอีเมลล์", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,
                    "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
        }


        else
        {
            loadingBar.setMessage("รอสักครู่ กำลังเข้าสู่ระบบ");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String online_user_id = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                usersReference.child(online_user_id).child("device_token").setValue(deviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();

                                                // เป็น Consider these two snippets
                                            }
                                        });

                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,
                                        "เกิดความผิดพลาด กรุณาตรวจสอบอีเมลล์หรือรหัสผ่านให้ถูกต้อง", Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
