package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class reseptinNaytto extends AppCompatActivity {


    ArrayList<String> raaka_aineet = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;

    //String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //User-id talteen
    String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

    String listaTitteli;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseptin_naytto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Tuodaan edellisestä (reseptiLista) activitystä ruokalajin nimi
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String  ruokaLaji = bundle.getString("key");
        listaTitteli = bundle.getString("listaTitteli");
        reseptinNaytto.this.setTitle(ruokaLaji + getString(R.string.ainekset_aineslista)); //Asetetaan toolbarin titteli vastaamaan kyseistä ruokalajia

        raaka_aineet = new ArrayList<>();
        Collections.addAll(raaka_aineet); //Lisää kokonaisen setin tarvittaessa.
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, raaka_aineet);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);



        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/" + kayttaja_email + "/listat/" + listaTitteli + "/reseptit/"+ruokaLaji);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getValue(String.class);
                raaka_aineet.add(value);
                adapter.notifyDataSetChanged();
                Collections.sort(raaka_aineet);
                lv.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //18.1.2019/////////////////////////////////////////////////////////
                String value = dataSnapshot.getValue(String.class);
                raaka_aineet.remove(value);
                adapter.notifyDataSetChanged();
                Collections.sort(raaka_aineet);
                //storeArrayVal(shoppingList, getApplicationContext());
                lv.setAdapter(adapter);
                ////////////////////////////////////////////////////////////////////
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(raaka_aineet.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(),getString(R.string.virhe), Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reseptinnaytto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_ohje){
            AlertDialog alertDialog = new AlertDialog.Builder(reseptinNaytto.this).create();
            alertDialog.setTitle(getString(R.string.ohje));
            alertDialog.setMessage(getString(R.string.ohje_aineslista));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        if (id == R.id.lisääRaakaAine){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.lisaa_raaka_aine_reseptiin));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                        Intent intent1 = getIntent(); //Tuodaan dataa edellisestä activitystä (Ruokalajin nimi)
                        Bundle bundle1 = intent1.getExtras();
                        String resepti = bundle1.getString("key");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users/" + kayttaja_email + "/listat/" + listaTitteli+ "/reseptit/" + resepti); //HUOM. oikean polun määritys
                    String key = input.getText().toString();

                    if(key.equals("")){
                        Toast.makeText(getApplicationContext(),getString(R.string.pakkolisata), Toast.LENGTH_LONG).show();
                    } else {
                        key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();

                        myRef.child(key).setValue(key);
                        String pushId = myRef.getKey();
                    }



                }
            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    public void removeElement(final String selectedItem, final int position){ //pöljä nimi, kokeile voiko vaihtaa.. 19.2.2019
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedItem);
        builder.setPositiveButton(getString(R.string.poista_), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String listaTeksti =(lv.getItemAtPosition(position).toString());
                myRef.child(listaTeksti).removeValue();

            }
        });

        builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}


