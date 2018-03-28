package com.example.opilane.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class UserProfileActivity extends AppCompatActivity {

    TextView profiil_eesnimi, profiil_perekonnanimi, profiil_epost;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.välja:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, loginActivity.class));
                return true;
            case R.id.kasutaja:
                finish();
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profiil_eesnimi = findViewById(R.id.profiilEesNimi);
        profiil_perekonnanimi = findViewById(R.id.profiilPerekonnaNimi);
        profiil_epost = findViewById(R.id.profiilEpost);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // saame firebase userid-ga kätte andmed
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        // attach a listener to read the data at our posts reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfileData userProfileData = dataSnapshot.getValue(UserProfileData.class);
                profiil_eesnimi.setText(userProfileData.getEesNimi());
                profiil_perekonnanimi.setText(userProfileData.getPerekonnaNimi());
                profiil_epost.setText(userProfileData.getEpost());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this,databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
