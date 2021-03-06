package andorid.appnotas.com.appnotas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button btregistrar;
    private Button btentrar;
    private EditText txtemail;
    private EditText txtpasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btregistrar= (Button) findViewById(R.id.btregistrar);
        btentrar= (Button) findViewById(R.id.btentrar);
        txtemail= (EditText) findViewById(R.id.txtmail);
        txtpasswd= (EditText) findViewById(R.id.txtpasswd);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MainAcitvity", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("MainActivity", "onAuthStateChanged:signed_out");
                }
            }
        };

        btregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.createUserWithEmailAndPassword(txtemail.getText().toString().trim(), txtpasswd.getText().toString().trim())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("OnComplete Registro", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Error en autenticcacion. "+task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    System.out.println(task.getException().getMessage());
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Registro Exitoso ",
                                            Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(!task.isSuccessful()){
                                                        Toast.makeText(MainActivity.this, "Error al enviar email. "+task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                        System.out.println(task.getException().getMessage());
                                                    }else{

                                                        Toast.makeText(MainActivity.this, "debe validar su email",
                                                                Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                    txtemail.setText("");
                                    txtpasswd.setText("");

                                }

                                // ...
                            }
                        });
            }
        });

        btentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(txtemail.getText().toString().trim(), txtpasswd.getText().toString().trim())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("Inicio Sesion", "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("Inicio sesion tarea", "signInWithEmail", task.getException());
                                    Toast.makeText(MainActivity.this, "Inicio de sesion Fallida: "+task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.w("Inicio sesion tarea", "signInWithEmail", task.getException());

                                    if(mAuth.getCurrentUser().isEmailVerified()){

                                        /**/
                                        Intent intent = new Intent(MainActivity.this, SegundaActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this, "Debe Validar Email",
                                                Toast.LENGTH_SHORT).show();
                                    }


                                }

                                // ...
                            }
                        });
            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
