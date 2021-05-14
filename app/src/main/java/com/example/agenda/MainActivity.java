package com.example.agenda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private FirebaseAuth mAuth;
    EditText editText1,editText2,editText3;
    FirebaseUser currentUser;
    private static final int REC_RESULT = 653;
    TextView textView5;
    LocationManager locationManager;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        editText1 = findViewById(R.id.editTextTextPersonName);
        editText2 = findViewById(R.id.editTextTextPassword);
        editText3 = findViewById(R.id.editTextTextPersonName2);
        textView5 = findViewById(R.id.textView5);
        db = openOrCreateDatabase("LocationDB", Context.MODE_PRIVATE,null);//Δημιουργία βάσης SQLite
        db.execSQL("CREATE TABLE IF NOT EXISTS Locations(user_id TEXT,user_loc TEXT)");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//Εντοπιαμός τοποθεσίας
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)//Αίτηση άδειας χρήσης τοποθεσίας συσκευής
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                this);
        //locationManager.removeUpdates(this);
    }

    public void signup(View view){//Εγγραφή νέου χρήστη
        mAuth.createUserWithEmailAndPassword(editText1.getText().toString(),editText2.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,R.string.toastSS, Toast.LENGTH_LONG).show();
                            currentUser = mAuth.getCurrentUser();
                            addUsernameToUser(editText3.getText().toString(),currentUser);
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void signin(View view){//Εισαγωγή χρήστη στο σύστημα
        mAuth.signInWithEmailAndPassword(editText1.getText().toString(),editText2.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();
                            String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();//Εύρεση id του χρήστη που έχει μπει στην εφαρμογή
                            String loc = textView5.getText().toString();
                            db.execSQL("INSERT INTO Locations VALUES('"+currentuser+"','"+loc+"')");//Γράψιμο στον πίνακα της βάσης
                            Toast.makeText(MainActivity.this,R.string.toastLS, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity2.class);//Εμφάνιση νέας οθόνης
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addUsernameToUser(String username, FirebaseUser user){//Εισαγώγη πεδίου ονόματος στον χρήστη
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,R.string.toastUU, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editText3.setText(matches.get(0));
        }

    }

    public void recognize(View view){//Φωνητική εισάγωγη ονόματος
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"");
        startActivityForResult(intent,REC_RESULT);
    }

    @Override
    public void onLocationChanged(Location location) {
        double  x = location.getLatitude();//Εύρεση γεωγραφικής συντεταγμένης Χ
        double y = location.getLongitude();//Εύρεση γεωγραφικής συντεταγμένης Υ
        textView5.setText(String.valueOf(x)+","+String.valueOf(y));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}