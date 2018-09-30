package com.camerrow.camerrowproject;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter{

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                PersonalFragment personalFragment = new PersonalFragment();
                return personalFragment;

            case 1:
                FriendsFragment friendsFragment  = new FriendsFragment();
                return friendsFragment;

//            case 2:
//                PlacesFragment placesFragment = new PlacesFragment();
//                return placesFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Personal";
            case 1:
                return "Friends";
//            case 2:
//                return "Places";
            default:
                return null;
        }
    }
}
