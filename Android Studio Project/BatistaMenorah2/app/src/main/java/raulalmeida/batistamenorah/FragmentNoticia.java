package raulalmeida.batistamenorah;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class FragmentNoticia extends Fragment {

    DatabaseReference refNoticias = FirebaseDatabase.getInstance().getReference().child("noticias");

    ListView noticias;

    ArrayList<String> listaNoticias = new ArrayList<>();
    ArrayList<Integer> listaIdNoticias = new ArrayList<>();


    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_noticia, container, false);

        noticias = (ListView) view.findViewById(R.id.noticias);

        carregaNoticias();

        listennerLista();

        return view;
    }

    void carregaNoticias(){
        final ProgressDialog carregando = ProgressDialog.show(getContext(), "","Carregando...", true);

        refNoticias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaNoticias.clear();
                listaIdNoticias.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    listaNoticias.add(data.child("titulo").getValue().toString());
                    listaIdNoticias.add(Integer.valueOf(data.getKey().toString()));
                }

                carregando.cancel();
                criaLista();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void criaLista(){
        Collections.reverse(listaNoticias);
        Collections.reverse(listaIdNoticias);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, listaNoticias);

        noticias.setAdapter(adapter);
    }

    void listennerLista(){
        noticias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticiaDialog.id = listaIdNoticias.get(position);

                MainActivity.fragmentId = R.layout.noticia_dialog;

                /*FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                NoticiaDialog n = new NoticiaDialog();
                fragmentTransaction.replace(R.id.frame, n);
                fragmentTransaction.commit();*/
                DialogFragment newFragment = new NoticiaDialog();
                newFragment.show(getActivity().getSupportFragmentManager(), "missiles");

            }
        });
    }


}
