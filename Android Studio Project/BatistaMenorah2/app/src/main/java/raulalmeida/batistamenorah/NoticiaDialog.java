package raulalmeida.batistamenorah;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by raulfelipealmeida on 25/10/2018.
 */

public class NoticiaDialog extends DialogFragment {

    DatabaseReference refNoticias = FirebaseDatabase.getInstance().getReference().child("noticias");

    static int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.noticia_dialog, container, false);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.noticia_dialog, null);

        final TextView imgViewTitle = view.findViewById(R.id.titulo);
        final TextView contentText = view.findViewById(R.id.conteudo);

        final ProgressDialog carregando = ProgressDialog.show(getContext(), "","Carregando...", true);

        refNoticias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgViewTitle.setText(dataSnapshot.child(String.valueOf(id)).child("titulo").getValue().toString());
                contentText.setText(dataSnapshot.child(String.valueOf(id)).child("conteudo").getValue().toString());
                carregando.cancel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view).setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
