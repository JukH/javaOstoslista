package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.jukka.javaostoslista.MainActivity.getArrayVal;

public class JaetutReseptitActivity extends AppCompatActivity {


    ArrayList<String> reseptit = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;

    String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //User-id talteen
    String jakajan_id;


    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRefResepti;
    DatabaseReference myRefReseptinHaku;
    DatabaseReference myRefOstosLista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resepti_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        JaetutReseptitActivity.this.setTitle(getString(R.string.resepti_titteli));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        jakajan_id = bundle.getString("key");


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/" + jakajan_id + "/lista" + "/reseptit"); //HUOM. viittaus eri polkuun tietokannassa

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                try {

                    String testi = dataSnapshot.getKey();
                    reseptit.add(testi); //Lisää tuplana listalle uudelleen avatteassa appia?
                    adapter.notifyDataSetChanged();
                    Collections.sort(reseptit);
                    lv.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(JaetutReseptitActivity.this, getString(R.string.virhe), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String testi = dataSnapshot.getKey().toString();
                reseptit.remove(testi);
                adapter.notifyDataSetChanged();
                Collections.sort(reseptit);

                lv.setAdapter(adapter);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reseptit = getArrayVal(getApplicationContext());
        Collections.sort(reseptit);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reseptit);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(reseptit.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.virhe), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reseptilista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_ohje) {
            AlertDialog alertDialog = new AlertDialog.Builder(JaetutReseptitActivity.this).create();
            alertDialog.setTitle(getString(R.string.ohje));
            alertDialog.setMessage(getString(R.string.ohjeet_resepti));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        if (id == R.id.action_reseptiOpetus) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.nimea_resepti));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //FIREBASETESTAUS/LISÄÄMINEN

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    String key = input.getText().toString();


                    if (key.equals(null) || key.equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.pakkolisata), Toast.LENGTH_LONG).show();
                    } else {

                        key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
                        myRef.child("Users/" + jakajan_id + "/lista" + "/reseptit").child(key).setValue(key); // 2.2.2019 Menee oikeaan osoitteeseen, nyt vielä reseptit omiin lokereoihin.

                        String pushId = myRef.getKey();

                        Intent mene = new Intent(JaetutReseptitActivity.this, JaettuReseptinNaytto.class); //2.2.2019 avataan toinen luokka jotta saadaan listalle reseptiobjektit
                        mene.putExtra("key", key);
                        mene.putExtra("key2", jakajan_id);
                        startActivity(mene);
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


    public void removeElement(final String selectedItem, final int position) { //Nimestä huolimatta tämä metodi hoitaa ostoksien lisäämisen resepti-listalta ostoslistalle
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedItem);
        builder.setPositiveButton(getString(R.string.poista_), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String listaTeksti = (lv.getItemAtPosition(position).toString());
                myRef.child(listaTeksti).removeValue();

                reseptit.remove(position);

                adapter.notifyDataSetChanged();
                Collections.sort(reseptit);

                lv.setAdapter(adapter);


            }
        });

        builder.setNeutralButton(getString(R.string.lisaa_listalle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ////////////////////////////////////////////////////////////////////////////
                myRefReseptinHaku = database.getInstance().getReference("Users/" + jakajan_id + "/lista" + "/reseptit/" + selectedItem);
                myRefResepti = database.getReference();
                myRefOstosLista = database.getReference("Users/" + jakajan_id + "/lista" + "ostos");


                myRefReseptinHaku.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        for (DataSnapshot ds1 : dataSnapshot.getChildren()) {


                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String value = ds.getKey();


                                myRefResepti.child("Users/" + jakajan_id + "/lista" + "/ostos").child(value + " (" + selectedItem + ")").setValue(value + " (" + selectedItem + ")");  //Lisätään reseptin sisältämät tuotteet ostoslistalle ja liitetään perään reseptin nimi

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Toast.makeText(JaetutReseptitActivity.this, selectedItem + getString(R.string.lisatty_listalle), Toast.LENGTH_SHORT).show();


            }
        });
        builder.setNegativeButton(getString(R.string.muokkaa), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent mene = new Intent(JaetutReseptitActivity.this, JaettuReseptinNaytto.class); //2.2.2019 avataan toinen luokka jotta saadaan listalle reseptiobjektit
                mene.putExtra("key", selectedItem);
                mene.putExtra("key2", jakajan_id);
                startActivity(mene);

            }
        });
        builder.show();
    }
}
