package com.example.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {
    TextView textView7;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        textView7 = findViewById(R.id.textView7);
        textView7.setText(getIntent().getStringExtra("key1"));
        listView = findViewById(R.id.listView);

        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity3.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(textView7.getText().toString());//Διάβασμα απ την βάση
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    arrayList.add(snapshot1.getValue().toString());//Εμφάνιση δεδομένων του επιλεγμένου παιδιού στο listview
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void back(View view){//Επιστροφή στην προηγούμενη οθόνη
        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
        startActivity(intent);
    }

    public void delete(View view){//Διαγραφή του επιλεγμένου παιδιού της βάσης
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child(textView7.getText().toString()).child(currentuser).removeValue();
        Toast.makeText(MainActivity3.this,R.string.toastdel, Toast.LENGTH_LONG).show();
    }
}