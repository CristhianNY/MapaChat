package com.optimusfly.cali1.mapa;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.optimusfly.cali1.mapa.Adapters.SliderAdapter;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.fragments.ChatFragment;
import com.optimusfly.cali1.mapa.fragments.MapaFragment;

import java.util.ArrayList;

public class PerfilActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private  ImageButton rightNav ,leftNav;
    private SliderAdapter adapter;
    private ArrayList<String> IMAGES = new ArrayList<>();
    private Button btnStartChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_perfil);

        // Delcaraciones

        btnStartChat = (Button) findViewById(R.id.btn_start_chat) ;
        viewPager =(ViewPager) findViewById(R.id.view_pager);
        rightNav = (ImageButton) findViewById(R.id.right_nav);
        leftNav = (ImageButton) findViewById(R.id.left_nav);

        // end Declaraciones
        Intent intent = getIntent();
        final String idUsuario = intent.getStringExtra("idUsuario");
        DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");

        usuarioReferece.orderByChild("idUsuario").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()
                        ) {

                    final Usuario userInfo = snapshot.getValue(Usuario.class);

                    IMAGES.add(userInfo.getImagenPerfil());

                    adapter = new SliderAdapter(getApplicationContext(),idUsuario, IMAGES);


                    viewPager.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Actions

        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ChatFragment fragmentChat = new ChatFragment();
                //  MainFragment mainFragment = new MainFragment();
                // principalFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentChat)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
            }
        });






    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        MapaFragment mapaFragment = new MapaFragment();
        //  MainFragment mainFragment = new MainFragment();
        // principalFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mapaFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
    }
}
