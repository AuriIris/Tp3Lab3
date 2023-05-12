package com.example.loginsharedpreferences.ui.registro;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginsharedpreferences.model.Usuario;
import com.example.loginsharedpreferences.request.ApiClient;
import com.example.loginsharedpreferences.ui.login.MainActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RegistroViewModel extends AndroidViewModel {
    private Context context;
    MutableLiveData<Usuario>usuario;
    private MutableLiveData<Bitmap> foto;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
        context=application.getApplicationContext();
    }

    public LiveData<Usuario> getUsuario(){
        if(usuario==null){
            usuario= new MutableLiveData<>();
        }
        return usuario;
    }

    public void obtenerDatos(Intent intent){
        Usuario usr= ApiClient.leer(context);
        usuario.setValue(usr);
    }

    public LiveData<Bitmap> getFoto() {
        if (foto == null) {
            foto = new MutableLiveData<>();
        }
        return foto;
    }
    public void guardarRegistro(Usuario usuario) {
        Log.d("Usuario: " , usuario+"");
        ApiClient.guardar(context,usuario);
    }

    public void cargar() {
        File archivo =new File(context.getFilesDir(),"foto1.png");

        Bitmap imageBitmap= BitmapFactory.decodeFile(archivo.getAbsolutePath());
        if(imageBitmap!=null) {

            foto.setValue(imageBitmap);
        }
    }
    public void respuetaDeCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE){
        Log.d("salida",requestCode+"");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Recupero los datos provenientes de la camara.
            Bundle extras = data.getExtras();
            //Casteo a bitmap lo obtenido de la camara.
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Rutina para optimizar la foto,
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            foto.setValue(imageBitmap);



            //Rutina para convertir a un arreglo de byte los datos de la imagen
            byte [] b=baos.toByteArray();


            //Aquí podría ir la rutina para llamar al servicio que recibe los bytes.
            File archivo =new File(context.getFilesDir(),"foto1.png");
            if(archivo.exists()){
                archivo.delete();
            }
            try {
                FileOutputStream fo=new FileOutputStream(archivo);
                BufferedOutputStream bo=new BufferedOutputStream(fo);
                bo.write(b);
                bo.flush();
                bo.close();
                Intent segunda=new Intent(context,RegistroActivity.class);
                segunda.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(segunda);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}