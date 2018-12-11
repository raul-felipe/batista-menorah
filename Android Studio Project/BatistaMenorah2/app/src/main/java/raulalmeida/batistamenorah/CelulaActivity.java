package raulalmeida.batistamenorah;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by raulfelipealmeida on 04/04/2018.
 */

public class CelulaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celula);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_celula);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentCelula not = new FragmentCelula();
        fragmentTransaction.replace(R.id.frame, not);
        fragmentTransaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_celula);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.celula) {
            FragmentCelula not = new FragmentCelula();
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        } /*else if (id == R.id.formulario) {
            FragmentNoticia not = new FragmentNoticia();
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_celula);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
