package sa.edu.uhb.uhbcommunity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import sa.edu.uhb.uhbcommunity.MainActivity;
import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.ResetPassActivity;
import sa.edu.uhb.uhbcommunity.RulesActivity;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rg_language;
    private RadioButton rb_english, rb_arabic;
    private TextView change_pass, terms;

    public String selectedLanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocale();
        setContentView(R.layout.activity_settings);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            finish();
        }

        // For the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rg_language = findViewById(R.id.rg_language);
        rb_english = findViewById(R.id.rb_english);
        rb_arabic = findViewById(R.id.rb_arabic);
        change_pass = findViewById(R.id.change_pass);
        terms = findViewById(R.id.terms);

        // To set the selected language
        SharedPreferences preferences = getSharedPreferences("Language", Activity.MODE_PRIVATE);
        selectedLanguage = preferences.getString("language","");
        if (selectedLanguage.equals("en")) {
            rb_english.setChecked(true);
        }
        else if (selectedLanguage.equals("ar")) {
            rb_arabic.setChecked(true);
        }
        else {
            if(Locale.getDefault().getLanguage() == "en") {
                rb_english.setChecked(true);
            }
            else {
                rb_arabic.setChecked(true);
            }
        }

        // When the user select the language
        rg_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {

                    case R.id.rb_english:
                        setLocale("en");
                        Intent refresh1 = new Intent(SettingsActivity.this, SettingsActivity.class);
                        startActivity(refresh1);
                        finish();
                        break;

                    case R.id.rb_arabic:
                        setLocale("ar");
                        Intent refresh2 = new Intent(SettingsActivity.this, SettingsActivity.class);
                        startActivity(refresh2);
                        finish();
                        break;
                }
            }
        });

        // When click on change password button, he will be redirected to the reset password page
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ResetPassActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        // When click on terms of use button, he will be redirected to the rules page
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, RulesActivity.class);
                startActivity(intent);
            }
        });
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
        setLocale(language);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}