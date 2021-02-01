package sa.edu.uhb.uhbcommunity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import sa.edu.uhb.uhbcommunity.Fragments.HomeFragment;
import sa.edu.uhb.uhbcommunity.Fragments.NotificationFragment;
import sa.edu.uhb.uhbcommunity.Fragments.ProfileFragment;
import sa.edu.uhb.uhbcommunity.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public ChipNavigationBar bottomNavigation;
    private Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        Intent intent = new Intent(MainActivity.this,AddPostActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_notification:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if(selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }

            }
        });

        // The default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }
}