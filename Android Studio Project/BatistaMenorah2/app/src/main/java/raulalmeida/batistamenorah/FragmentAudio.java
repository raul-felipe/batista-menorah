package raulalmeida.batistamenorah;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by raulfelipealmeida on 12/04/2018.
 */

public class FragmentAudio extends Fragment {

    public interface ClickListener {
        void positionDownloadClickListener(int position, Button downloadAudioBtn, ProgressBar downloadProgressBar, TextView porcentagemDownload, StorageReference audiosStorageReference);

        void positionDeleteClickListener(int position, Button downloadAudioBtn);
    }

    DatabaseReference audiosDatabaseReference = FirebaseDatabase.getInstance().getReference().child("audios");

    ArrayList<String> nomeArquivoLista = new ArrayList<>();
    ArrayList<String> nomePregacaoLista = new ArrayList<>();
    ArrayList<String> infoPregacaoLista = new ArrayList<>();
    ArrayList<String> urlPregacaoLista = new ArrayList<>();
    ArrayList<Long> tamanhoPregacaoLista = new ArrayList<>();

    RecyclerView audiosRecyclerView;
    Spinner audioSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        pedePermissao();

        preparaPastaExterna();

        audiosRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_audio);
        audioSpinner = (Spinner) view.findViewById(R.id.spinner_audio);

        carregaListasFirebase(audiosDatabaseReference.child("domingo"));

        audioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        carregaListasFirebase(audiosDatabaseReference.child("domingo"));
                        break;
                    case 1:
                        carregaListasFirebase(audiosDatabaseReference.child("confAudaz2018"));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    void preparaPastaExterna() {
        File pastaBatista = new File(Environment.getExternalStorageDirectory() + "/Batista Menorah");

        File pastaAudios = new File(Environment.getExternalStorageDirectory() + "/Batista Menorah/Audios");

        if (!pastaBatista.exists())
            pastaBatista.mkdirs();

        if (!pastaAudios.exists())
            pastaAudios.mkdirs();
    }

    boolean temPermissao() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    void pedePermissao() {
        if (!temPermissao()) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
        preparaPastaExterna();
    }

    void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        audiosRecyclerView.setLayoutManager(layoutManager);

        AdapterAudio adapter = new AdapterAudio(nomeArquivoLista, nomePregacaoLista, infoPregacaoLista, getContext(), new ClickListener() {
            @Override
            public void positionDownloadClickListener(int position, Button downloadAudioBtn, ProgressBar downloadProgressBar, TextView porcentagemDownload, StorageReference audiosStorageReference) {
                downloadListener(position, downloadAudioBtn, downloadProgressBar, porcentagemDownload, audiosStorageReference);

            }

            @Override
            public void positionDeleteClickListener(int position, Button downloadAudioBtn) {
                deleteListener(position, downloadAudioBtn);
            }
        }, tamanhoPregacaoLista);

        audiosRecyclerView.setAdapter(adapter);
    }

    void carregaListasFirebase(DatabaseReference audiosRef) {
        audiosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomeArquivoLista.clear();
                nomePregacaoLista.clear();
                infoPregacaoLista.clear();
                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    nomePregacaoLista.add(data.child("titulo").getValue().toString());
                    nomeArquivoLista.add(data.child("nome").getValue().toString());
                    infoPregacaoLista.add(data.child("data").getValue().toString());
                    try {
                        urlPregacaoLista.add(data.child("url").getValue().toString());
                        tamanhoPregacaoLista.add(Long.valueOf(data.child("tamanho").getValue().toString()));
                    } catch (Exception e) {
                        urlPregacaoLista.add("");
                        tamanhoPregacaoLista.add((long) 0);
                    }

                }
                Collections.reverse(nomePregacaoLista);
                Collections.reverse(nomeArquivoLista);
                Collections.reverse(infoPregacaoLista);
                Collections.reverse(urlPregacaoLista);
                Collections.reverse(tamanhoPregacaoLista);
                setRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void downloadListener(final int position, final Button downloadAudioBtn, final ProgressBar downloadProgressBar, final TextView porcentagemDownload, final StorageReference audiosStorageReference) {
        final File arquivoAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Batista Menorah/Audios/" + nomePregacaoLista.get(position) + ".mp3");

        if (arquivoAudio.exists()) {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File file = new File(arquivoAudio.toURI());
            intent.setDataAndType(Uri.fromFile(file), "audio/mp3");
            startActivity(intent);
        } else {
            downloadAudioBtn.setVisibility(View.INVISIBLE);
            downloadProgressBar.setVisibility(View.VISIBLE);
            porcentagemDownload.setVisibility(View.VISIBLE);

            //Declara construtor de notificacoes
            final NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext());

            final int[] segundoAnterior = {-1}; // gambiarra para guardar os segundos antigos



            /*mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                    Toast.makeText(getContext(), "Download cancelado", Toast.LENGTH_LONG).show();
                }
            });*/


            //Firebase Download
            /*audiosStorageReference.child(nomeArquivoLista.get(position)).getFile(arquivoAudio).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    manager.cancel(position);
                    Toast.makeText(getContext(), "Salvo em: "+arquivoAudio.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    downloadAudioBtn.setBackgroundResource(R.mipmap.play);
                    downloadAudioBtn.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.INVISIBLE);
                    porcentagemDownload.setVisibility(View.INVISIBLE);
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    long porcentagem = taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount();
                    porcentagemDownload.setText(porcentagem+"%");

                    Calendar calendar = Calendar.getInstance();

                    int segundoAtual = calendar.get(Calendar.SECOND);

                    if(segundoAnterior[0] !=segundoAtual) {
                        segundoAnterior[0] =segundoAtual;
                        notifyManager(position, porcentagem, manager, mBuilder);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Erro: "+ e,Toast.LENGTH_LONG).show();
                    manager.cancel(position);
                    if(arquivoAudio.exists()) {
                        arquivoAudio.delete();
                    }
                    downloadAudioBtn.setVisibility(View.VISIBLE);
                    downloadProgressBar.setVisibility(View.INVISIBLE);
                    porcentagemDownload.setVisibility(View.INVISIBLE);
                }
            });*/
        }
    }

    void deleteListener(int position, Button downloadAudioBtn) {

        final File arquivoAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Batista Menorah/Audios/" + nomePregacaoLista.get(position) + ".mp3");

        if (arquivoAudio.exists()) {
            if (arquivoAudio.delete()) {
                downloadAudioBtn.setBackgroundResource(R.mipmap.download);
                Toast.makeText(getContext(), "Audio Excluído", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(getContext(), "O audio não existe", Toast.LENGTH_LONG).show();
    }

    void notifyManager(int position, long porcentagem, NotificationManager manager, NotificationCompat.Builder mBuilder) {

        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("Baixando " + nomePregacaoLista.get(position)).setContentText(" " + porcentagem + "% completo");

        manager.notify(position, mBuilder.build());
    }

    class AdapterAudio extends RecyclerView.Adapter<AdapterAudio.MyViewHolder> {

        ArrayList<String> nomeArquivoLista;
        ArrayList<String> nomePregacaoLista;
        ArrayList<String> infoPregacaoLista;
        ArrayList<Long> tamanhoPregacaoLista;

        Context context;


        StorageReference audiosStorageReference = FirebaseStorage.getInstance().getReference().child("audios");

        FragmentAudio.ClickListener listener;

        AdapterAudio(ArrayList<String> nomeArquivoLista, ArrayList<String> nomePregacaoLista, ArrayList<String> infoPregacaoLista, Context context, FragmentAudio.ClickListener listener, ArrayList<Long> tamanhoPregacaoLista) {
            this.nomeArquivoLista = nomeArquivoLista;
            this.nomePregacaoLista = nomePregacaoLista;
            this.infoPregacaoLista = infoPregacaoLista;
            this.tamanhoPregacaoLista = tamanhoPregacaoLista;
            this.context = context;
            this.listener = listener;
        }

        @Override
        public AdapterAudio.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_adapter, parent, false);
            AdapterAudio.MyViewHolder holder = new AdapterAudio.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final AdapterAudio.MyViewHolder holder, int position) {

            final int pos = holder.getAdapterPosition();
            File arquivoAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Batista Menorah/Audios/" + nomePregacaoLista.get(pos) + ".mp3");

            //Prepara o texto de informacao
            holder.nomePregacaoText.setText(nomePregacaoLista.get(pos));
            holder.infoPregacaoText.setText(infoPregacaoLista.get(pos) + " (" + tamanhoPregacaoLista.get(pos) + "MB)");

        /*audiosStorageReference.child(nomeArquivoLista.get(pos)).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                try {
                    holder.infoPregacaoText.setText(infoPregacaoLista.get(pos) + " (" + storageMetadata.getSizeBytes() / 1000000 + "MB)");
                }catch (Exception e){
                }
            }
        });*/

            //Se o audio já existe muda o botao para botao de play
            if (arquivoAudio.exists())
                holder.downloadAudioBtn.setBackgroundResource(R.mipmap.play);
            if (!arquivoAudio.exists())
                holder.downloadAudioBtn.setBackgroundResource(R.mipmap.download);


        }

        @Override
        public int getItemCount() {
            return nomeArquivoLista.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {


            TextView nomePregacaoText;
            TextView infoPregacaoText;
            Button downloadAudioBtn;
            Button deleteAudioBtn;
            ProgressBar downloadProgressBar;
            TextView porcentagemDownload;


            public MyViewHolder(View itemView) {


                super(itemView);
                nomePregacaoText = (TextView) itemView.findViewById(R.id.nome_pregacao);
                infoPregacaoText = (TextView) itemView.findViewById(R.id.dados_pregacao);
                downloadAudioBtn = (Button) itemView.findViewById(R.id.download_audio_btn);
                deleteAudioBtn = (Button) itemView.findViewById(R.id.delete_audio_btn);
                downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.download_progress_bar);
                porcentagemDownload = (TextView) itemView.findViewById(R.id.porcentagem_download);

                downloadAudioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = getAdapterPosition();
                        setIsRecyclable(false);
                        //listener.positionDownloadClickListener(position, downloadAudioBtn, downloadProgressBar, porcentagemDownload, audiosStorageReference);

                        File arquivoAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Batista Menorah/Audios/" + nomePregacaoLista.get(position) + ".mp3");

                        if (arquivoAudio.exists()) {

                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            File file = new File(arquivoAudio.toURI());
                            intent.setDataAndType(Uri.fromFile(file), "audio/mp3");
                            startActivity(intent);
                        } else {

                            DownloadTask downloadTask = new DownloadTask(context, position);
                            downloadTask.execute("the url to the file you want to download");
                        }
                    }
                });

                deleteAudioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = getAdapterPosition();

                        listener.positionDeleteClickListener(position, downloadAudioBtn);
                    }
                });


            }

            private class DownloadTask extends AsyncTask<String, Integer, String> {

                private Context context;
                private PowerManager.WakeLock mWakeLock;
                private int position;
                File arquivoAudio;

                public DownloadTask(Context context, int position) {
                    this.context = context;
                    this.position = position;
                }

                @Override
                protected String doInBackground(String... sUrl) {
                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;
                    try {
                        arquivoAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Batista Menorah/Audios/" + nomePregacaoLista.get(position) + ".mp3");
                        URL url = new URL(urlPregacaoLista.get(position).toString());
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        // expect HTTP 200 OK, so we don't mistakenly save error report
                        // instead of the file
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            return "Server returned HTTP " + connection.getResponseCode()
                                    + " " + connection.getResponseMessage();
                        }

                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        long fileLength = tamanhoPregacaoLista.get(position)*1000000;

                        // download the file
                        input = connection.getInputStream();
                        output = new FileOutputStream(arquivoAudio.getAbsoluteFile());

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                input.close();
                                return null;
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                                publishProgress((int) (total * 100 / fileLength));
                            output.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        return e.toString();
                    } finally {
                        try {
                            if (output != null)
                                output.close();
                            if (input != null)
                                input.close();
                        } catch (IOException ignored) {
                        }

                        if (connection != null)
                            connection.disconnect();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    // take CPU lock to prevent CPU from going off if the user
                    // presses the power button during download
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                            getClass().getName());
                    mWakeLock.acquire();
                    downloadAudioBtn.setVisibility(View.INVISIBLE);
                    downloadProgressBar.setVisibility(View.VISIBLE);
                    porcentagemDownload.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onProgressUpdate(Integer... progress) {
                    super.onProgressUpdate(progress);
                    porcentagemDownload.setText(progress[0]+"%");

                }

                @Override
                protected void onPostExecute(String result) {
                    mWakeLock.release();
                    if (result != null) {
                        Toast.makeText(context, "Erro no download: " + result, Toast.LENGTH_LONG).show();
                        if(arquivoAudio.exists()) {
                            arquivoAudio.delete();
                        }
                        downloadAudioBtn.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.INVISIBLE);
                        porcentagemDownload.setVisibility(View.INVISIBLE);
                    }
                    else {
                        downloadAudioBtn.setBackgroundResource(R.mipmap.play);
                        downloadAudioBtn.setVisibility(View.VISIBLE);
                        downloadProgressBar.setVisibility(View.INVISIBLE);
                        porcentagemDownload.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Salvo em: "+arquivoAudio.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        }


    }
}
