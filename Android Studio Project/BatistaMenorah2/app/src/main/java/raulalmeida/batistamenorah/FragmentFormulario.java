package raulalmeida.batistamenorah;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by raulfelipealmeida on 09/12/2018.
 */

public class FragmentFormulario extends Fragment{

    DatabaseReference formularioRef = FirebaseDatabase.getInstance().getReference().child("celulas").child("fomularios");
    DatabaseReference estudoRef = FirebaseDatabase.getInstance().getReference().child("estudos");

    Spinner celulaSpinner;
    EditText qtdMembrosField;
    EditText qtdDiscipuladoresField;
    Button enviarBtn;

    String formulario1;
    String formulario2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_celula, container, false);

        celulaSpinner = (Spinner) view.findViewById(R.id.celulas_spinner);

        qtdMembrosField = (EditText) view.findViewById(R.id.qtd_membro_field);
        qtdDiscipuladoresField= (EditText) view.findViewById(R.id.qtd_discipuladores_field);
        enviarBtn = (Button) view.findViewById(R.id.atualizar_btn);

        enviarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    refCelulas.child(String.valueOf(pos)).child("qtdDiscipuladores").setValue(qtdDiscipuladoresField.getText().toString());
                    refCelulas.child(String.valueOf(pos)).child("qtdDiscipulos").setValue(qtdDiscipulosField.getText().toString());
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

    void carregaUltimosFormularios(){
        estudoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
