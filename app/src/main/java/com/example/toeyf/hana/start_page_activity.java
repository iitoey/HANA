package com.example.toeyf.hana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class start_page_activity extends AppCompatActivity {

    private CardView already_account;
    private CardView new_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page_activity);

        new_account = (CardView) findViewById(R.id.new_account);
        already_account = (CardView) findViewById(R.id.already_account);


        new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(start_page_activity.this, RegisterActivity.class);
               startActivity(registerIntent);
            }
        });

        already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent loginIntent = new Intent(start_page_activity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
