package com.test.testinsta.custom;

/**
 * Created by Nikhil-PC on 22/12/17.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ImageDownload {

    private String FILE_NAME;
    private Context mContext;

    public void DownloadFromUrl(Context context, String imageURL, String fileName) {  //this is the downloader method

        mContext = context;
        FILE_NAME = fileName;
        new DownloadFromURL().execute(imageURL);
    }


    class DownloadFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mContext, "Image is Downloading. Please wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try {

                File file = new File("/sdcard/" + FILE_NAME + ".jpg");
                if (file.exists())
                    file.delete();

                URL url = new URL(fileUrl[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                // show progress bar 0-100%
                int fileLength = urlConnection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                OutputStream outputStream = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                    outputStream.write(data, 0, count);
                }
                // flushing output
                outputStream.flush();
                // closing streams
                outputStream.close();
                inputStream.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {
            Toast.makeText(mContext, "Image saved on this device", Toast.LENGTH_SHORT).show();
        }
    }
}
