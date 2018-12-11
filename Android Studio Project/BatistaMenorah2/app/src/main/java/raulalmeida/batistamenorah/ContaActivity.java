package raulalmeida.batistamenorah;

import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by raulfelipealmeida on 23/10/2018.
 */

public class ContaActivity extends AppCompatActivity {

    EditText emailField;
    EditText nameField;
    EditText codeField;

    RadioGroup radioMember;
    RadioButton radioTrue;
    RadioButton radioFalse;

    Button updateBtn;

    FirebaseAuth loginFirebase;

    String FAKE_PASSWORD= "rfa2506";

    boolean membro;
    String email;
    String nome;
    String codigo;

    DatabaseReference usuarioReference = FirebaseDatabase.getInstance().getReference().child("usuarios");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        emailField = findViewById(R.id.email_conta_field);
        nameField = findViewById(R.id.nome_conta_field);
        codeField = findViewById(R.id.codigo_conta_field);

        radioMember = findViewById(R.id.radio_membro);
        radioTrue = findViewById(R.id.radioTrue);
        radioFalse = findViewById(R.id.radioFalse);

        updateBtn = findViewById(R.id.atualizar_btn);

        loginFirebase = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences("arquivo", MODE_PRIVATE); // CARREGA O VALOR DE USUARIO
        email = prefs.getString("email", null);
        nome = prefs.getString("nome", null);
        codigo = prefs.getString("codigo", null);
        membro = prefs.getBoolean("membro",false);

        emailField.setText(email);
        nameField.setText(nome);
        codeField.setText(codigo);

        radioFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membro = false;
            }
        });
        radioTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membro = true;
            }
        });

        radioTrue.setChecked(membro);
        radioFalse.setChecked(!membro);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("arquivo", Context.MODE_PRIVATE).edit();
                email = emailField.getText().toString();
                nome = nameField.getText().toString();
                codigo = codeField.getText().toString();
                editor.putString("email", email);
                editor.putString("nome", nome);
                editor.putString("codigo", codigo);
                editor.putBoolean("membro", membro);
                editor.apply();

                loginFirebase.signInWithEmailAndPassword(email, FAKE_PASSWORD).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("nome").setValue(nome);
                        usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("email").setValue(email);
                        usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("codigo").setValue(codigo);
                        usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("membro").setValue(membro);
                        Toast.makeText(getApplicationContext(), "Dados atualizados com sucesso no sistema.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginFirebase.createUserWithEmailAndPassword(email, FAKE_PASSWORD).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("nome").setValue(nome);
                                usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("email").setValue(email);
                                usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("codigo").setValue(codigo);
                                usuarioReference.child(loginFirebase.getCurrentUser().getUid()).child("membro").setValue(membro);
                                Toast.makeText(getApplicationContext(), "Dados armazenados com sucesso no sistema.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
        });


    }
}
