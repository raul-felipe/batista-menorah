package raulalmeida.batistamenorah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by raulfelipealmeida on 28/02/2018.
 */

public class FragmentArquivos extends Fragment {

    ArrayList<String> listaArquivos = new ArrayList<>();
    RecyclerView recyclerView;
    DatabaseReference refArquivos = FirebaseDatabase.getInstance().getReference().child("arquivos");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_arquivo, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        carregaListaArquivos();

        return view;
    }

    void carregaListaArquivos(){
        final ProgressDialog carregando = ProgressDialog.show(getContext(), "","Carregando...", true);
        refArquivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    listaArquivos.add(data.getValue().toString());
                }
                setRecyclerView();
                carregando.cancel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setRecyclerView(){
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        AdapterEnquete adapter = new AdapterEnquete(listaArquivos, getContext());

        recyclerView.setAdapter(adapter);
    }

}

class AdapterEnquete extends RecyclerView.Adapter<AdapterEnquete.MyViewHolder>{

    ArrayList<String> listaArquivos = new ArrayList<>();
    Context context;
    StorageReference refArquivos= FirebaseStorage.getInstance().getReference().child("arquivos");

    AdapterEnquete(ArrayList<String> listaArquivos, Context context){
        this.listaArquivos=listaArquivos;
        this.context=context;
    }

    ViewGroup parent;

    @Override
    public AdapterEnquete.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.arquivo_adapter, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        this.parent=parent;
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterEnquete.MyViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        holder.nomeArquivo.setText(listaArquivos.get(position));

        final StorageReference ref = refArquivos.child(listaArquivos.get(position));
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), listaArquivos.get(position));

        if(file.exists())
            holder.porcentagem.setText("100%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(file.exists())
                    Toast.makeText(context, "Arquivo armazenado na pasta Downloads", Toast.LENGTH_LONG).show();
                else
                    baixaArquivo(ref,file, holder);


            }
        });
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                holder.sizeArquivo.setText(storageMetadata.getSizeBytes()/1000+"kbytes");
            }
        });



    }

    @Override
    public int getItemCount() {
        return listaArquivos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nomeArquivo;
        TextView porcentagem;
        TextView sizeArquivo;
        MyViewHolder(View view){
            super(view);
            nomeArquivo = (TextView)view.findViewById(R.id.nomeArquivo);
            porcentagem = (TextView)view.findViewById(R.id.downloadPorcentagem);
            sizeArquivo = (TextView)view.findViewById(R.id.sizeArquivo);
        }
    }

    void baixaArquivo(StorageReference storageReference, File file, final AdapterEnquete.MyViewHolder holder){
        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context , "Download concluído. Arquivo salvo no repositório DOWNLOADS", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                long porcentagem = taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount();
                holder.porcentagem.setText(porcentagem+"%");
            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context , "falha...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
