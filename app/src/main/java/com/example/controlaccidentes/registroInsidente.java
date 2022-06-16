package com.example.controlaccidentes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class registroInsidente extends AppCompatActivity {

    ImageView imgContainer;
    EditText tipo,descripcion;
    Button btn_agregar, btn_add;
    String Images_Directory;
    String File_Name;
    Uri file;
    File Image_Path;
    EditText editTextFecha, editTipo, editDescripcion;
    ImageView imagen;

    private static final int REQUEST_CODE_CAMERA = 200;
    private static final int REQUEST_CODE_TAKE_PICTURE = 300;

    private FirebaseDatabase database;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_insidente);
        tipo = findViewById(R.id.tipo);
        descripcion = findViewById(R.id.descripcion);
        btn_add = findViewById(R.id.btn_add);


        imgContainer=findViewById(R.id.imagen);
        btn_agregar = findViewById(R.id.btn_agregar);
        btn_agregar.setOnClickListener(view -> {
            Intent Gallery = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Gallery, 1);

            auth = FirebaseAuth.getInstance();

            mProgressDialog = new ProgressDialog(registroInsidente.this);


        });
        FirebaseApp.initializeApp(this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarView();
                String type, description, errores;

                type = tipo.getText().toString();
                description = descripcion.getText().toString();



                errores = validaciones(type, description);

                if (errores == null) {
                    crearIncidente(type, description);
                } else {
                    Toast.makeText(registroInsidente.this, errores, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void Take_Picture(){
        Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Camera.resolveActivity(getPackageManager()) != null){
            File Picture = Create_File();
            if(Picture != null){
                Uri Picture_Path = FileProvider.getUriForFile(
                        registroInsidente.this,
                        "net.irivas.incidentsapp", Picture);
                Camera.putExtra(MediaStore.EXTRA_OUTPUT, Picture_Path);
                Images_Directory = Picture_Path.toString();
                file = Picture_Path;
                startActivityForResult(Camera, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void Check_Permissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            if(ActivityCompat.checkSelfPermission(
                    registroInsidente.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                Take_Picture();
            }else{
                ActivityCompat.requestPermissions(registroInsidente.this,
                        new String[]{
                                Manifest.permission.CAMERA
                        }, REQUEST_CODE_CAMERA);
            }
        }else{
            Take_Picture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResult){
        if(requestCode == REQUEST_CODE_CAMERA){
            if(permissions.length > 0
                    && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                Take_Picture();
            }else{
                Toast.makeText(registroInsidente.this, "Permission required", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                imgContainer.setImageURI(Uri.parse(Images_Directory));
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            file = uri;
            imgContainer.setImageURI(uri);
            Images_Directory = getRealPathFromUri(uri, registroInsidente.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File Create_File(){
        String date_format = new SimpleDateFormat("yyyyMMdd_HHmm_ss",
                Locale.getDefault()).format(new Date());
        File_Name = "AppCam" + date_format + "_";
        File Image = null;
        Image_Path  = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            Image = File.createTempFile(File_Name, ".jpeg", Image_Path);
        }catch (IOException e){
            e.printStackTrace();
        }

        return Image;
    }

    public String getRealPathFromUri(Uri uri, Activity context){
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null,null);
        if(cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if(cursor.moveToFirst()){
            String string = cursor.getString(column_index);
            return string;
        }
        return null;
    }
    public void cargarView(){
        Intent ventana = new Intent(registroInsidente.this, ReporteInsidencias.class);
        startActivity(ventana);
        this.finish();
    }

    public void crearIncidente(String type, String description) {

        if(!File_Name.equals("")){
            storageReference.child("image/"+Images_Directory).putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl =  taskSnapshot.getStorage().getDownloadUrl().getResult();
                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                }
            });
        }




    }

    public String validaciones(String type, String description) {

        if (type.trim().equals("")) {
            return "campo tipo no puede quedar vacio";
        } else if (description.trim().equals("")) {
            return "La description no puede quedar vacio";
        }
        return null;
    }
}



