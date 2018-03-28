package com.example.toeyf.hana;

import android.annotation.TargetApi;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendRequestButtons;
    private Button declineFriendRequestButton;
    private TextView profileName;
    private TextView profileStatus;
    private ImageView profileImage;
    private DatabaseReference usersReference;

    private String CURRENT_STATE;
    private DatabaseReference friendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;

    private DatabaseReference friend2Referance;
    private DatabaseReference notificationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendRequestReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();


        friend2Referance = FirebaseDatabase.getInstance().getReference().child("Friends");
        friend2Referance.keepSynced(true);

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);



        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();



        sendFriendRequestButtons = (Button) findViewById(R.id.send_friend_request_button);
        declineFriendRequestButton = (Button) findViewById(R.id.decline_friend_request_button);
        profileName = (TextView) findViewById(R.id.profile_visit_username);
        profileStatus = (TextView) findViewById(R.id.profile_visit_user_status);
        profileImage = (ImageView) findViewById(R.id.profile_visit_user_image);



        CURRENT_STATE = "not_friends";




        usersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(profileImage);


                friendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                    if(dataSnapshot.hasChild(receiver_user_id))
                                    {
                                        String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                        if(req_type.equals("sent"))
                                        {
                                            CURRENT_STATE = "receiver_sent";
                                            sendFriendRequestButtons.setText("ยกเลิกคำขอเป็นเพื่อน");

                                            declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            declineFriendRequestButton.setEnabled(false);
                                        }

                                        else if(req_type.equals("received"))
                                        {
                                            CURRENT_STATE = "request_received";
                                            sendFriendRequestButtons.setText("ตอบรับคำขอเป็นเพื่อน");

                                            declineFriendRequestButton.setVisibility(View.VISIBLE);
                                            declineFriendRequestButton.setEnabled(true);

                                            declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    declineFriendRequest();
                                                }
                                            });
                                        }
                                    }
                                else
                                {
                                    friend2Referance.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    if(dataSnapshot.hasChild(receiver_user_id))
                                                    {
                                                        CURRENT_STATE = "friends";
                                                        sendFriendRequestButtons.setText("ลบเพื่อน");

                                                        declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                        declineFriendRequestButton.setEnabled(false);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError)
                                                {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        declineFriendRequestButton.setVisibility(View.INVISIBLE);
        declineFriendRequestButton.setEnabled(false);

                if(!sender_user_id.equals(receiver_user_id))
                {
                    sendFriendRequestButtons.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            sendFriendRequestButtons.setEnabled(false);


                            if(CURRENT_STATE.equals("not_friends"))
                            {
                                sendFriendRequestToAPerson();
                            }

                            if(CURRENT_STATE.equals("request_sent"))
                            {
                                CancelFriendRequest();
                            }

                            if(CURRENT_STATE.equals("request_received"))
                            {
                                AcceptFriendRequest();
                            }

                            if(CURRENT_STATE.equals("friends"))
                            {
                                UnfriendaFriend();
                            }


                        }
                    });

                }
                else
                    {
                    sendFriendRequestButtons.setVisibility(View.INVISIBLE);
                    declineFriendRequestButton.setVisibility(View.INVISIBLE);
                    }
    }

    private void declineFriendRequest()
    {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendFriendRequestButtons.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestButtons.setText("ส่งคำขอเป็นเพื่อน");

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);

                                                Toast.makeText(ProfileActivity.this, "ยกเลิกคำขอเป็นเพื่อนแล้ว",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void UnfriendaFriend()
    {
        friend2Referance.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friend2Referance.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendFriendRequestButtons.setEnabled(true);
                                                CURRENT_STATE = "not_Friends";
                                                sendFriendRequestButtons.setText("ส่งคำขอเป็นเพื่อน");

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);

                                                Toast.makeText(ProfileActivity.this, "ลบเพื่อนเรียบร้อยแล้ว",
                                                                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calForDATE = Calendar.getInstance();
        android.icu.text.SimpleDateFormat currentDate = new android.icu.text
                .SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDATE.getTime());


        friend2Referance.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        friend2Referance.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                sendFriendRequestButtons.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendRequestButtons.setText("ลบเพื่อน");

                                                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                declineFriendRequestButton.setEnabled(false);

                                                                                Toast.makeText(ProfileActivity.this, "เพิ่มเพื่อนเรียบร้อยแล้ว",
                                                                                        Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


    private void CancelFriendRequest()
    {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                       if(task.isSuccessful())
                       {
                           friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task)
                                       {
                                            if(task.isSuccessful())
                                            {
                                                sendFriendRequestButtons.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestButtons.setText("ส่งคำขอเป็นเพื่อน");

                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);

                                                Toast.makeText(ProfileActivity.this, "ยกเลิกคำขอเป็นเพื่อนแล้ว",
                                                                                                    Toast.LENGTH_SHORT).show();
                                            }
                                       }
                                   });
                       }
                    }
                });
    }


    private void sendFriendRequestToAPerson()
    {
        friendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            friendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                           if(task.isSuccessful())
                                           {
                                               HashMap<String, String> noficationData = new HashMap<String, String>();
                                               noficationData.put("from", sender_user_id);
                                               noficationData.put("type", "request");

                                               notificationReference.child(receiver_user_id).push().setValue(noficationData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                sendFriendRequestButtons.setEnabled(true);
                                                                CURRENT_STATE = "request_sent";
                                                                sendFriendRequestButtons.setText("ยกเลิกคำขอเป็นเพื่อน");

                                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                declineFriendRequestButton.setEnabled(false);
                                                            }
                                                        }
                                                    });

                                               Toast.makeText(ProfileActivity.this, "ส่งคำขอเป็นเพื่อนไปแล้ว",
                                                                                                Toast.LENGTH_SHORT).show();
                                           }
                                        }
                                    });
                        }
                    }
                });
    }
}
