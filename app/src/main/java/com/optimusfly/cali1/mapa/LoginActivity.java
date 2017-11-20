package com.optimusfly.cali1.mapa;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.optimusfly.cali1.mapa.Models.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fiAuthStateListener;
    private FirebaseUser usuario;
    private Button loginButton2;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//       FacebookSdk.sdkInitialize(getApplicationContext());


//        Blurry.with(getApplicationContext()).radius(25).sampling(2).onto((ViewGroup) fondoLogin);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.cristhianbonilla.cantantesmedellin", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        crearHask();
        setContentView(R.layout.activity_login);

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton2 = (Button) findViewById(R.id.btn_login);
        fbLogin();


        loginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                //fbLogin();
            }
        });

        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),LoginActivity.this)) {
            //fetchLocationData();
        }
        else
        {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),LoginActivity.this);
        }
    }

    private boolean checkPermission(String accessFineLocation, Context applicationContext, LoginActivity loginActivity) {

        int result = ContextCompat.checkSelfPermission(applicationContext, accessFineLocation);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }

    }

    final int LOCATION_REQUEST_CODE = 1;
    private void insertarUsuario(final String idUsuarioFu, final  String email, final String deviceToken){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference(References.USUARIO);
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
                                            Usuario usuarioFb  = new Usuario(
                                                    me.optString("name"),email,profileImageUrl,idUsuarioFu,"regular",deviceToken,me.optString("id"));

                                            //   contectReview.setText("");
                                            ref.child(idUsuarioFu).setValue(usuarioFb);
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

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
         //   Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public static boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void fbLogin() {




        mAuth = FirebaseAuth.getInstance();

        fiAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                   // Log.d("Login",user.getUid());


                    insertarUsuario(user.getUid(), user.getEmail(), deviceToken);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }else{
                   // Log.d("Login","Signed Out");
                   // Toast.makeText(getApplicationContext(), "Estamos AFUERA ",Toast.LENGTH_LONG).show();

                }
            }
        };
        loginButton.setReadPermissions("email","user_friends");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handledFacebookAccessToken(loginResult.getAccessToken());



            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("error ",error.toString());

            }
        });

    }


    private void crearHask() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cristhianbonilla.askloy", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
               // Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goMainScreen() {


        sendTokenToServer();
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
    }
    private void sendTokenToServer() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();


        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = userEmail;

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void handledFacebookAccessToken(final AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.fiebase_error_login,Toast.LENGTH_LONG).show();
                }else {

                    GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                            accessToken,
                            //AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    //   Intent intent = new Intent(MainActivity.this,friend.class);
                                    try {
                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        usuario = FirebaseAuth.getInstance().getCurrentUser();
                                        //  final DatabaseReference ref = database.getReference(References.FRIENDS+"/"+ usuario.getUid());
                                        // final DatabaseReference refAsk = database.getReference(References.FRIENDS);
                                        final JSONArray rawName = response.getJSONObject().getJSONArray("data");

                                        //     ArrayList<Friend> castList= new ArrayList<Friend>();
                                        final DatabaseReference refUser = database.getReference("usuarios"+"/"+usuario.getUid()+"/"+"username");
                                        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String value = (String) dataSnapshot.getValue();
                                                System.out.println(value);
                                                //  Toast.makeText(getContext(), username.getUsername(),Toast.LENGTH_LONG).show();

                                                for (int i=0; i < rawName.length(); i++) {

                                                    try {
                                                        JSONObject jpersonObj = rawName.getJSONObject(i);

                                                        //  Friend amigos = new Friend(jpersonObj.getString("id"),jpersonObj.getString("name"));
                                                        //ref.setValue(amigos);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        /**   ItemFragment itemFragment = new ItemFragment();
                                         Bundle bundle = new Bundle();
                                         String nameFriends = rawName.toString();
                                         bundle.putString("nameFriends",nameFriends);

                                         itemFragment.setArguments(bundle);
                                         **/
                                        System.out.print(rawName.toString()+"algo por aca");
                                        //  intent.putExtra("jsondata", rawName.toString());
                                        //     startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();
                    goMainScreen();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fiAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fiAuthStateListener);
    }

}
