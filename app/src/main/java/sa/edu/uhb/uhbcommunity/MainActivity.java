package sa.edu.uhb.uhbcommunity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Locale;

import sa.edu.uhb.uhbcommunity.Fragments.HomeFragment;
import sa.edu.uhb.uhbcommunity.Fragments.NotificationFragment;
import sa.edu.uhb.uhbcommunity.Fragments.ProfileFragment;
import sa.edu.uhb.uhbcommunity.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static ChipNavigationBar bottomNavigation;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocale();
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            finish();
        }

        /* if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        } */

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // The default selected item in the menu
        bottomNavigation.setItemSelected(R.id.nav_home,true);

        // To view the content of the selected item from the bottom navigation bar
        bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch (id) {

                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        getBaseContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().commit();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        Intent intent = new Intent(MainActivity.this,AddPostActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.nav_notification:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        getBaseContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().commit();
                        break;
                }

                if(selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }

            }
        });

        /* This data is received from comment adapter
         And it will be sent to the profile fragment */
        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            String userId = intent.getString("userId");
            bottomNavigation.setItemSelected(R.id.nav_profile,true);
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("userId",userId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
        else {
            // The default fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Language",MODE_PRIVATE).edit();
        editor.putString("language",language);
        editor.apply();
    }

    public void getLocale() {
        SharedPreferences preferences = getSharedPreferences("Language", Activity.MODE_PRIVATE);
        String language = preferences.getString("language","");
        if(language.equals("en") || language.equals("ar")) {
            setLocale(language);
        }
        else {
            setLocale(Locale.getDefault().getLanguage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

        /* Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit me", true);
        startActivity(intent);
        finish(); */
    }
}