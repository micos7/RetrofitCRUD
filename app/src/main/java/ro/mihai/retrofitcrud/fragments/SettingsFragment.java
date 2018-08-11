package ro.mihai.retrofitcrud.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Callback;
import retrofit2.Response;
import ro.mihai.retrofitcrud.R;
import retrofit2.Call;
import ro.mihai.retrofitcrud.activities.LoginActivity;
import ro.mihai.retrofitcrud.activities.MainActivity;
import ro.mihai.retrofitcrud.activities.ProfileActivity;
import ro.mihai.retrofitcrud.api.RetrofitClient;
import ro.mihai.retrofitcrud.models.DefaultResponse;
import ro.mihai.retrofitcrud.models.LoginResponse;
import ro.mihai.retrofitcrud.models.User;
import ro.mihai.retrofitcrud.storage.SharedPrefManager;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private EditText editTextName, editTextEmail, editTextSchool;
    private EditText editTextcurrentPassword, editTextNewPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextName = view.findViewById(R.id.editTextName);
        editTextSchool = view.findViewById(R.id.editTextSchool);
        editTextcurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

        view.findViewById(R.id.buttonSave).setOnClickListener(this);
        view.findViewById(R.id.buttonChangePassword).setOnClickListener(this);
        view.findViewById(R.id.buttonLogout).setOnClickListener(this);
        view.findViewById(R.id.buttonDelete).setOnClickListener(this);
    }

    private void updateProfile() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }


        if (name.isEmpty()) {
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

        if (school.isEmpty()) {
            editTextSchool.setError("School is required!");
            editTextSchool.requestFocus();
            return;
        }

        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        Call<LoginResponse> call = RetrofitClient.getInstance()
                .getApi().updateUser(
                        user.getId(), email, name, school
                );

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                if (!response.body().isError()) {
                    SharedPrefManager.getInstance(getActivity()).saveUser(response.body().getUser());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    private void updatePassword() {
        String currentpassword = editTextcurrentPassword.getText().toString().trim();
        String newpassword = editTextNewPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (currentpassword.isEmpty()) {
            editTextcurrentPassword.setError("Password required!");
            editTextcurrentPassword.requestFocus();
            return;
        }

        if (newpassword.isEmpty()) {
            editTextNewPassword.setError("New Password required!");
            editTextNewPassword.requestFocus();
            return;
        }


        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                .updatePassword(currentpassword, newpassword, user.getEmail());

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });
    }

    private void logout() {
        SharedPrefManager.getInstance(getActivity()).clear();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void deleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?");
        builder.setMessage("This action is irreversible!");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                User user = SharedPrefManager.getInstance(getActivity()).getUser();
                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                        .deleteUser(user.getId());

                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(!response.body().isErr()){
                            SharedPrefManager.getInstance(getActivity()).clear();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSave:
                updateProfile();
                break;
            case R.id.buttonChangePassword:
                updatePassword();
                break;
            case R.id.buttonLogout:
                logout();
                break;
            case R.id.buttonDelete:
                deleteUser();
                break;
        }
    }
}
