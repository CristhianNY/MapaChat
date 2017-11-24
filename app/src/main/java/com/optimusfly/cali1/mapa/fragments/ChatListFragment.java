package com.optimusfly.cali1.mapa.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.optimusfly.cali1.mapa.Adapters.ListChatAdapter;
import com.optimusfly.cali1.mapa.Models.ChatList;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.References;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ChatList> chatlists;
    private ListChatAdapter listChatAdapter;
    private FirebaseUser usuario;
    private String id;
    View v;


    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }

        try {
            v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        id = this.getArguments().getString("id");

        if(id != "null"){
            final ChatFragment fragmentChat = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idUsuario",id);
            bundle.putString("email",id);
            fragmentChat.setArguments(bundle);
            fragmentChat.show(getFragmentManager(),"chat");


        }



        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_chat_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        chatlists = new ArrayList<>();
        cargarChatList();
        return v;
    }


    private void cargarChatList() {
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuario.getUid();



        final  DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("listChat");



        listChatAdapter = new ListChatAdapter(chatlists, this);

        recyclerView.setAdapter(listChatAdapter);

        ref2.child(usuario.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot2) {

                if(dataSnapshot2.getValue()!=null) {
                    for (DataSnapshot snapshot :
                            dataSnapshot2.getChildren()
                            ) {

                        ChatList chatList = snapshot.getValue(ChatList.class);

                        chatlists.add(chatList);

                    }

                }else{

                    System.out.println("por aca esta ");
                }
                recyclerView.smoothScrollToPosition(listChatAdapter.getItemCount());
                listChatAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



}
