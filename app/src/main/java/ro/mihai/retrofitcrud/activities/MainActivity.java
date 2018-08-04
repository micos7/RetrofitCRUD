package ro.mihai.retrofitcrud.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.mihai.retrofitcrud.models.DefaultResponse;
import ro.mihai.retrofitcrud.R;
import ro.mihai.retrofitcrud.api.RetrofitClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextMail, editTextPassword, editTextName, editTextSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextSchool = findViewById(R.id.editTextSchool);

        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
    }

    private void userSignup(){
        String email = editTextMail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();

    // Validation
        if(email.isEmpty()){
            editTextMail.setError("Email is required!");
            editTextMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextMail.setError("Enter a valid email!");
            editTextMail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()< 6){
            editTextPassword.setError("Password must be at least 6 charaters");
            editTextPassword.requestFocus();
            return;
        }

        if(name.isEmpty()){
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

        if(school.isEmpty()){
            editTextSchool.setError("School is required!");
            editTextSchool.requestFocus();
            return;
        }

        Call<DefaultResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .createUser(email, password, name, school);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                if(response.code() == 201) {
                    DefaultResponse dr = response.body();
                    Toast.makeText(MainActivity.this, dr.getMsg(), Toast.LENGTH_SHORT).show();
                }else if(response.code() == 422 ){
                    Toast.makeText(MainActivity.this, "User already exist!", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSignUp:
                userSignup();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
