package com.optimusfly.cali1.mapa;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginActivity extends AppCompatActivity {


    private Button loginbtn,registrationbtn;
    private EditText emailTxr, paswordTxt;
    // private FirebaseUser  usuer;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        auth =FirebaseAuth.getInstance();

        firebaseListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapaActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        loginbtn = (Button) findViewById(R.id.loginBtn);
        registrationbtn = (Button) findViewById(R.id.registrationBtn);
        emailTxr= (EditText) findViewById(R.id.email);
        paswordTxt = (EditText) findViewById(R.id.password);

        registrationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email  = emailTxr.getText().toString();
                final String password = paswordTxt.getText().toString();
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this,"registro Error",Toast.LENGTH_LONG).show();

                        }else{
                            String user_id = auth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                            currentUserDb.setValue(true);
                        }
                    }
                });

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email  = emailTxr.getText().toString();
                final String password = paswordTxt.getText().toString();
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this,"Error al entrar",Toast.LENGTH_LONG).show();

                        }

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseListener);
    }
}
