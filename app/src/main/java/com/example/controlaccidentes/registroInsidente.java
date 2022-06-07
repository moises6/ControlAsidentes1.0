package com.example.controlaccidentes;

import static java.util.Calendar.DAY_OF_MONTH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class registroInsidente extends AppCompatActivity {
    EditText editTextFecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.registreAccident);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Button cerrarEmpleado = findViewById(R.id.RegresarEmpleado);
        cerrarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });


        setContentView(R.layout.activity_registro_insidente);
        editTextFecha = findViewById(R.id.editTimefecha);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener  date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar() {
                String Format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);

                editTextFecha.setText(sdf.format(calendar.getTime()));
            }
        };
        editTextFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(registroInsidente.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(DAY_OF_MONTH)).show();

            }
        });
    }


}