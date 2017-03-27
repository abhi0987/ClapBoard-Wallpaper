package io.wings.gabriel.tapestry.gif_wall_service;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;

import io.wings.gabriel.tapestry.Gif_Image_activity;
import io.wings.gabriel.tapestry.app.AppController;

public class GIFWallpaperService extends WallpaperService {

    ArrayList<String>  gif = new ArrayList<>();
    public static String PATH;
    Movie movie;




    @Override
	public Engine onCreateEngine() {

       try {
           PATH = AppController.getInstance().getPrefManger().get_path();
            movie = Movie.decodeFile(PATH);
       }catch (Exception e){


       }

        return new GIFWallpaperEngine(movie);
    }

    private class GIFWallpaperEngine extends Engine {

        private final int frameDuration = 20;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;

        public GIFWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };


        private void draw() {
            if (visible) {
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                    // Adjust size and position so that
                    // the image looks good on your screen
                    canvas.scale(2.9f, 3f);
                    movie.draw(canvas, -100, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);

        }


        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

            handler.removeCallbacks(drawGIF);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
