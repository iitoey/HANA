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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;
    private ProgressDialog loadingBar;
    private android.support.v7.widget.Toolbar mToolbar;
    private EditText RegisterUserName;
    private EditText RegisterUserEmail;
    private EditText RegisterUserPassword;
    private Button CreatteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ลงทะเบียนเข้าสู่ระบบ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ลูกศรย้อนกลับ

        RegisterUserName = (EditText) findViewById(R.id.register_name);
        RegisterUserEmail = (EditText) findViewById(R.id.register_email);
        RegisterUserPassword = (EditText) findViewById(R.id.register_password);
        CreatteAccountButton = (Button) findViewById(R.id.create_account_button);
        loadingBar = new ProgressDialog(this);

        CreatteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String name = RegisterUserName.getText().toString();
                String email = RegisterUserEmail.getText().toString();
                String password = RegisterUserPassword.getText().toString();

                RegisterAccount(name, email, password);
            }
        });

    }

    private void RegisterAccount(final String name, String email, String password)
    {
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this, "กรุณากรอกชื่อของคุณ",
                                                            Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this, "กรุณากรอกอีเมลล์ของคุณ",
                                                            Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this, "กรุณากรอกรหัสผ่าน",
                                                            Toast.LENGTH_LONG).show();
        }

        else
        {
            loadingBar.setMessage("กำลังทำการสร้างปัญชีใหม่ของคุณ");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    //Token เป็นรหัสชุดหนึ่ง ขั้น session layer ไว้ระบุตัวตนนๆว่าคือใคร เอามาใช้ในการทำ RESTful API
                                    //จะถูกส่งไปทุก request ผ่าน HTTP Headers
                                    //จะถูกส่งไปทุก request ผ่าน HTTP Headers

                                String current_user_id = mAuth.getCurrentUser().getUid();
                                storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                                storeUserDefaultDataReference.child("user_name").setValue(name);
                                storeUserDefaultDataReference.child("user_status").setValue("ยินดีต้อนรับเข้าสู่ HANACHAT!");
                                storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                                storeUserDefaultDataReference.child("device_token").setValue(deviceToken);
                                storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")


                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();

                                                //เป็น Consider these two snippets
                                            }
                                        }
                                    });

                            }
                            else
                                {
                                    Toast.makeText(RegisterActivity.this,"เกิดความผิดพลาด กรุณาลองใหม่อีกครั้ง",
                                                                                        Toast.LENGTH_SHORT).show();
                                }

                                loadingBar.dismiss();
                        }
                    });
        }
    }
}
