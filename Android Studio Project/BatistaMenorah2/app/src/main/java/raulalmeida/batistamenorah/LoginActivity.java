package raulalmeida.batistamenorah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by raulfelipealmeida on 28/03/2018.
 */

public class LoginActivity extends AppCompatActivity {

    EditText emailCadastrarField;
    EditText nomeField;
    EditText emailEntrarField;
    EditText codigoField;

    Button cadastrarBtn;
    Button entrarBtn;

    RadioButton radioSim;

    Button pularCadastroBtn;

    String FAKE_PASSWORD= "rfa2506";

    DatabaseReference usuarioReference = FirebaseDatabase.getInstance().getReference().child("usuarios");

    FirebaseAuth loginFirebase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailCadastrarField = (EditText) findViewById(R.id.email_cadastrar_field);
        emailEntrarField = (EditText) findViewById(R.id.email_entrar_field);
        nomeField = (EditText) findViewById(R.id.nomeEdt);
        codigoField = (EditText) findViewById(R.id.codigoEdt);

        cadastrarBtn = (Button) findViewById(R.id.cadastrarBtn);
        entrarBtn = (Button) findViewById(R.id.entrarBtn);

        radioSim = findViewById(R.id.radioSim);


        pularCadastroBtn = (Button) findViewById(R.id.pular_cadastro_btn);

        loginFirebase = FirebaseAuth.getInstance();

        boolean membro = false;

        if(radioSim.isChecked())
            membro = true;

        final boolean membroFinal = membro;

        cadastrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar(emailCadastrarField.getText().toString(), nomeField.getText().toString(), codigoField.getText().toString(), membroFinal);
            }

        });

        entrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrar(emailEntrarField.getText().toString(),codigoField.getText().toString());
            }
        });

        pularCadastroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("arquivo", Context.MODE_PRIVATE).edit();
                editor.putString("email", ""); //preenche o email com uma string vazia só para a tela de login não aparecer toda hora
                editor.apply();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    void poePrefsEmailCodigo(final String email, final String codigo, DatabaseReference reference){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences.Editor editor = getSharedPreferences("arquivo", Context.MODE_PRIVATE).edit();
                editor.putString("email", email);
                editor.putString("nome", dataSnapshot.getValue().toString());
                editor.putString("codigo", codigo);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void poePrefsEmailNomeCodigo(String email, String nome, String codigo, boolean membro){
        SharedPreferences.Editor editor = getSharedPreferences("arquivo", Context.MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("nome", nome);
        editor.putString("codigo", codigo);
        editor.putBoolean("membro", membro);
        editor.apply();
    }

    void preencheEmailCodigoFirebase(String email, String codigo, String UID){
        usuarioReference.child(UID).child("email").setValue(email);
        usuarioReference.child(UID).child("codigo").setValue(codigo);
    }

    void preencheEmailNomeCodigoFirebase(String email,String nome, String codigo, String UID,boolean membro){
        usuarioReference.child(UID).child("email").setValue(email);
        usuarioReference.child(UID).child("nome").setValue(nome);
        usuarioReference.child(UID).child("codigo").setValue(codigo);
        usuarioReference.child(UID).child("membro").setValue(membro);
    }

    void cadastrar(final String email, final String nome, final String codigo, final boolean membro){
        loginFirebase.createUserWithEmailAndPassword(email, FAKE_PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = loginFirebase.getCurrentUser();
                            user.getUid();
                            preencheEmailNomeCodigoFirebase(email, nome, codigo, user.getUid(), membro);
                            poePrefsEmailNomeCodigo(email, nome, codigo, membro);
                            Toast.makeText(LoginActivity.this, "Sucesso",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Falha no Cadastro",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void entrar(final String email, final String codigo){
        loginFirebase.signInWithEmailAndPassword(email, FAKE_PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = loginFirebase.getCurrentUser();
                            preencheEmailCodigoFirebase(email, codigo, user.getUid());
                            poePrefsEmailCodigo(email, codigo, usuarioReference.child(user.getUid()).child("nome"));
                            Toast.makeText(LoginActivity.this, "Sucesso",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Falha ao logar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
