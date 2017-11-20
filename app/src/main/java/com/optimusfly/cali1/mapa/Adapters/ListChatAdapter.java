package com.optimusfly.cali1.mapa.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.optimusfly.cali1.mapa.Models.ChatList;
import com.optimusfly.cali1.mapa.Models.UsuarioCerca;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.fragments.ChatFragment;
import com.optimusfly.cali1.mapa.fragments.PerfilFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cali1 on 27/10/2017.
 */

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ListChatViewAdapter> {

    private List<ChatList> listUsuarios;
    private Fragment fragment;

    public ListChatAdapter(List<ChatList> listUsuarios, Fragment fragment) {
        this.listUsuarios = listUsuarios;
        this.fragment = fragment;
    }

    @Override
    public ListChatViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_list_chat, parent, false);
        ListChatViewAdapter holder = new ListChatViewAdapter(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListChatViewAdapter holder, int position) {

        final ChatList usuario = listUsuarios.get(position);


       holder.nombre.setText(usuario.getNombre());
        Picasso.with(fragment.getContext()).load(usuario.getImagenPerfil()).resize(200,200).into( holder.fotoPerfil);
        holder.fecha.setText(usuario.getMensaje());

        holder.relativeLayoutchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString("idUsuario",usuario.getIdUsuario());
                bundle.putString("email",usuario.getIdUsuario());

                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);

                chatFragment.show(fragment.getFragmentManager(),"chat");


            }
        });

    }

    @Override
    public int getItemCount() {
        return listUsuarios.size();
    }

    public static class ListChatViewAdapter extends RecyclerView.ViewHolder{

        private CircleImageView fotoPerfil;
        private TextView nombre,fecha;
        private RelativeLayout relativeLayoutchat;

        public ListChatViewAdapter(View itemView) {
            super(itemView);

            fotoPerfil = (CircleImageView) itemView.findViewById(R.id.foto_perfil);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            relativeLayoutchat = (RelativeLayout) itemView.findViewById(R.id.row_user_near);
        }
    }
}
