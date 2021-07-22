package com.example.note_trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.note_trial.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    //Creating variables
    FirebaseAuth auth;
    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_register);
        auth= FirebaseAuth.getInstance();
        // Hiding the action bar
        getSupportActionBar().hide();

    }

    public void register(View view) {
        //Here we are creating a string and fetching the data which is entered by the user.
        String m=binding.rmail.getText().toString();
        String p=binding.rpass.getText().toString();
        String rp=binding.rrepass.getText().toString();
        if(m.isEmpty()|p.isEmpty()|rp.isEmpty()){
            Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show();
        }
        else if(!p.equals(rp)){
            binding.rrepass.setError("Not Same");
        }
        else if(p.length()<6){
            binding.rpass.setError("Length should be 6 digits");
        }
        else{
            auth.createUserWithEmailAndPassword(m,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(RegisterActivity.this,NotesActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}