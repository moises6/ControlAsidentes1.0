package com.example.controlaccidentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class Register extends AppCompatActivity {
    EditText fullName, email, password, phone;
    Button registerBtn, goToLogin;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    CheckBox administrador,empleado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.fondoRegistrar);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        administrador = findViewById(R.id.administrador);
        empleado = findViewById(R.id.empleado);

        // verificar los checkBox por rol
        empleado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    administrador.setChecked(false);
                }
            }
        });
        administrador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    empleado.setChecked(false);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                // checkBox validation
                if(!(administrador.isChecked() ||  empleado.isChecked()) ){
                    Toast.makeText(Register.this, "Selecione un tipo de cuenta", Toast.LENGTH_SHORT).show();
                    return;

                }


                if(valid){
                    // inicio de registro de usuario
                    fAuth.createUserWithEmailAndPassword(String.valueOf(email.getText()),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fstore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Nombre",fullName.getText().toString());
                            userInfo.put("Correo",email.getText().toString());
                            userInfo.put("Telefono",phone.getText().toString());
                            userInfo.put("Contraseña",password.getText().toString());

                            //Especificar si el usuario es administrador o empleado
                            if(administrador.isChecked()){
                                userInfo.put("Administrador","1");
                            }
                            if (empleado.isChecked()){
                                userInfo.put("Empleado", "0");
                            }
                            df.set(userInfo);

                            //Direcionando activity correcta para cada rol
                            if(administrador.isChecked()){
                                startActivity(new Intent(getApplicationContext(),Admin.class));
                                finish();
                            }
                            if(empleado.isChecked()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }




                        }
                    }).addOnFailureListener(new OnFailureListener(){

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "No se acreado la cuenta", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }
}