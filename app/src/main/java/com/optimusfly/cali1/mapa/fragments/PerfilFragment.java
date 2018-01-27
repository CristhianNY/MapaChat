package com.optimusfly.cali1.mapa.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.optimusfly.cali1.mapa.Adapters.SliderAdapter;
import com.optimusfly.cali1.mapa.LoginActivity;
import com.optimusfly.cali1.mapa.Models.ChatList;
import com.optimusfly.cali1.mapa.Models.Usuario;
import com.optimusfly.cali1.mapa.R;
import com.optimusfly.cali1.mapa.References;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.R.attr.data;
import static android.R.attr.key;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    private ViewPager viewPager;
    private SliderAdapter adapter;
    private ArrayList<String> IMAGES = new ArrayList<>();
    private Button btnStartChat,abrirPerfil;
    private String idUsuario,email;
    private TextView edad,nombredeUsuario;
    private FirebaseUser usuario;
    private FragmentTransaction fragmentTransaction;
    private final static String TAG = "DashBoardActivity";

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_perfil, container, false);


        btnStartChat = (Button) v.findViewById(R.id.btn_start_chat) ;
        viewPager =(ViewPager) v.findViewById(R.id.view_pager);
        /**rightNav = (ImageButton) v.findViewById(R.id.right_nav);
        leftNav = (ImageButton) v.findViewById(R.id.left_nav);**/

        nombredeUsuario = (TextView) v.findViewById(R.id.nombredeUsuario);
        edad = (TextView) v.findViewById(R.id.age);
        idUsuario = this.getArguments().getString("idUsuario");
        email = this.getArguments().getString("email");

        abrirPerfil = (Button)v.findViewById(R.id.abrir_perfil);
        final DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");

        usuarioReferece.orderByChild("idUsuario").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()
                        ) {

                    final Usuario userInfo = snapshot.getValue(Usuario.class);

                    IMAGES.add(userInfo.getImagenPerfil());

                    adapter = new SliderAdapter(getActivity(),idUsuario, IMAGES);


                    viewPager.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // eventos click

        abrirPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{



                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference ref= database.getReference("usuario/"+idUsuario+"/urlPerfil");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = (String) dataSnapshot.getValue();

                            // do your stuff here with value
                            if(value.length() > 0){



                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });


                }catch(Exception e){

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/appetizerandroid")));

                }
            }
        });
        
        cargarInformacionUsuario(idUsuario);
        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ChatFragment fragmentChat = new ChatFragment();
                //  MainFragment mainFragment = new MainFragment();
                // principalFragment.setArguments(bundle);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                usuario = FirebaseAuth.getInstance().getCurrentUser();
             final   DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("listChat").child(usuario.getUid()).child(idUsuario);
              //  final DatabaseReference ref = database.getReference(References.LISTCHAT+ "/" + usuario.getUid());
                final DatabaseReference ref2 = database.getReference(References.USUARIO + "/"+idUsuario);
               // adapterComentarios = new ComentariosAdapter(comentarios,this);
              //  Toast.makeText(getContext(),key ,Toast.LENGTH_LONG).show();

             //   recyclerView.setAdapter(adapterComentarios);
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {

                      //  String profileImageUrl = ImageRequest.getProfilePictureUri(me.optString("id"), 500, 500).toString();
                        // Log.i(LOG_TAG, profileImageUrl);

                        Usuario usuario = dataSnapshot2.getValue(Usuario.class);
                        ChatList chatlist = new ChatList(usuario.getImagenPerfil(),
                                usuario.getUsuario(), new Date().toString(),idUsuario,idUsuario,email);
                       // ref.setValue(chatlist);
                        //   String valorCalificacion = Float.toString(calificacion);


                        //   contectReview.setText("");

                        Bundle bundle = new Bundle();
                        bundle.putString("idUsuario",idUsuario.toString());
                        bundle.putString("email",email);
                        fragmentChat.setArguments(bundle);
                        fragmentChat.show(getFragmentManager(),"chat");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


              /**  FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.container, fragmentChat);
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();**/
            }
        });



        return v;


    }

    private void cargarInformacionUsuario(String id) {

        final DatabaseReference usuarioReferece = FirebaseDatabase.getInstance().getReference("usuario");

        usuarioReferece.orderByChild("idUsuario").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()
                        ) {

                    final Usuario userInfo = snapshot.getValue(Usuario.class);
                    nombredeUsuario.setText(userInfo.getUsuario());

                    if(userInfo.getBirthday().toString().length() != 0){



                        String date = userInfo.getBirthday();


                        String[] calend = date.split("/");
                        int month = Integer.parseInt(calend[1]);
                        int day = Integer.parseInt(calend[0]);
                        int year = Integer.parseInt(calend[2]);

                        int userEdad =   getAge(year,month,day);

                        String EdadUsuario = String.valueOf(userEdad);

                        edad.setText(EdadUsuario);
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

   /** @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_chat, menu);
        inflater.inflate(R.menu.usuarios_cerca_menu, menu);
        // Use filter.xml from step 1
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.usuarios){
            //Do whatever you want to do
            ListUserNearFragment listUserNearFragment = new ListUserNearFragment();


            FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            transaction.replace(R.id.container, listUserNearFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
            return true;
        }
        if(id == R.id.listChat){

            ChatListFragment chatListFragment = new ChatListFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container,chatListFragment);
            transaction.addToBackStack(null);

            transaction.commit();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    **/
 // arreglar el problema de la edad . sale mal

    private int getAge(int year, int month, int day){
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!= null){

        }else{
            Intent myIntent = new Intent( getActivity(), LoginActivity.class);
            startActivityForResult(myIntent, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!= null){

        }else{
            Intent myIntent = new Intent( getActivity(), LoginActivity.class);
            startActivityForResult(myIntent, 0);
        }
    }
}
