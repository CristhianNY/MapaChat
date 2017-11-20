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

import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.Models.UsuarioCerca;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.fragments.ChatFragment;
import com.optimusfly.cali1.mapa.fragments.PerfilFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cali1 on 25/10/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UsuarioViewAdapter> {

     private List<UsuarioCerca> listUsuarios;
     private Fragment fragment;

    public UserListAdapter(List<UsuarioCerca> listUsuarios, Fragment fragment) {
        this.listUsuarios = listUsuarios;
        this.fragment = fragment;
    }

    @Override
    public UsuarioViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_user_near, parent, false);
        UsuarioViewAdapter holder = new UsuarioViewAdapter(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsuarioViewAdapter holder, int position) {

        final UsuarioCerca usuario = listUsuarios.get(position);


        holder.nombre.setText(usuario.getUsuario());
        Picasso.with(fragment.getContext()).load(usuario.getImagenPerfil()).resize(200,200).into( holder.fotoPerfil);
        holder.distancia.setText("12 metros");

        holder.relativeLayoutchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString("idUsuario",usuario.getIdUsuario());
                bundle.putString("email",usuario.getIdUsuario());
                PerfilFragment perfilFragment = new PerfilFragment();
                perfilFragment.setArguments(bundle);

                FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.container, perfilFragment);
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listUsuarios.size();
    }

    public static class UsuarioViewAdapter extends RecyclerView.ViewHolder{

        private CircleImageView fotoPerfil;
        private TextView nombre,distancia;
        private RelativeLayout relativeLayoutchat;

        public UsuarioViewAdapter(View itemView) {
            super(itemView);

            fotoPerfil = (CircleImageView) itemView.findViewById(R.id.foto_perfil);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            distancia = (TextView) itemView.findViewById(R.id.distancia);
            relativeLayoutchat = (RelativeLayout) itemView.findViewById(R.id.row_user_near);
        }
    }
}
