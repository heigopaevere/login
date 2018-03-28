package com.example.opilane.login;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    EditText epost, salasõna;
    Button btn_login;
    TextView katsed, registreeri, unustatud;
    int loendaja = 3;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        epost = findViewById(R.id.epost);
        salasõna = findViewById(R.id.password);
        btn_login = findViewById(R.id.btnLogin);
        unustatud = findViewById(R.id.unustatud);
        registreeri = findViewById(R.id.registreeri);
        katsed = findViewById(R.id.katsed);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseUser kasutaja = firebaseAuth.getCurrentUser();
        if (kasutaja != null){
            finish();
            startActivity(new Intent(loginActivity.this, UserProfileActivity.class));
        }
        /* kui vajutatakse sisse logimis nupule siis läheb tööle valideerimis meetod,
        * võttes parameetriteks kasutaja poolt sisestatud eposti ja salasõna*/
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valideeri(epost.getText().toString(),salasõna.getText().toString());
            }
        });
        registreeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registreerimine = new Intent(loginActivity.this, RegisterActivity.class);
                startActivity(registreerimine);
            }
        });
        unustatud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent unustatud_sala = new Intent(loginActivity.this, PasswordActivity.class);
                startActivity(unustatud_sala);
            }
        });
    }
    // loome funktsiooni valideeri kontrollimaks kas sisselogimis andmed vastavad sellega mis andmebaasis
    private void valideeri(String epost, String salasõna){
// progressdialogi abil anname teada kasutajale et võib aega minna
        progressDialog.setMessage("Andmete edastamisega läheb aega, palun kannatust!");
        progressDialog.show();
        /* üritatakse kasutajat sisse logida: kontrollides kas email ja salasõna vastavad sellele,
         mis on andmebaasis. addoncompletelisteneriga saame teada anda kasutajale kas õnnestus või mitte */
        firebaseAuth.signInWithEmailAndPassword(epost, salasõna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //kui logimine oli edukas siis paneme progressdialogi kinni ja kontrollime kas epost on valideeritud või mitte
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    kontrolliEposti();
                }
                /* anname teada teatega et logime ebaõnnestus, paneme progressdialogi kinni loeme
                sisse logimiskatsest ühe maha */
                else {
                    teade("Sisse logimine ebaõnnestus!");
                    /* kui logimiskatseid on alla 3 siis anname teada kasutajale mitu katset
                    tal veel on jäänud */
                    if (loendaja < 3){
                        katsed.setText("Katseid on jäänud veel " + String.valueOf(loendaja));
                    }
                    /* kui katsete arv on võrdne 0ga, siis lukustame login nupu ning anname
                    textviews teada, et ta peab adminiga ühendust võtma */
                    if (loendaja == 0){
                        btn_login.setEnabled(false);
                        katsed.setText("Võta ühendust administraatoriga");
                    }
                }
            }
        });
    }
    // funktsioon kontrollimkas kas eposti kinnitus on sooritatud või mitte
    private void kontrolliEposti(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        // kui eposti kinnitus on sooritatud siis avaneb uus tegevus
        boolean epostiKontroll = firebaseUser.isEmailVerified();
        if (epostiKontroll){
            finish();
            startActivity(new Intent(loginActivity.this, UserProfileActivity.class));
        }
        // kui mitte siis tuleb teade et on vaja kinnitada oma epost
        else{
            teade("Kinnitage oma eposti aadress!");
        }
    }
    // funktsioon teadete kuvamiseks Toast abil
    public void teade(String message){
        //standard teatestruktuur
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    }
