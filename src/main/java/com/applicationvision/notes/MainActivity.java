package com.applicationvision.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    File[] directoryListing;
    File dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web1 = findViewById(R.id.webview1);
        web1.getSettings().setJavaScriptEnabled(true);
        web1.loadUrl("file:///android_asset/Template/index.html");
        web1.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!title.isEmpty()){
                    web1.loadUrl("javascript:Do1('"+title+"','"+body+"');");
                    Toast.makeText(MainActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    title="";
                    body="";
                }
                if(!title2.isEmpty()){
                    body2 = readFromFile(MainActivity.this, title2);
                    web1.loadUrl("javascript:Modify('"+title2+"','"+body2+"');");
                    title2 = "";
                    body2 = "";
                }
                if(Hide){
                    Hide = false;
                    web1.loadUrl("javascript:go = true; HideNote();");
                }
                if(!txt.isEmpty()){
                    dir = MainActivity.this.getFilesDir();
                    directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File child : directoryListing) {
                            name = child.getName();
                            if (name.indexOf(".") > 0)
                                name = name.substring(0, name.lastIndexOf("."));
                            if(name.toLowerCase().indexOf(txt.toLowerCase()) != -1
                            || readFromFile(MainActivity.this, name)
                                    .toLowerCase().indexOf(txt.toLowerCase()) != -1){
                                web1.loadUrl("javascript:AddCard('"+name+"', '"+
                                        readFromFile(MainActivity.this, name)+"');");
                            }
                        }
                    }
                    txt = "";
                }
                if(!txt_delete.isEmpty()){
                    myfile = new File(getFilesDir(), txt_delete+".txt");
                    if(myfile.exists()){
                        myfile.delete();
                        web1.loadUrl("javascript:RestartApp();");
                        Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
                    }
                    txt_delete = "";
                }
                new Handler().postDelayed(this, 100);
            }
        }, 100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(start){
                    start = false;
                    dir = MainActivity.this.getFilesDir();
                    directoryListing = dir.listFiles();
                    if (directoryListing != null) {
                        for (File child : directoryListing) {
                            name = child.getName();
                            if (name.indexOf(".") > 0)
                                name = name.substring(0, name.lastIndexOf("."));
                            web1.loadUrl("javascript:AddCard('"+name+"', '"+
                                    readFromFile(MainActivity.this, name)+"');");


                        }
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        }, 100);
    }

    File myfile;
    String name;
    String title="", body="";
    String title2="", body2="";
    String title3="";
    boolean start = false;
    WebView web1;
    public static String txt_delete="";

    void Do(String title, String body){
        this.title = title;
        this.body = body;
    }

    boolean Hide = false;
    public static String txt="";

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getI(int i) {
            MainActivity.i = i;
        }

        @JavascriptInterface
        public void Start() {
            start = true;
        }

        @JavascriptInterface
        public void Edit(String title) {
            title2 = title;
            title3 = title;
        }

        @JavascriptInterface
        public void dev() {


            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com/a.y.m.a.n__a.t.i.f"));
           mContext.startActivity(browserIntent);
        }

        File myfile;

        @JavascriptInterface
        public void Save(String title, String body, String col) {

             myfile = new File(mContext.getFilesDir(), title+".txt");
            if(myfile.exists()){
                Toast.makeText(mContext, "Title already exists, please change it!", Toast.LENGTH_SHORT).show();
            }
            else{
                writeToFile(mContext, title,col+";"+body);
                Do(title,col+";"+ body);
            }
        }

        @JavascriptInterface
        public void Search(String txt){
            MainActivity.txt = txt;
        }

        @JavascriptInterface
        public void Delete(String txt){
            MainActivity.txt_delete = txt;
        }

        @JavascriptInterface
        public void Modify(String title, String body, String col) {
            myfile = new File(mContext.getFilesDir(), title3+".txt");
            if(myfile.exists())
            {
                myfile.delete();
            }
                writeToFile(mContext, title,col+";"+body);
                Hide = true;
        }

        private void writeToFile(Context context, String filename, String data) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename+".txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch (IOException e) {

            }
        }

    }

    private String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename+".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        return ret;
    }
    public static int i = 0;

    @Override
    public void onBackPressed() {
        if(i==-1){
            web1.loadUrl("javascript:HideResources();");
        }
        else if(i==1){
            web1.loadUrl("javascript:HideMain();");
        }
        else if(i==2){
            web1.loadUrl("javascript:HideNote();");
        }
        else if(i==0){
            finish();
        }
    }
}