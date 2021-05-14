package com.example.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    EditText editText4,editText5,editText6,editText7,editText8;
    TextView textView6;
    CalendarView calendarView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editText4 = findViewById(R.id.editTextTextPersonName3);
        editText5 = findViewById(R.id.editTextTextPersonName4);
        editText6 = findViewById(R.id.editTextTextPersonName5);
        editText7 = findViewById(R.id.editTextTextPersonName6);
        editText8 = findViewById(R.id.editTextTextPersonName7);
        textView6 = findViewById(R.id.textView6);
        calendarView2 = findViewById(R.id.calendarView2);
        calendarView2.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                textView6.setText(String.valueOf(dayOfMonth)+" "+String.valueOf(month+1)+" "+String.valueOf(year));
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(textView6.getText().toString())) {//Αν υπάρχει παιδί στην βάση με το όνομα της επιλεγμένης ημερομηνίας που εγγράγηκε μέσω του textview
                            Intent intent = new Intent(getApplicationContext(),MainActivity3.class);
                            intent.putExtra("key1", textView6.getText().toString());
                            startActivity(intent);//Εμφάνιση νέας οθόνης
                        } else{
                            Toast.makeText(MainActivity2.this,R.string.toastNI, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void write(View view){//Γράψιμο στην βάση
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();//userid του χρήστη
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();//email του χρήστη
        FirebaseDatabase.getInstance().getReference().child(textView6.getText().toString()).child(currentuser).setValue(user+": "+editText4.getText().toString()+" "+editText5.getText().toString()+" "+editText6.getText().toString()+" "+editText7.getText().toString()+" "+editText8.getText().toString());//Εγγραφή στην βάση
        Toast.makeText(MainActivity2.this,R.string.dn, Toast.LENGTH_LONG).show();
        editText4.setText("");
        editText5.setText("");
        editText6.setText("");
        editText7.setText("");
        editText8.setText("");
    }
}