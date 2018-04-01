package com.example.toeyf.hana;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by toeyf on 3/17/2018.
 */

class TabsPagerAdapter extends FragmentPagerAdapter
{
    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch  (position)
        {
            case 0:
                FriendFragment friendFragment = new FriendFragment();
                return  friendFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    public CharSequence gerPageTitel(int position)
    {
        switch (position)
        {
            case 0:
                return  "เพื่อน";

            case 1:
                return  "ข้อความ";

            case 2:
                return  "คำร้องขอ";

            default:
                return null;

        }
    }
}
