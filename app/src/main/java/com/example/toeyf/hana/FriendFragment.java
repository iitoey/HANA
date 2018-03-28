package com.example.toeyf.hana;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment
{

    private RecyclerView myFriendList;
    private DatabaseReference friendReference;
    private DatabaseReference usersReferenc;
    private FirebaseAuth mAuth;

    private View myMainView;
    String online_user_id;


    View v;
    private Toolbar mToolbar;
    public FriendFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_friend, container, false);

        myFriendList = (RecyclerView) myMainView.findViewById(R.id.friend_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        usersReferenc=FirebaseDatabase.getInstance().getReference().child("Users");
        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));


        return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, friendViewHolder> firebaseRecyclerAdapter
            = new FirebaseRecyclerAdapter<Friends, friendViewHolder>
                (
                        Friends.class,
                        R.layout.all_users_display_layout,
                        friendViewHolder.class,
                        friendReference
                )
        {
            @Override
            protected void populateViewHolder(friendViewHolder viewHolder, Friends model, int position)
            {
                viewHolder.setDate(model.getDate());

                String list_user_id = getRef(position).getKey();

                usersReferenc.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String username = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                        friendViewHolder.setUserName(username);
                        friendViewHolder.setThumbImage(thumbImage, getContext());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        };

        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class friendViewHolder extends RecyclerView.ViewHolder
    {
        static View mView;

        public friendViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date)
        {
            TextView sinceFriendDate = (TextView) mView.findViewById(R.id.all_user_status);
            sinceFriendDate.setText(date);
        }

        public void  setUserame (String userame)
        {
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userame);
        }

        public static void setUserName(String username)
        {
        }

        public static void setThumbImage(final String thumbImage, final Context ctx)
        {
            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);

            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_profile).into(thumb_image);
                        }
                    });
        }
    }
}
