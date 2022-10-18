package org.techtown.capston;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener;

public class LoginActivity2 extends AppCompatActivity {
    /*
    login.xml에 있는 탭 레이아웃과 뷰 페이저
    float v = 1 은 value의 v임 그냥 1 넣어도 돼
    아 참고로 TabLayout은 로그인 회원가입 탭해서 넘어가는 메뉴바 같이 생긴거 말하는거야
    *  */

    TabLayout tabLayout;
    ViewPager viewPager;
    float v = 1;

    /*
    *  tabLayout.setTabGravity(TabLayout.GRAVITY_FILL) : 탭레이아웃 크기와 너비 꽉 채우기
    *  tabLayout.setupWithViewPager(viewPager) : 탭레이아웃을 ViewPager에 연결
    *  final LoginAdapter adapter = new LoginAdapter ~ : 탭의 각각 화면들이 프래그먼트로 이루어져있는데 ViewPager 안에 프래그먼트가 들어가 있는 형태거든 ? 어댑터로 연결해야
    ViewPager에서 어떤 화면이 보여지는지 설정?이라고 해야하나 하튼 되는거임
    *  tabLayout.setTranslationY(50) : 탭 위치 (0,50) 이거 내가 자체적으로 값 하나하나 넣어보면서 맞춘거~ 변경 X
    * 이름 알아보기 쉽게 상수로 바꿀거면 바꾸던가 ㅇㅇ
    *
    *  tabLayout.setAlpha(v): 탭레이아웃 투명도 조절값 1로 조정
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTranslationY(50);
        tabLayout.setAlpha(v);
    }
}
