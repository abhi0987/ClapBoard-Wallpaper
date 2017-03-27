package io.wings.gabriel.tapestry.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;




import java.io.File;
import java.io.FileOutputStream;

import io.wings.gabriel.tapestry.R;

/**
 * Created by ABHISHEK on 2/10/2016.
 */
public class Utils {

  private   String TAG = Utils.class.getSimpleName();
  private Context context;
  private PrefManager pref;



    public Utils(Context context) {

       this.context=context;
       pref =  new PrefManager(context);
    }


    public int getScreenWidth(){
        int columnWidth;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
         try{
             display.getSize(point);
         }catch (NoSuchMethodError e){
             point.x = display.getWidth();
             point.y = display.getHeight();

         }

        columnWidth=point.x;
        return columnWidth;
    }

    public void saveImageToSD_CARD(Bitmap bitmap, CoordinatorLayout coordinatorLayout, String idList){

        File myDir  = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName());
        myDir.mkdirs();
      /*  Random geneRator = new Random();
        int n =10000;
        n = geneRator.nextInt(n);
        String fname = "Wallpaper-" + n + ".jpg" ;*/
        String fname = idList;

        File file = new File(myDir,fname);
        if(file.exists())
            file.delete();


            try{
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                Snackbar snackbar = Snackbar.make(coordinatorLayout,context.getString(R.string.toast_saved).replace("#","\"" +
                                                                             pref.getGalleryName() + "\""),Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();

                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();


            } catch (Exception e) {
                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, context.getString(R.string.toast_saved_failed), Snackbar.LENGTH_SHORT);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }

    }

    public static Uri getImageContentUri(Context context ,File imageFile){
        String filePath = imageFile.getAbsolutePath();
        String[] star ={ MediaStore.Images.Media._ID };
        String p = MediaStore.Images.Media.DATA + "=?";
        Cursor cursor = context.getContentResolver()
                       .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, star, p, new String[]{filePath}, null);
        if(cursor!=null && cursor.moveToFirst()){
            int ID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + ID);
        }else {
            if(imageFile.exists()){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }else {
                return null;
            }
        }
    }




/*

    public void setWallpaper(Bitmap bitmap , CoordinatorLayout coordinatorLayout){
          File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName());
          myDir.mkdirs();

          Random geneRators = new Random();
          int n = 10000;
          n = geneRators.nextInt();

        String fname = "Wallpaper-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();

        try{
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Uri uri = getImageContentUri(context,file);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Intent intent = null;

                intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(uri, "image*//**//*");
                intent.putExtra("mimeType", "image*//**//*");
                Intent chooser_intent = Intent.createChooser(intent,"Set As");
                chooser_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);




            try {
                context.startActivity(chooser_intent);
            } catch (Exception e) {
                //handle error
            }




        }catch (Exception e){
            e.printStackTrace();

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, context.getString(R.string.toast_wallpaper_set_failed), Snackbar.LENGTH_SHORT);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }*/


    public void share_wallPaper(Bitmap bitmap, CoordinatorLayout coordinatorLayout, String idList){

        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName());
        myDir.mkdirs();

       /* Random geneRators = new Random();
        int n = 10000;
        n = geneRators.nextInt();*/

        String fname = idList;
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Uri uri = getImageContentUri(context,file);
            Intent share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.setDataAndType(uri,"image/*");
            share_intent.putExtra(Intent.EXTRA_STREAM,uri);
            Intent chooser_intent = Intent.createChooser(share_intent,"Share Using ");
            chooser_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(chooser_intent);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, context.getString(R.string.toast_wallpaper_set_failed), Snackbar.LENGTH_SHORT);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }



    }



}
