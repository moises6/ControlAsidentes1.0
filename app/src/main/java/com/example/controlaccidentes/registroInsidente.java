package com.example.controlaccidentes;

import static java.util.Calendar.DAY_OF_MONTH;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.controlaccidentes.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;


public class registroInsidente extends AppCompatActivity {
    EditText editTextFecha;
    private ImageView imagepic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ActivityMainBinding binding;
    
    
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_insidente);
        imagepic = findViewById(R.id.firebaseimage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        




        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.selectImagebtn);
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


        binding.selectImagebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            selectImage();


        }
    });

        binding.uploadimagebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            uploadImage();

        }
    });

}

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        binding.firebaseimage.setImageURI(null);
                        Toast.makeText(registroInsidente.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(registroInsidente.this,"Failed to Upload",Toast.LENGTH_SHORT).show();


                    }
                });

    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            binding.firebaseimage.setImageURI(imageUri);


        }
    }
}


