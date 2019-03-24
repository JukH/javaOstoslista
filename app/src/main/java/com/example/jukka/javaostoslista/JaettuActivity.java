package com.example.jukka.javaostoslista;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JaettuActivity extends AppCompatActivity {

    //Tehdään taulukko johon kerätään listan käyttäjät
    ArrayList<String> jäsenet = null;

    ArrayList<String> shoppingList = null;
    ArrayAdapter<String> adapter = null;
    ListView lvMain = null;
    private static MainActivity instance;


    String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Otetaan user-id talteen
    String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    String kayttajaNimi = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    String listaTitteli, jakajan_email, jakajan_nimi, kaverinId,jako_sposti, jakajanId,jakajanEmail, kaverinEmail, nimi;



    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference jaettukoRef, kaveriRef,kaveriRef2, jakoRef, kayttajanIdHakuRef, jäsenetRef,kaverinNimiRef, jakajanNimiRef, nimienLisäysRef, ListatRef, onkoJaettuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Tuodaan mainactivitystä käyttäjän antama email ja id, jonka kanssa halutaan jakaa lista.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //if(intent.hasExtra("key") && intent.hasExtra("key2")) {
            jako_sposti = bundle.getString("key");
            jakajan_email = bundle.getString("key2");
            listaTitteli = bundle.getString("key3");
            jakajan_nimi = bundle.getString("key4");
            kaverinId = bundle.getString("key5");
            jakajanId = bundle.getString("key6");
            kaverinEmail = bundle.getString("key7");
        //}





        //Luodaan viittaus tietokantaan (polkuun /ostos) johon voidaan tämän luokan kautta lisätä tuotteita (Näkyy oikeassa listanäkymässä, eli varsinaisella ostoslistalla, ei mene esim. reseptilistaan)
        String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","); //Otetaan nykyisen käyttäjän s.posti talteen ja kuunnellaan jos joku lisää sen listalleen (haluaa jakaa hänen kanssaan)
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/" + jakajan_email  +  "/listat/" + listaTitteli + "/ostos"); // TESTATAAN JOSKO MENISI JOKAISEN KÄYTTÄJÄN OMAAN POLKUUN
        jaettukoRef = database.getReference("Users/" +  kayttaja_email + "/listat/" + listaTitteli + "/");
        jakoRef = database.getReference("Users/" + jakajan_email  +  "/listat/" + listaTitteli + "/ostos");
        kayttajanIdHakuRef = database.getReference("Users/emailToUid");
        nimienLisäysRef = database.getReference("Users/" + jakajan_email  +  "/listat/" + listaTitteli + "/kaveri" + "/jakajat");
        jäsenetRef = database.getReference("Users/" + kayttaja_id + "/nimi"); //Haetaan listan käyttäjien nimet


        /////////////////////////////////////////////////////
        //Lisätään nykyinen käyttäjä jakajiin tietokannassa kun hän avaa jaetun listan
        /////////////////////////////////////////////////////
        //Lisätään "Sinä" taulukkoon
        jäsenet = getArrayVal(getApplicationContext());

        //Lisätään kuuntelija käyttäjän nimeen, jotta voidaan lisätä se tarvittaessa listan jakajiin (taulukko)
        jäsenetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nimi = dataSnapshot.getValue(String.class);
                if(jäsenet.contains(nimi)) {
                } else {
                    nimienLisäysRef.child(nimi).setValue(nimi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////

        nimienLisäysRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uusiJäsen = dataSnapshot.getValue(String.class);
                jäsenet.add(uusiJäsen);
                JaettuActivity.this.setTitle(listaTitteli + " " +jäsenet.toString());



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String poistunutJäsen = dataSnapshot.getValue(String.class);
                jäsenet.remove(poistunutJäsen);
                JaettuActivity.this.setTitle(listaTitteli + " " + jäsenet.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




/*
            //Tarkastetaan onko käyttäjä jakaja vai kaveri:
            jaettukoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("jakaja")){
                        jakajanEmail = dataSnapshot.child("jakaja").child(jakajan_email).getKey(); //MIKSINULL
                        //Haetaan jakajan ja kaverin ID:t
                        kayttajanIdHakuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String jakajanId = dataSnapshot.child(jakajanEmail).getValue(String.class);
                                //Haetaan jakajan nimi joka näytetään muille paitsi itse jakajalle:
                                jakajanNimiRef = database.getReference("Users/" + jakajanId + "/nimi");
                                jakajanNimiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String jakajanNimi = dataSnapshot.getValue(String.class);
                                        JaettuActivity.this.setTitle(listaTitteli + jäsenet.toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    } else {

                        kayttajanIdHakuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(kaverinEmail == null) {
                                    kaverinId = dataSnapshot.child(jako_sposti).getValue(String.class);
                                } else {
                                    kaverinId = dataSnapshot.child(kaverinEmail).getValue(String.class);
                                }
                                //Haetaan kaverin nimi joka lisättiin listalle:
                                kaverinNimiRef = database.getReference("Users/" + kaverinId + "/nimi");
                                kaverinNimiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String kaverinNimi = dataSnapshot.getValue(String.class);


                                        JaettuActivity.this.setTitle(listaTitteli + jäsenet.toString());

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


*/


//////////////////////////////////////////////////////////////////////

      /* if(kayttaja_email == jakajan_email.replace(",", ".")){
            JaettuActivity.this.setTitle(listaTitteli + " (Luonut: Sinä)");
        } else {
            JaettuActivity.this.setTitle(listaTitteli + " (Luonut: " + jakajan_email.replace(",", ".") + ")");
        } */
        ////////////////////////////////////////////////////////////////////////
        jakoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getValue(String.class); //Poimitaan valittu tuote

                shoppingList.add(value); //Lisätään uusi tuote ostoslistaan
                adapter.notifyDataSetChanged(); //Ilmoitetaan adapterille että on tapahtunut muutos listassa
                Collections.sort(shoppingList); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); //Näytetään päivitetty lista


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Kun tuote poistetaan, tämä metodi poistaa tuotteen listanäkymästä
                String value = dataSnapshot.getValue(String.class);
                shoppingList.remove(value); //Poistetaan näkyvältä ostoslistalta valittu tuote
                adapter.notifyDataSetChanged(); //Ilmoitetaan lista-adapterille että on tapahtunut muutos
                Collections.sort(shoppingList); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); // Näytetään päivitetty lista

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });














