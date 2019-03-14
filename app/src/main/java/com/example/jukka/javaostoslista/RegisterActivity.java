package com.example.jukka.javaostoslista;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    EditText email_input,password_input, kayttaja_input;
    Button registerButton,loginButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference nimiRef,publicRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        email_input = (EditText) findViewById(R.id.email);
        password_input = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.new_user_button);
        loginButton = (Button) findViewById(R.id.login_button);
        kayttaja_input = (EditText) findViewById(R.id.kayttaja_nimi);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_input.getText().toString();
                String password = password_input.getText().toString();
                final String kayttajaNimi = kayttaja_input.getText().toString();


                if(TextUtils.isEmpty(kayttajaNimi)){
                    Toast.makeText(getApplicationContext(),"Lisää käyttäjänimi",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Sähköposti on pakollinen",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Salasana on pakollinen",Toast.LENGTH_SHORT).show();
                }

                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Salasanan tulee olla vähintään 6 merkkiä pitkä",Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    database = FirebaseDatabase.getInstance();
                                    nimiRef = database.getReference("Users/" + kayttaja_id); //Luodaan tietokantapolku nimen asettamista varten
                                    publicRef = database.getReference("Users/");
                                    nimiRef.child("/nimi").setValue(kayttajaNimi);
                                    nimiRef.child("/id").setValue(kayttaja_id);
                                    nimiRef.child("/email").setValue(kayttaja_email);
                                    publicRef.child("emailToUid").child(kayttaja_email.replace(".", ",")).setValue(kayttaja_id);

                                    Intent mene = new Intent(getApplicationContext(), MainActivity.class);
                                    mene.putExtra("key", kayttajaNimi);
                                    startActivity(mene);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Sähköposti tai salasana virheellinen!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
}