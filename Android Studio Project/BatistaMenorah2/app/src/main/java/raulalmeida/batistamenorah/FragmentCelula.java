package raulalmeida.batistamenorah;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by raulfelipealmeida on 03/11/2018.
 */

public class FragmentCelula extends Fragment{

    DatabaseReference refCelulas = FirebaseDatabase.getInstance().getReference().child("celulas");
    ArrayList<String> listaNomeCelula = new ArrayList<>();
    ArrayList<Integer> listaIdCelula = new ArrayList<>();
    Spinner celulaSpinner;

    EditText qtdMembrosField;
    EditText qtdDiscipuladoresField;
    EditText qtdDiscipulosField;
    EditText enderecoField;

    Button atualizarButton;
    
    int pos=-1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_celula, container, false);

        celulaSpinner = (Spinner) view.findViewById(R.id.celulas_spinner);

        qtdMembrosField = (EditText) view.findViewById(R.id.qtd_membro_field);
        qtdDiscipuladoresField= (EditText) view.findViewById(R.id.qtd_discipuladores_field);
        qtdDiscipulosField= (EditText) view.findViewById(R.id.qtd_discipulos_field);
        enderecoField= (EditText) view.findViewById(R.id.endereco_field);
        atualizarButton = (Button) view.findViewById(R.id.atualizar_btn);

        atualizarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos>=0) {
                    refCelulas.child(String.valueOf(pos)).child("qtdDiscipuladores").setValue(qtdDiscipuladoresField.getText().toString());
                    refCelulas.child(String.valueOf(pos)).child("qtdDiscipulos").setValue(qtdDiscipulosField.getText().toString());
                    refCelulas.child(String.valueOf(pos)).child("qtdMembros").setValue(qtdMembrosField.getText().toString());
                    refCelulas.child(String.valueOf(pos)).child("endereco").setValue(enderecoField.getText().toString());
                }
            }
        });

        refCelulas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaNomeCelula.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    listaNomeCelula.add(data.child("nome").getValue().toString());
                    listaIdCelula.add(Integer.valueOf(data.getKey().toString()));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaNomeCelula);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                celulaSpinner.setAdapter(adapter);
                if(pos>0){
                    celulaSpinner.setSelection(pos-1);
                }
                listennerSpinner();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    void listennerSpinner(){
        celulaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                qtdMembrosField.setText("");
                qtdDiscipuladoresField.setText("");
                qtdDiscipulosField.setText("");
                enderecoField.setText("");

                pos = listaIdCelula.get(listaNomeCelula.indexOf(parent.getSelectedItem().toString()));


                try {
                    setTextFieldFromFirebase(refCelulas.child(String.valueOf(pos)).child("qtdDiscipuladores"), qtdDiscipuladoresField);
                    setTextFieldFromFirebase(refCelulas.child(String.valueOf(pos)).child("qtdDiscipulos"), qtdDiscipulosField);
                    setTextFieldFromFirebase(refCelulas.child(String.valueOf(pos)).child("qtdMembros"), qtdMembrosField);
                    setTextFieldFromFirebase(refCelulas.child(String.valueOf(pos)).child("endereco"), enderecoField);
                }catch (Exception e){
                    Log.d(e.getMessage(),e.getLocalizedMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void setTextFieldFromFirebase(DatabaseReference data, final EditText field) {
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    field.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
