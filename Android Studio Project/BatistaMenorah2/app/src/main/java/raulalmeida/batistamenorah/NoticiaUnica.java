package raulalmeida.batistamenorah;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by raulfelipealmeida on 27/02/2018.
 */

public class NoticiaUnica extends Fragment {

    DatabaseReference refNoticias = FirebaseDatabase.getInstance().getReference().child("noticias");

    static int id;

    TextView titulo;
    TextView conteudo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.noticia_dialog, container, false);

        titulo = (TextView) view.findViewById(R.id.titulo);
        conteudo = (TextView) view.findViewById(R.id.conteudo);

        final ProgressDialog carregando = ProgressDialog.show(getContext(), "","Carregando...", true);

        refNoticias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.setText(dataSnapshot.child(String.valueOf(id)).child("titulo").getValue().toString());
                conteudo.setText(dataSnapshot.child(String.valueOf(id)).child("conteudo").getValue().toString());
                carregando.cancel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
