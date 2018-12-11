package raulalmeida.batistamenorah;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentEstudo extends Fragment {

    ExpandableListView expandableListView;

    ArrayList<String> titulo = new ArrayList<>();
    ArrayList<String> estudo = new ArrayList<>();

    DatabaseReference refEstudo = FirebaseDatabase.getInstance().getReference().child("estudos");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_estudo, container, false);

        expandableListView = (ExpandableListView) view.findViewById(R.id.lista);

        recebeEstudos();

        return view;
    }

    void recebeEstudos(){

        final ProgressDialog carregando = ProgressDialog.show(getContext(), "","Carregando...", true);

        refEstudo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.clear();
                estudo.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    titulo.add(data.child("titulo").getValue().toString());
                    estudo.add(data.child("conteudo").getValue().toString());
                }
                Collections.reverse(titulo);
                Collections.reverse(estudo);

                carregando.cancel();
                expandableListView.setAdapter(new ExpandableList(getContext(),estudo, titulo, 14 ));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

class ExpandableList extends BaseExpandableListAdapter{

    Context context;
    List<String> titulo;
    List<String> estudo;
    float size;

    ExpandableList(Context context, List<String> estudo, List<String> titulo, float size){
        this.context = context;
        this.estudo=estudo;
        this.titulo=titulo;
        this.size=size;
    }

    @Override
    public int getGroupCount() {
        return titulo.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titulo.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return estudo.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.titulo_layout, null);
        }

        TextView tituloText = (TextView) convertView.findViewById(R.id.titulo);
        tituloText.setText(titulo.get(groupPosition));



        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.conteudo_layout, null);
        }

        FloatingActionButton btnMais = (FloatingActionButton ) convertView.findViewById(R.id.btnBigger);
        FloatingActionButton  btnMenos = (FloatingActionButton ) convertView.findViewById(R.id.btnLower);

        final TextView tituloText = (TextView) convertView.findViewById(R.id.conteudo);

        tituloText.setText(estudo.get(groupPosition));

        btnMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size = size+4;
                tituloText.setTextSize(size);
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size = size-4;
                tituloText.setTextSize(size);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }




}
