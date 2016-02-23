package com.example.jimmy.nanohttpdtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Random;
//import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends Activity {
    private WebServer server;
    public static final String HTTP_BADREQUEST = "400 Bad Request";
    public static final String HTTP_FORBIDDEN = "403 Forbidden";
    public static final String HTTP_INTERNALERROR = "500 Internal Server Error";
    public static final String HTTP_NOTFOUND = "404 Not Found";
    public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";
    public static final String HTTP_NOTMODIFIED = "304 Not Modified";
    public static final String HTTP_OK = "200 OK";
    public static final String HTTP_PARTIALCONTENT = "206 Partial Content";
    public static final String HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable";
    public static final String HTTP_REDIRECT = "301 Moved Permanently";

    public static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_JS = "application/javascript",
            MIME_CSS = "text/css",
            MIME_PNG = "image/png",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml";

    public static final String MIME_JAVASCRIPT = "text/javascript";
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_SVG = "image/svg+xml";
    public static final String MIME_JSON = "application/json";
    public static final String MIME_GIF = "image/gif";
    private static Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext = this;
        //File root = Environment.getExternalStorageDirectory();
        //Log.d("jimmy","path = "+root.getAbsolutePath() +
        //        "/www/index.html");

        server = new WebServer();
        try {
            //server.
            server.start();
        } catch(IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");

    }


    // DON'T FORGET to stop the server
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    private class WebServer extends NanoHTTPD {

        public WebServer() {
            super(8080);
        }

    /*
        @Override
        public Response serve(IHTTPSession session) {
            String answer = "";
            String uri = null;
            uri = session.getUri();
            Log.d("jimmy","uri = " +uri);

            try {
                // Open file from SD Card
                File root = Environment.getExternalStorageDirectory();
               // Log.d("jimmy","getRootDirectory = "+Environment.getRootDirectory() +
                //        "/www/index.html");
                //Log.d("jimmy","getAbsolutePath = "+root.getAbsolutePath()+"/www/index.html");
                String Filepath = "/system/www/index.html";
                //FileReader index = new FileReader(root.getAbsolutePath() +
                 //       "/www/index.html");
                FileReader index = new FileReader(Filepath);

                BufferedReader reader = new BufferedReader(index);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line;
                }
                reader.close();



            } catch(IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }

            //public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
            return newFixedLengthResponse(answer);
        }

    */
        @Override
        public Response serve(IHTTPSession session) {
            String answer = "";
            String uri = null;
            uri = session.getUri();
            Log.d("jimmy","uri = " +uri);
            Log.d("jimmy","uri substring(1)= " +uri.substring(1));

            InputStream mbuffer = null;
            if(uri.equals("/")) {
                try {
                    BufferedReader reader = null;
                    try {

                        reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("index.html")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        answer += line + "\n";
                    }
                    reader.close();

                } catch(IOException ioe) {
                    Log.w("Httpd", ioe.toString());
                }


                return newFixedLengthResponse(answer);
            }else if(uri.endsWith("js")){

                //Log.i("jimmy","it's javascript file");
                try {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    try {
                        BufferedReader reader = null;
                        try {
                            String [] list = null;

                            reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open(uri.substring(1))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            answer += line + "\n";
                        }
                        reader.close();

                    } catch(IOException ioe) {
                        Log.w("Httpd", ioe.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return newFixedLengthResponse(answer);

            }else if(uri.endsWith("gif") || uri.endsWith("png")){
                Log.d("jimmy","It's png & gif file");

                InputStream is = null;
                        try {
                            is = mContext.getAssets().open(uri.substring(1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                //Response newChunkedResponse(IStatus status, String mimeType, InputStream data)
                return newChunkedResponse(Response.Status.OK, MIME_PNG, is);
            }else{
                //newFixedLengthResponse(NanoHTTPD.Response.IStatus status, String mimeType, String message)
                //return newFixedLengthResponse(HTTP_NOTFOUND, String mimeType, String txt);
                //return newFixedLengthResponse(HTTP_NOTFOUND, String mimeType, String message);
            }

            Method method = session.getMethod();
            Log.d("jimmy","method = "+method);
            //public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
            //return newFixedLengthResponse(IStatus status, String mimeType, String txt);
            return newFixedLengthResponse(answer);
        }


        /*
        @Override
        public Response serve(IHTTPSession session) {
            String mime_type = NanoHTTPD.MIME_HTML;
            Method method = session.getMethod();
            String uri = session.getUri();
            Log.d("jimmy", "uri = " + uri);
            Log.d("jimmy","11111111111");
            InputStream descriptor = null;
            String answer = "";

            if(method.toString().equalsIgnoreCase("GET")){
                String path;
                if(uri.equals("/")){
                    String Filepath = "/system/www/index.html";

                    FileReader index = null;
                    try {
                        index = new FileReader(Filepath);
                        BufferedReader reader = new BufferedReader(index);
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            answer += line;
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return newFixedLengthResponse(answer);

                }else{
                    path = uri;
                    try{
                        if(path.endsWith(".js")){
                            mime_type = MIME_JAVASCRIPT;
                        }else if(path.endsWith(".css")){
                            mime_type = MIME_CSS;
                        }else if(path.endsWith(".html")){
                            mime_type = MIME_HTML;
                        }else if(path.endsWith(".jpeg")){
                            mime_type = MIME_JPEG;
                        }else if(path.endsWith(".png")){
                            mime_type = MIME_PNG;
                        }else if(path.endsWith(".jpg")){
                            mime_type = MIME_JPEG;
                        }else if(path.endsWith(".svg")){
                            mime_type = MIME_SVG;
                        }else if(path.endsWith(".json")){
                            mime_type = MIME_JSON;
                        }else if(path.endsWith(".gif")){
                            mime_type = MIME_GIF;
                        }
                    }catch(Exception e){

                    }
                }

                try {
                    // Open file from SD Card
                    descriptor = getAssets().open("www/orbit"+path);

                } catch(IOException ioe) {
                    Log.w("Httpd", ioe.toString());
                }

                String webpath = "/asserts/www";
            }
            String webpath = "/asserts/www";
            return newFixedLengthResponse(Response.Status.OK, mime_type, webpath);
        }
        */


        /*
        @Override
        public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
            Log.d("jimmy","SERVE ::  URI "+uri);
            //final StringBuilder buf = new StringBuilder();
           // for (Entry<Object, Object> kv : header.entrySet())
            //    buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
            InputStream mbuffer = null;



            try {
                if(uri!=null){

                    if(uri.contains(".js")){
                        mbuffer = getAssets().open(uri.substring(1));
                        return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer);
                    }else if(uri.contains(".css")){
                        mbuffer = mContext.getAssets().open(uri.substring(1));
                        return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer);

                    }else if(uri.contains(".png")){
                        mbuffer = mContext.getAssets().open(uri.substring(1));
                        // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                        return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
                    }else if (uri.contains("/mnt/sdcard")){
                        Log.d(TAG,"request for media on sdCard "+uri);
                        File request = new File(uri);
                        mbuffer = new FileInputStream(request);
                        FileNameMap fileNameMap = URLConnection.getFileNameMap();
                        String mimeType = fileNameMap.getContentTypeFor(uri);

                        Response streamResponse = new Response(HTTP_OK, mimeType, mbuffer);
                        Random rnd = new Random();
                        String etag = Integer.toHexString( rnd.nextInt() );
                        streamResponse.addHeader( "ETag", etag);
                        streamResponse.addHeader( "Connection", "Keep-alive");






                        return streamResponse;
                    }else{
                        mbuffer = mContext.getAssets().open("index.html");
                        return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, mbuffer);
                    }
                }

            } catch (IOException e) {
                Log.d("jimmy","Error opening file"+uri.substring(1));
                e.printStackTrace();
            }

            return null;

        }
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
