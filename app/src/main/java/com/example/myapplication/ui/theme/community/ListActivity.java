package com.example.myapplication.ui.theme.community;

import com.example.myapplication.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.util.Log;


public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    ListView listView;
    Button reg_button;
    String userid = "";

    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<String> seqList = new ArrayList<>();

    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_main);

        userid = getIntent().getStringExtra("userid");

        listView = findViewById(R.id.listView);

        // Firebase 초기화
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("posts");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ListActivity.this, adapterView.getItemAtPosition(i) + " 클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("board_seq", seqList.get(i));
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        reg_button = findViewById(R.id.reg_button);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, RegisterActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetBoard();
    }

    private void GetBoard() {

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titleList.clear();
                seqList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String title = postSnapshot.child("title").getValue(String.class);
                    String seq = postSnapshot.getKey();

                    titleList.add(title);
                    seqList.add(seq);
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_list_item_1, titleList);
                listView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
