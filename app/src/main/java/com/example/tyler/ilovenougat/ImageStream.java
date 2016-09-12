package com.example.tyler.ilovenougat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

/**
 * Created by Tyler Collison on 9/9/2016.
 */
public class ImageStream extends Activity{

    // stores all actively running threads
    Stack<AsyncStreamToImageView> threadStack = new Stack<>();

    /**
     * Displays an image at the @url and displays it in the @imageView
     *
     * @param url URL of an image
     * @param imageView target at which to display the image
     *
     * @requires @url points to a valid image file and @imageView is not null
     *
     * @ensures the image at @url is displayed as the source of @imageView
     */
    public void streamToImageView (String url, ImageView imageView) {
        // start the async task
        AsyncStreamToImageView stream = new AsyncStreamToImageView();
        Object[] parameters = {url, imageView};
        stream.execute(parameters);
        // add the async task to the thread stack
        threadStack.add(stream);
    }

    /**
     * Cancels all running threads
     *
     * @ensures all running threads are halted safely
     */
    public void cancelAll () {
        // halt all running threads in the thread stack
        for (AsyncStreamToImageView task : threadStack) {
            task.stop();
        }
        // clear the thread stack
        threadStack.clear();
    }

    private class AsyncStreamToImageView extends AsyncTask<Object, Void, Void> {

        // flag for determining whether to halt the thread
        private volatile boolean stop = false;

        /**
         * Async retrieves an image from the url and displays it in the view
         *
         * @param objects takes parameters String url and ImageView view in order
         * @return always null
         *
         * @requires objects[0] = String url and objects[1] = ImageView view
         *
         * @ensures the source of view is the image at url
         */
        @Override
        protected Void doInBackground(Object... objects) {
            String url = objects[0].toString();
            ImageView view = (ImageView)objects[1];

            // halt the thread if stop flag is true
            if (stop) {
                return null;
            }

            // get the image and display it in the view
            displayImage(url, view);
            // remove this thread from the thread stack
            threadStack.remove(this);
            return null;
        }

        /**
         * Halts the thread
         */
        public void stop () {
            stop = true;
        }

    }

    /**
     * Displays an image at the @url and displays it in the @view
     *
     * @param url URL of an image
     * @param view target at which to display the image
     *
     * @requires @url points to a valid image file and @view is not null
     *
     * @ensures the image at @url is displayed as the source of @imageView
     */
    private void displayImage(String url, final ImageView view){
        final Bitmap result;
        URL bitURL;
        try {
            bitURL = new URL(url);
            // open a connection to the url
            URLConnection conn = bitURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            // decode the bitmap image
            result = BitmapFactory.decodeStream(is);
            // assign the bitmap image to the view on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setImageBitmap(result);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
