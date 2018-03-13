package com.example.jusju.carapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText etUserName = (EditText) findViewById(R.id.etUserName);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        final Button btSignIn = (Button) findViewById(R.id.btSignIn);
        final Context thisContext = this.getApplicationContext();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();

                final String authToken = username + ":" + password;
                final String base = Base64.encodeToString(authToken.getBytes(), Base64.NO_WRAP);

                PassiClient service =
                        ServiceGenerator.createService(PassiClient.class, username, password);
                Call<User> call = service.haeKayttaja(username);
                doLogin(username, password);
            }
        });
    }

    public void doLogin(String username, String password) {


        final int RESULT_NOT_FOUND = 401;

        final String authToken = username + ":" + password;
        final String base = Base64.encodeToString(authToken.getBytes(), Base64.NO_WRAP);

        PassiClient service =
                ServiceGenerator.createService(PassiClient.class, username, password);
        Call<User> call = service.haeKayttaja(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String text;


                if (response.isSuccessful()) {
                    User k = response.body();

                    SharedPreferences mySharedPreferences = getSharedPreferences("konfiguraatio", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putString("tunnus", k.getUsername());
                    editor.apply();
                    editor.putString("token", base);
                    editor.apply();
                    editor.putString("userID", k.getUserID());
                    editor.apply();

                    onLoginSuccess();
                } else if (response.code() == RESULT_NOT_FOUND) {
                    text = "Salasana tai käyttäjänimi väärin";
                    onLoginFailed(text);
                } else { // Jokin muu virhe
                    text = "Virhe tietojen haussa";
                    FirebaseCrash.log("Failed login attempt");
                    onLoginFailed(text);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Passi", "Virhe kirjautumisessa " + t.toString());
            }
        });
    }


    public void onLoginSuccess() {
        Context context = getApplicationContext();
        Toast toast;
        int duration = Toast.LENGTH_LONG;
        toast = Toast.makeText(context, "succesful login", duration);
        toast.show();
        Intent successIn = new Intent(MainActivity.this, SignedInAlreadyActivity.class);
        startActivity(successIn);
    }

    public void onLoginFailed(String errorMessage) {
        Context context = getApplicationContext();
        Toast toast;
        int duration = Toast.LENGTH_LONG;
        toast = Toast.makeText(context, errorMessage, duration);
        toast.show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

