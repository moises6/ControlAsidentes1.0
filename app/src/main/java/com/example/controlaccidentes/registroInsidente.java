package com.example.controlaccidentes;

import static java.util.Calendar.DAY_OF_MONTH;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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