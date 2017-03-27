package io.wings.gabriel.tapestry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.utils.PrefManager;
import io.wings.gabriel.tapestry.utils.TypefaceSpan;

public class Setting extends AppCompatActivity {

    TextInputLayout textInputLayout;
    EditText editText;
    PrefManager pref;
    Button save , clear;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString s = new SpannableString("Settings");
        s.setSpan(new TypefaceSpan(this, "AvenirLTStd-Book.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_setting);

        textInputLayout = (TextInputLayout) findViewById(R.id.grid_coloumns);
        editText = (EditText) findViewById(R.id.grid_coloumns_text);
        save = (Button) findViewById(R.id.save);
        clear = (Button) findViewById(R.id.Clear_cache);

        pref = new PrefManager(getBaseContext());

        editText.setText(String.valueOf(pref.getNoOfGridColumns()));



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String no_of_col = editText.getText().toString().trim();

                if(editText.getText().toString().trim().length()==0|| !isInteger(editText.getText().toString().trim())){

                  /*  Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_enter_valid_grid_columns),
                            Toast.LENGTH_LONG).show();*/

                    Snackbar  snackbar = Snackbar
                            .make(coordinatorLayout, getString(R.string.toast_enter_valid_grid_columns), Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                    return;

                }



                if(!no_of_col.equalsIgnoreCase(String.valueOf(pref.getNoOfGridColumns()))){

                    pref.setNoOfGridColumns(Integer.parseInt(no_of_col));

                    Intent i = new Intent(Setting.this,
                            SplashActivity.class);
                    // Clear all the previous activities
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }else {

                    onBackPressed();
                }

            }
        });


        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppController.getInstance().clearCache();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isInteger(String input) {
        try {
            int num =  Integer.parseInt(input);
            if(num >=2){
                return true;
            }else {
                return false;
            }



        } catch (Exception e) {
            return false;
        }
    }




    }


