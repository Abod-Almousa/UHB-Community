package sa.edu.uhb.uhbcommunity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import sa.edu.uhb.uhbcommunity.LoginActivity;
import sa.edu.uhb.uhbcommunity.R;
import sa.edu.uhb.uhbcommunity.ResetPassActivity;

public class LinksActivity extends AppCompatActivity {

    private TextView link1, link2, link3, link4, link5, link6, link7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        // For the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        link1 = findViewById(R.id.link1);
        link2 = findViewById(R.id.link2);
        link3 = findViewById(R.id.link3);
        link4 = findViewById(R.id.link4);
        link5 = findViewById(R.id.link5);
        link6 = findViewById(R.id.link6);
        link7 = findViewById(R.id.link7);

        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link1 = new Intent(Intent.ACTION_VIEW);
                link1.setData(Uri.parse("https://twitter.com/UoHB_Official?s=08"));
                if(link1.resolveActivity(getPackageManager()) != null) {
                    startActivity(link1);
                }
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link2 = new Intent(Intent.ACTION_VIEW);
                link2.setData(Uri.parse("https://twitter.com/REG_UOHB?s=08"));
                if(link2.resolveActivity(getPackageManager()) != null) {
                    startActivity(link2);
                }
            }
        });

        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link3 = new Intent(Intent.ACTION_VIEW);
                link3.setData(Uri.parse("https://twitter.com/dsa_uhb?s=08"));
                if(link3.resolveActivity(getPackageManager()) != null) {
                    startActivity(link3);
                }
            }
        });

        link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link4 = new Intent(Intent.ACTION_VIEW);
                link4.setData(Uri.parse("https://twitter.com/edhaah4?s=08"));
                if(link4.resolveActivity(getPackageManager()) != null) {
                    startActivity(link4);
                }
            }
        });

        link5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link5 = new Intent(Intent.ACTION_VIEW);
                link5.setData(Uri.parse("https://www.uhb.edu.sa/ar/Pages/default.aspx"));
                if(link5.resolveActivity(getPackageManager()) != null) {
                    startActivity(link5);
                }
            }
        });

        link6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link6 = new Intent(Intent.ACTION_VIEW);
                link6.setData(Uri.parse("http://sis.uhb.edu.sa/psp/hcs9prd/?&cmd=login&languageCd=ARA&"));
                if(link6.resolveActivity(getPackageManager()) != null) {
                    startActivity(link6);
                }
            }
        });

        link7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link7 = new Intent(Intent.ACTION_VIEW);
                link7.setData(Uri.parse("https://lms.uhb.edu.sa/"));
                if(link7.resolveActivity(getPackageManager()) != null) {
                    startActivity(link7);
                }
            }
        });
    }
}