//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




        //Koska ollaan onCreate-metodissa:
        shoppingList = getArrayVal(getApplicationContext()); //Annetaan listalle sisältö

        Collections.sort(shoppingList); //Järjestetään listan sisältö (aakkosjärjestys)
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingList); //Alustetaan lista-adapteri
        lvMain = (ListView)findViewById(R.id.listView); //Annetaan listanäkymälle ulkonäkö XML-tiedostosta (id = listView)
        lvMain.setAdapter(adapter); //Asetetaan adapteri (Näytetään sisältö)

        //Luodaan kuuntelija, jonka avulla voidaan poistaa valittu tuote listalta
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(shoppingList.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(),getString(R.string.ei_voi_poistaa), Toast.LENGTH_LONG).show();
                }
            }
        });

        // lvMain.setBackgroundColor(Color.LTGRAY); Saa vaihdettua listan taustavärin



    }
    //Asetetaan menu näkyväksi
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jaettu, menu);
        return true;
    }
    //Asetetaan kuuntelijat valikko-itemeiden käyttöä varten
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        //Jos valitaan "ohje", suoritetaan seuraavat toiminnot:
        if (id == R.id.action_ohje) {
            AlertDialog alertDialog = new AlertDialog.Builder(JaettuActivity.this).create(); //Avataan pikku-ikkuna
            alertDialog.setTitle(getString(R.string.ohje)); //Titteli ikkunalle
            alertDialog.setMessage(getString(R.string.ohjeet_jaettu)); //Asetetaan viesti
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", //Luodaan "kuittaus"-nappi...
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //...jota painaessa suljetaan ohje.
                        }
                    });
            alertDialog.show(); //Asetetaan viesti-ikkuna näkyväksi
        }
