package com.pronuxtech.slynk;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity {

    //WIDGETS
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    //CONSTANT VALUES
    private static String sendingSigningValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

//        ACTION BAR HIDE
        getSupportActionBar().hide();


        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


//        ====================================================== CASTINGS END ======================================================


        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

//        GETTING TAB POSITION AND ACCEPTING VALUE FROM PREVIOUS ACTIVITY
        sendingSigningValue = getIntent().getStringExtra("SENDING_SIGNIN_VALUE");
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab.getPosition() == 1)

//            SIGN IN TAB OPEN FROM GETTING PREVIOUS VALUE
            if (sendingSigningValue != null) {

                Log.d("tabSignIn", "SignIn Tab Open");
                tab.select();
            }

    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new signup_part_1(), "Sign up");
        adapter.addFragment(new signin(), "Sign in");

        viewPager.setAdapter(adapter);
    }

    class viewPagerAdapter extends FragmentPagerAdapter {

        TabLayout.Tab tab = mTabLayout.getTabAt(1);

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentTitleList.size();

        }

        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }


}
