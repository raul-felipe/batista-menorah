package raulalmeida.batistamenorah;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String codigo;
    String email;
    boolean membro;

    static int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseMessaging.getInstance().subscribeToTopic("comum");

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentInicio inicio = new FragmentInicio();
        fragmentTransaction.replace(R.id.frame, inicio);
        fragmentTransaction.commit();

        try {
            if (getIntent().getExtras().getBoolean("notif")) {
                DialogFragment newFragment = new NotificationDialog();

                Bundle b = new Bundle();
                b.putString("titulo", getIntent().getExtras().getString("titulo"));
                b.putString("mensagem", getIntent().getExtras().getString("mensagem"));
                newFragment.setArguments(b);
                newFragment.show(getSupportFragmentManager(),"missiles");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        abreLogin();

    }


    void abreLogin(){
        SharedPreferences prefs = getSharedPreferences("arquivo", MODE_PRIVATE); // CARREGA O VALOR DE USUARIO
        email = prefs.getString("email", null);

        if(email==null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SharedPreferences prefs = getSharedPreferences("arquivo", MODE_PRIVATE); // CARREGA O VALOR DE USUARIO
        codigo = prefs.getString("codigo", null);
        membro = prefs.getBoolean("membro", false);

        if(membro)
            FirebaseMessaging.getInstance().subscribeToTopic("membro");
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic("membro");

        if(codigo != null && codigo.equals("7722")) {
            FirebaseMessaging.getInstance().subscribeToTopic("lider");
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic("lider");

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.acessoRestrito:
                Intent i = new Intent(MainActivity.this, CelulaActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (fragmentId){
            case R.id.inicio:
                super.onBackPressed();
                break;
            case R.id.noticias:
                FragmentInicio inicio = new FragmentInicio();
                setTitle("FragmentInicio");
                fragmentTransaction.replace(R.id.frame, inicio);
                fragmentTransaction.commit();
                fragmentId = R.id.inicio;
                break;
            case R.id.audios:
                inicio = new FragmentInicio();
                setTitle("FragmentInicio");
                fragmentTransaction.replace(R.id.frame, inicio);
                fragmentTransaction.commit();
                fragmentId = R.id.inicio;
                break;
            case R.id.estudos:
                inicio = new FragmentInicio();
                setTitle("FragmentInicio");
                fragmentTransaction.replace(R.id.frame, inicio);
                fragmentTransaction.commit();
                fragmentId = R.id.inicio;
                break;
            case R.layout.noticia_dialog:
                FragmentNoticia noticias = new FragmentNoticia();
                setTitle("Notícias");
                fragmentTransaction.replace(R.id.frame, noticias);
                fragmentTransaction.commit();
                fragmentId = R.id.noticias;
                break;



        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        fragmentId = item.getItemId();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragmentId == R.id.inicio) {
            FragmentInicio not = new FragmentInicio();
            setTitle("Início");
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        } else if (fragmentId == R.id.noticias) {
            FragmentNoticia not = new FragmentNoticia();
            setTitle("Notícias");
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        } else if (fragmentId == R.id.audios) {

            FragmentAudio not = new FragmentAudio();
            setTitle("Audios");
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        } else if (fragmentId == R.id.estudos) {

            FragmentEstudo not = new FragmentEstudo();
            setTitle("Estudos");
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        } else if (fragmentId == R.id.conta) {

            Intent intent = new Intent(MainActivity.this, ContaActivity.class);
            startActivity(intent);


        } /*else if (id == R.id.arquivos) {

            FragmentArquivos not = new FragmentArquivos();
            setTitle("FragmentArquivos");
            fragmentTransaction.replace(R.id.frame, not);
            fragmentTransaction.commit();

        }*/




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