////////////////////

        if(id == R.id.action_jaa){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.jaa_lista));
            final EditText input = new EditText(this);
            builder.setMessage(getString(R.string.anna_sposti));
            builder.setView(input);
            builder.setPositiveButton("Jaa lista", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Lisätään ehto, että vain listan omistaja voi jakaa sitä eteenpäin
                    jaettukoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("jakaja")){
                                Toast.makeText(JaettuActivity.this, getString(R.string.vain_admin_voi_jakaa), Toast.LENGTH_SHORT).show();
                            } else {
                                final String jako_sposti = input.getText().toString().trim();
                                //imiRef.child("/jasenet").child("/toinen").setValue(jako_sposti);
                                //imiRef.child("/jasenet").child("/admin").setValue(kayttaja_email);
                                final String jako_sposti2 = jako_sposti.replace(".",",");

                                ///////////////////////////////


                                kayttajanIdHakuRef.addListenerForSingleValueEvent(new ValueEventListener() { //Tarkastetaan onko annettua käyttäjää olemassa
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(jako_sposti2)) {
                                            String vastaanOttajanEmail = kayttajanIdHakuRef.child(jako_sposti2).getKey();
                                            String kaverinId = dataSnapshot.child(jako_sposti2).getValue(String.class); //Haetaan jaettavan ID nimen noutoa varten Jaetussa Activityssä

                                            String jakajanId = dataSnapshot.child(kayttaja_email).getValue(String.class); //Haetaan jakajan ID nimen noutoa varten Jaetussa Activityssä

                                            Toast.makeText(JaettuActivity.this, getString(R.string.jaettu_käyttäjälle) + vastaanOttajanEmail.replace(",", "."), Toast.LENGTH_LONG).show();
                                            jakoRef = database.getReference("Users/" + vastaanOttajanEmail + "/listat");
                                            kaveriRef = database.getReference("Users/" + kayttaja_email + "/listat/" + listaTitteli + "/kaveri");
                                            kaveriRef.child("kaveri").setValue(vastaanOttajanEmail);


                                            jakoRef.child(listaTitteli).setValue(listaTitteli); //Lisätään jaettu lista kaverin listalistaan

                                            kaveriRef2 = database.getReference("Users/" + vastaanOttajanEmail + "/listat/" + listaTitteli + "/jakaja");
                                            kaveriRef2.child("jakaja").setValue(kayttaja_email);





                                            Intent mene = new Intent(JaettuActivity.this, JaettuActivity.class); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
                                            mene.putExtra("key", jako_sposti2);
                                            mene.putExtra("key2", kayttaja_email);
                                            mene.putExtra("key3", listaTitteli);
                                            mene.putExtra("key4", kayttajaNimi);
                                            mene.putExtra("key5", kaverinId);
                                            mene.putExtra("key6", jakajanId);
                                            startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka
                                            finish();
                                        } else {
                                            Toast.makeText(JaettuActivity.this, getString(R.string.käyt_ei_löydy), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /////////////////////////////////////





                    /*
                    Intent mene = new Intent(MainActivity.this, JaettuActivity.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
                    mene.putExtra("key", jako_sposti2);
                    mene.putExtra("key2", )
                    startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka
                    */



                }

            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Voidaan peruuttaa input
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel(); //Suljetaan input-ikkuna jos klikataan "Peruuta"
                }
            });
            builder.show();
            return true;
        }
        ///////////////////////////////////
        if(id == R.id.lisääOstos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.lisaa_ostos));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Otetaan yhteys tietokantaan
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();


                    String key = input.getText().toString(); //Poimitaan text-inputista String
                    if(key.equals(null) || key.equals("")) { //Tsekataan ettei ole tyhjä input (kaatuu muuten)
                        Toast.makeText(getApplicationContext(),getString(R.string.pakkolisata), Toast.LENGTH_LONG).show();
                    } else {
                        key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase(); //Asetetaan annettu String alkavaksi isolla alkukirjaimella (Listan siisteys)

                        myRef.child("Users/" + jakajan_email  +  "/listat/" + listaTitteli + "/ostos").child(key).setValue(key); //Sijoitetaan annettu input tietokantaan polkuun: /ostos/input
                    }

                }
            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Voidaan peruuttaa input
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel(); //Suljetaan input-ikkuna jos klikataan "Peruuta"
                }
            });
            builder.show();
            return true;
        }
        //Listan jaosta poistuminen
        if(id == R.id.action_poistu_jaosta){
            onkoJaettuRef = database.getReference("Users/" + kayttaja_email + "/listat/" + listaTitteli);
            onkoJaettuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("kaveri")) { //Tarkastetaan onko käyttäjä listan jakaja, eikä anneta hänen poistua jos näin on
                        Toast.makeText(JaettuActivity.this, "Et voi poistua itse jakamaltasi listalta!", Toast.LENGTH_SHORT).show();
                    } else {

                        jäsenetRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                nimi = dataSnapshot.getValue(String.class);
                                jäsenet.remove(nimi);
                                nimienLisäysRef.child(nimi).setValue(null);
                                ListatRef = database.getReference("Users/" + kayttaja_email + "/listat");
                                ListatRef.child(listaTitteli).removeValue();
                                Intent mene = new Intent(JaettuActivity.this, ListaListaActivity.class);
                                startActivity(mene);
                                finish();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        //Koko listan tyhjentäminen kerralla
        if(id == R.id.action_clear) { //Jos klikataan valikossa "tyhennä koko lista"..
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //..avataan dialogi-ikkuna
            builder.setTitle(getString(R.string.tyhjenna_lista)); //Asetetaan viesti ikkunaan
            builder.setPositiveButton(getString(R.string.tyhjenna), new DialogInterface.OnClickListener() { //Asetetaan tyhjennynappi
                @Override
                public void onClick(DialogInterface dialog, int which) { //Jos klikataan "Tyhjennä"
                    myRef.setValue(null); //Tyhjennetään annettu tietokantapolku (myref = /ostos), eli kaikki ostokset poistuvat listalta
                }
            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Peruutus-nappi
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); //Klikatessa "Peruuta"-nappia, suljetaan ikkuna
                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_reseptit){ //Jos klikataan valikossa "reseptit"..

            Intent mene = new Intent(JaettuActivity.this, JaetutReseptitActivity.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
            mene.putExtra("key", jakajan_email);
            mene.putExtra("key2", listaTitteli);
            startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka


        }


        return super.onOptionsItemSelected(item);
    }

    public static ArrayList getArrayVal( Context dan) //Listan sisällön luonti, tämä metodi hieman hämärän peitossa itsellä
    {
        SharedPreferences WordSearchGetPrefs = dan.getSharedPreferences("dbArrayValues", Activity.MODE_PRIVATE);
        Set<String> tempSet = new HashSet<String>();
        tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);
        return new ArrayList<String>(tempSet);
    }
    //Tuotteen poisto-metodi
    public void removeElement(final String selectedItem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.poista) + selectedItem + "?");
        builder.setPositiveButton(getString(R.string.poista_), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String listaTeksti =(lvMain.getItemAtPosition(position).toString()); //Poimitaan klikatusta lista-itemistä String
                myRef.child(listaTeksti).removeValue(); //Poistetaan saadun Stringin avulla tietokannasta valittu item, tietokantakuuntelijan kautta päivittyy myös itse listanäkymä
                Toast.makeText(JaettuActivity.this, selectedItem + " " + getString(R.string.poistettu_listalta), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Peruutusnappi
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}