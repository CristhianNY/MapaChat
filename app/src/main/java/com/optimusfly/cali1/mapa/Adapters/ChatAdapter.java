package com.optimusfly.cali1.mapa.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.optimusfly.cali1.mapa.Models.Mensaje;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.fragments.ChatFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cali1 on 23/10/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Mensaje> listMensajes;
    private Fragment chatFragment;
    private FirebaseUser usuario;
    public ChatAdapter(List<Mensaje> listMensaje, ChatFragment chatFragment) {

        this.listMensajes = listMensaje;

        this.chatFragment = chatFragment;
    }

    public void addMensaje(Mensaje m){
        listMensajes.add(m);

    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       // View v = LayoutInflater.from(chatFragment).inflate(R.layout.row_chats,parent,false);
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_chats, parent, false);


        return new ChatViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {


        usuario = FirebaseAuth.getInstance().getCurrentUser();
        final Mensaje mensaje = listMensajes.get(position);





        if(mensaje.getIdUsuario().equals(usuario.getUid())){

            holder.mensajeRecivido.setText(mensaje.getMensaje());
            holder.frameImagen.setGravity(Gravity.RIGHT);


            holder.boxMensaje.setBackground(ContextCompat.getDrawable(chatFragment.getContext(), R.drawable.round_blue));
            holder.mensajeRecivido.setTextColor(Color.WHITE);

        }else{
            holder.mensajeRecivido.setText(mensaje.getMensaje());
        }

        Picasso.with(this.chatFragment.getContext()).load(mensaje.getFotoperfil()).into(holder.imagenPerfil);







    }



    @Override
    public int getItemCount() {
        return listMensajes.size();
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView mensajeRecivido;
        private CircleImageView imagenPerfil;

        private LinearLayout frameImagen,boxMensaje;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mensajeRecivido = (TextView) itemView.findViewById(R.id.mensaje_recivido);

            imagenPerfil = (CircleImageView) itemView.findViewById(R.id.imagen_perfil_mensaje);
            frameImagen = (LinearLayout) itemView.findViewById(R.id.frame_mensaje);
            boxMensaje = (LinearLayout) itemView.findViewById(R.id.box_mensaje);
        }

    }
}
