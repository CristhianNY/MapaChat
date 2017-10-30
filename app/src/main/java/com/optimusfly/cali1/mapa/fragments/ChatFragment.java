package com.optimusfly.cali1.mapa.fragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.optimusfly.cali1.mapa.Adapters.ChatAdapter;
import com.optimusfly.cali1.mapa.Adapters.SliderAdapter;
import com.optimusfly.cali1.mapa.Dibujos.BubbleTransformation;
import com.optimusfly.cali1.mapa.Models.Mensaje;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.References;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends DialogFragment {

    private String idUsuario;
    private CircleImageView imagenDePerfil;
    private TextView nombreUsuario;
    private ArrayList<String> IMAGES = new ArrayList<>();
    private RecyclerView recyclerViewChat;
    private FirebaseUser usuario;
    private ChatAdapter adapter;
    private ImageButton enviar;
    private ImageView cerrarChat;
    private EditText mensajeContenido;
    private List<Mensaje> mensajes;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
   View v = inflater.inflate(R.layout.fragment_chat, container, false);
        idUsuario = this.getArguments().getString("idUsuario");

        // declaramos los objetos en las vista de chat

        imagenDePerfil = (CircleImageView) v.findViewById(R.id.imagen_perfil);
        nombreUsuario = (TextView) v.findViewById(R.id.nombreUsurio);
        recyclerViewChat = (RecyclerView) v.findViewById(R.id.recycler_chat);
        enviar = (ImageButton) v.findViewById(R.id.enviar);
        mensajeContenido = (EditText) v.findViewById(R.id.mensaje_contenido);
        cerrarChat = (ImageView) v.findViewById(R.id.cerrar_chat);

        cerrarChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        // fin declaraciones
// acciones

        // enventos Click listener
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mensajeContenido.getText()!= null){
                    sendMessage(mensajeContenido.getText().toString());
                }



            }
        });

        // cargamos los mensajes

        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setNestedScrollingEnabled(false);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());

        recyclerViewChat.setLayoutManager(mManager);
        //mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);


        mensajes = new ArrayList<>();
        cargarInfoChat();


        // aca mostramos o capturamos la imagen de perfil
        DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");
        usuarioReferece.orderByChild("idUsuario").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()
                        ) {

                    final Usuario userInfo = snapshot.getValue(Usuario.class);


                    Picasso.with(getActivity()).load(userInfo.getImagenPerfil()).into(imagenDePerfil);

                    nombreUsuario.setText(userInfo.getUsuario());






                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // enventos click


        return v;

    }



    public void cargarInfoChat(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference ref = database.getReference().child(References.CHAT+"/"+idUsuario+"_"+usuario.getUid());





        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mensajes.removeAll(mensajes);

                for (DataSnapshot snapshop:
                        dataSnapshot.getChildren()
                        ) {

                   // contador2 = contador2 +1;
                    Mensaje comentario = snapshop.getValue(Mensaje.class);

                    //        comentario.getRating();
                //    calificacion =  calificacion + Float.parseFloat(comentario.getRating());

                    mensajes.add(comentario);
                    adapter = new ChatAdapter(mensajes,ChatFragment.this);
                    recyclerViewChat.setAdapter(adapter);

                    //   adapter.notifyDataSetChanged();
                   // rb.setRating(calificacion/contador2);

                    recyclerViewChat.smoothScrollToPosition(adapter.getItemCount());
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    public void sendMessage (final String mensaje){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference().child(References.CHAT+"/"+idUsuario+"_"+usuario.getUid());
        final DatabaseReference ref2 = database.getReference().child(References.CHAT+"/"+usuario.getUid()+"_"+idUsuario);
        adapter = new ChatAdapter(mensajes,this);
        recyclerViewChat.setAdapter(adapter);

        if (AccessToken.getCurrentAccessToken() != null) {

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(final JSONObject me, GraphResponse response) {

                            if (AccessToken.getCurrentAccessToken() != null) {

                                if (me != null) {

                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String profileImageUrl = ImageRequest.getProfilePictureUri(me.optString("id"), 500, 500).toString();
                                            // Log.i(LOG_TAG, profileImageUrl);
                                            String comentariokey = ref.push().getKey();

                                            //String valorCalificacion = Float.toString(calificacion);

                                            Mensaje m = new Mensaje(mensaje,profileImageUrl,"1","00:00",usuario.getUid());

                                            //   contectReview.setText("");
                                            ref.push().setValue(m);
                                            ref2.push().setValue(m);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });





                                }
                            }
                        }
                    });
            GraphRequest.executeBatchAsync(request);
        }
    }

}
