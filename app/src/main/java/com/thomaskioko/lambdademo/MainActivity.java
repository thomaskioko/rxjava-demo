package com.thomaskioko.lambdademo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.btnClick)
    Button mButtonClick;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);


        mFloatingActionButton.setOnClickListener(view -> Snackbar.make(view, "Lambdas Rock", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        //Method reference
        mButtonClick.setOnClickListener(this::showSnackBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method used to display a snackbar.
     *
     * @param view {@link View}
     */
    private void showSnackBar(View view) {
        Snackbar.make(view, "Method Reference", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
