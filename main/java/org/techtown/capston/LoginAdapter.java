package org.techtown.capston;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LoginAdapter extends FragmentPagerAdapter {
   /* tabsTitle : string 배열로 Tab에 보여질 문구 넣어놨음 */

    private Context context;
    private String[] tabsTitle = {"로그인","회원가입"};
    int totalTabs;

    /*
    *
    *
    * */

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitle[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignUpTabFragment signUpTabFragment = new SignUpTabFragment();
                return signUpTabFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsTitle.length;
    }
}
