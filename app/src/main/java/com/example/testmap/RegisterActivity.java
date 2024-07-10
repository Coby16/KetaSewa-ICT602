package com.example.testmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText e1, e2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = findViewById(R.id.editText);
        e2 = findViewById(R.id.editText2);

        mAuth = FirebaseAuth.getInstance();
    }

    public void createUser(View v){
        if(e1.getText().toString().isEmpty() || e2.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),"Blank not allowed", Toast.LENGTH_SHORT).show();
        } else {
            String email = e1.getText().toString();
            String password = e2.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"User created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(),"User could not be created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
