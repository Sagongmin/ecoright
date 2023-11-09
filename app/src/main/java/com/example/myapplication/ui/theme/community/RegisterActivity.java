package com.example.myapplication.ui.theme.community;
import com.example.myapplication.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    final private String TAG = getClass().getSimpleName();

    EditText title_et, content_et;
    Button reg_button;

    String userid = "";

    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_register);

        userid = getIntent().getStringExtra("userid");

        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);

        // Firebase 초기화
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("posts");

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegBoard regBoard = new RegBoard();
                regBoard.execute(userid, title_et.getText().toString(), content_et.getText().toString());
            }
        });
    }

    class RegBoard extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("success")) {
                Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String userid = params[0];
            String title = params[1];
            String content = params[2];

            try {
                String postId = databaseRef.push().getKey();
                databaseRef.child(postId).child("userid").setValue(userid);
                databaseRef.child(postId).child("title").setValue(title);
                databaseRef.child(postId).child("content").setValue(content);

                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }
}
