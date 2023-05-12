package com.example.loginsharedpreferences.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.loginsharedpreferences.model.Usuario;

import java.io.*;


    public class ApiClient {

        private static SharedPreferences sp;

        private static SharedPreferences conectar (Context context) {
            if (sp == null) {
                Log.d("paz","entro");
                sp = context.getSharedPreferences("datos", 0);
            }
            Log.d("paz","entro"+ sp);
            return sp;
        }

        public static void guardar(Context context, Usuario usuario) {
            SharedPreferences sp = conectar(context);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong("dni", usuario.getDni());
            editor.putString("apellido", usuario.getApellido());
            editor.putString("nombre", usuario.getNombre());
            editor.putString("mail", usuario.getEmail());
            editor.putString("pass", usuario.getPass());
            editor.commit();
        }

        public static Usuario leer(Context context) {
            SharedPreferences sp = conectar (context);
            Long dni = sp.getLong("dni", -1);
            String apellido = sp.getString("apellido", "-1");
            String nombre = sp.getString("nombre", "-1");
            String email = sp.getString("mail", "-1");
            String pass = sp.getString("pass", "-1");
            Usuario usuario = new Usuario(dni, apellido, nombre, email, pass);
            return usuario;
        }



        public static Usuario login(Context context, String mail, String pass) {
            Usuario usuario = null;
            SharedPreferences sp = conectar(context);
            Long dni = sp.getLong("dni", -1);
            String apellido = sp.getString("apellido", "-1");
            String nombre = sp.getString("nombre", "-1");
            String email = sp.getString("mail", "-1");
            String passw = sp.getString("pass", "-1");
            Log.d("paz", mail+" "+email+" "+pass+" "+passw);
            if (email.equals(mail) && passw.equals(pass)) {
                usuario = new Usuario(dni, apellido, nombre, email, pass);
            }
            return usuario;
        }
    }