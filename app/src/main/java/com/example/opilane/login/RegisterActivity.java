package com.example.opilane.login;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText eesNimi, perekonnaNimi, epost, salasõna;
    Button btn_registreeri, btn_pilt;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String _eesNimi, _perekonnaNimi, _epost, _salasõna;
    ImageView profiiliPilt;
    private StorageReference storageReference;
    private static final int CAMERA_REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profiiliPilt = findViewById(R.id.profile_pic);
        btn_pilt = findViewById(R.id.btnTeePilt);
        eesNimi = findViewById(R.id.eesNimi);
        perekonnaNimi = findViewById(R.id.pereNimi);
        epost = findViewById(R.id.epost);
        salasõna = findViewById(R.id.password);
        btn_registreeri = findViewById(R.id.btnRegistreeri);
        progressDialog = new ProgressDialog(this);
        //tegu on peamise klassi objektiga, saame firebase instance'i meie muutujasse
        firebaseAuth = FirebaseAuth.getInstance();
        // storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        btn_pilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pilt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pilt,CAMERA_REQUEST_CODE);
            }
        });
        btn_registreeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valideeri()) {
                    //laadi andmed andmebaasi
                    String k_epost = epost.getText().toString().trim();
                    String k_salasõna = salasõna.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(k_epost, k_salasõna)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.setMessage("Andmete edastamisega läheb aega, palun kannatust!");
                                        progressDialog.show();
                                        pildiUpload();
                                        saadaEpostiKinnitus();
                                }

                                    else {
                                        //teeme erandi juhuks kui selline email on juba registreeritud
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            Bitmap bitmap =(Bitmap)data.getExtras().get("data");
            profiiliPilt.setImageBitmap(bitmap);
        }
    }
    //üleslaadimise protsess
    private void pildiUpload(){
        progressDialog.setMessage("Pildi üleslaadimine");
        progressDialog.show();
        StorageReference pathReference = storageReference.child("pildid/profiiliPilt.jpg");
        profiiliPilt.setDrawingCacheEnabled(true);
        profiiliPilt.buildDrawingCache();
        Bitmap bitmap = profiiliPilt.getDrawingCache();
        ByteArrayOutputStream baitOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baitOS);
        byte[] data = baitOS.toByteArray();
        UploadTask uploadTask = pathReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Picasso.get().load(downloadUrl).fit().centerCrop().into(profiiliPilt);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                teade("Pilti ei laetud ülesse");
            }
        });
    }

    // valideerimine ehk saame andmed ning kontrollime et väljad oleksid täidetud
    private boolean valideeri() {
        // tulemus false
        boolean tulemus = false;
        // omistame kasutaja poolsed andmed muutujatele
        _eesNimi = eesNimi.getText().toString();
        _perekonnaNimi = perekonnaNimi.getText().toString();
        _epost = epost.getText().toString();
        _salasõna = salasõna.getText().toString();
        // kui mõni väli on jäänud täitmata siis anname sellest teada teatega
        if (_eesNimi.isEmpty() || _perekonnaNimi.isEmpty() || _epost.isEmpty() || _salasõna.isEmpty()) {
            teade("Täida kõik väljad!");
        }
        // kui kõik on täidetud siis on meie tulemus true
        else {
            tulemus = true;
        }
        // tagastan tulemuse
        return tulemus;
    }

    // funktsioon eposti kinnituse saatmiseks
    private void saadaEpostiKinnitus() {
        // siin ei ole vaja kasutada getinstance kuna meil on uus kasutaja keda me tahame registreerida
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    /* kui õnnestus siis anname selle kohta teate, logime firebaseauth välja ja
                    * lõpetametegevuse töö ning käivitame uue tegevuse*/
                    if (task.isSuccessful()) {
                        //saadame kasutaja sisestatud andmed firebasedatabase'i
                        saadaKasutajaAndmed();
                        teade("Registreerimine õnnestus, teile saadeti kinnitus email!");
                        finish();
                        firebaseAuth.signOut(); //logid välja, et saaksid valideerida ennast ning uuesti sisse logida siis
                        startActivity(new Intent(RegisterActivity.this, loginActivity.class));
                    } else {
                        teade("Kinnitus emaili ei saadetud!");
                    }
                }
            });
        }
    }

    // kasutaja andmete saatmiseks
    private void saadaKasutajaAndmed() {
        //loome muutuja millega saame ligi firebasedatabase klassile
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        /* loome muutuja andmebaasi referencile, iga kasutaja saam unikaalse userid. sellega saame
         iga kasutaja andmed */
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserProfileData userProfileData = new UserProfileData(_eesNimi, _perekonnaNimi, _epost);
        databaseReference.setValue(userProfileData);
    }

    // funktsioon teadete kuvamiseks Toast abil
    public void teade(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

