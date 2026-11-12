package com.example.statusapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.statusapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progress;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        // Sessione già attiva: salta il login
        if (auth.getCurrentUser() != null) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progress = findViewById(R.id.progress);
        errorText = findViewById(R.id.errorText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Inserisci un'email valida");
            return;
        }
        if (password.isEmpty()) {
            showError("Inserisci la password");
            return;
        }

        setLoading(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        openMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        setLoading(false);
                        showError(mapAuthError(e));
                    }
                });
    }

    private String mapAuthError(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException
                || e instanceof FirebaseAuthInvalidUserException) {
            return "Credenziali non valide";
        }
        if (e instanceof FirebaseNetworkException) {
            return "Nessuna connessione, riprova";
        }
        return "Errore di accesso, riprova";
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        if (loading) errorText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void openMain() {
        startActivity(new Intent(this, ScanActivity.class));
        finish();
    }
}