package com.example.loginsharedpreferences.ui.registro;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loginsharedpreferences.R;
import com.example.loginsharedpreferences.databinding.ActivityRegistroBinding;
import com.example.loginsharedpreferences.model.Usuario;
import com.example.loginsharedpreferences.request.ApiClient;
import com.example.loginsharedpreferences.ui.login.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegistroActivity extends AppCompatActivity {
    private ActivityRegistroBinding binding;
    private RegistroViewModel vm;

    private ImageView imageViewFotoPerfil;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vm = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroViewModel.class);
        vm.getUsuario().observe(this, usuario -> {
            try {
                binding.editTextDni.setText(usuario.getDni() + "");
                binding.editTextNombre.setText(usuario.getNombre());
                binding.editTextApellido.setText(usuario.getApellido());
                binding.editTextEmail.setText(usuario.getEmail());
                binding.editTextPassword.setText(usuario.getPass());
            } catch (Exception e) {
                Toast.makeText(this, "Puede registrarse", Toast.LENGTH_SHORT).show();
            }
        });
        vm.obtenerDatos(getIntent());
        vm.getFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                try {
                    imageViewFotoPerfil.setImageBitmap(bitmap);

                } catch (Exception e) {

                }
            }
        });
        vm.cargar();
        configView();


        binding.buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dni = Long.parseLong(binding.editTextDni.getText().toString());
                String nombre = binding.editTextNombre.getText().toString();
                String apellido = binding.editTextApellido.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String pass = binding.editTextPassword.getText().toString();
                Usuario usuario = new Usuario(dni, nombre, apellido, email, pass);
                vm.guardarRegistro(usuario);
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void tomarFoto(View v){
//startActivityForResult es otra forma de iniciar una activity, pero esperando desde donde la llamé un resultado
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("paz","estoy fuera del if"+takePictureIntent.resolveActivity(getPackageManager()));
        /*if (takePictureIntent.resolveActivity(getPackageManager()) == null) {*/
            Log.d("paz","estoy en el if");
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    //Este método es llamado automáticamente cuando retorna de la cámara.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        vm.respuetaDeCamara(requestCode,resultCode,data,REQUEST_IMAGE_CAPTURE);
    }



    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if((shouldShowRequestPermissionRationale(CAMERA))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{CAMERA},100);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){

            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistroActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{CAMERA},100);
            }
        });
        dialogo.show();
    }
    public void configView(){
        imageViewFotoPerfil=findViewById(R.id.fotoLeer);
        validaPermisos();
        vm.getFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imageViewFotoPerfil.setImageBitmap(bitmap);
            }
        });
    }
}