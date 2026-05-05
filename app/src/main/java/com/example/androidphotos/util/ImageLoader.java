package com.example.androidphotos.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads scaled images from content URIs to avoid large bitmap crashes.
 */
public final class ImageLoader {

    private static final int THUMBNAIL_TARGET_SIZE = 200;
    private static final int LARGE_TARGET_SIZE = 1200;

    private ImageLoader() {
    }

    public static void loadThumbnail(Context context, ImageView imageView, String uriString) {
        loadScaledImage(context, imageView, uriString, THUMBNAIL_TARGET_SIZE, THUMBNAIL_TARGET_SIZE);
    }

    public static void loadLargeImage(Context context, ImageView imageView, String uriString) {
        loadScaledImage(context, imageView, uriString, LARGE_TARGET_SIZE, LARGE_TARGET_SIZE);
    }

    private static void loadScaledImage(Context context, ImageView imageView, String uriString,
                                        int targetWidth, int targetHeight) {
        if (context == null || imageView == null || uriString == null || uriString.trim().isEmpty()) {
            setPlaceholder(imageView);
            return;
        }

        Uri uri = Uri.parse(uriString);
        ContentResolver resolver = context.getContentResolver();

        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        if (!decodeBounds(resolver, uri, boundsOptions)) {
            setPlaceholder(imageView);
            return;
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = calculateInSampleSize(boundsOptions, targetWidth, targetHeight);
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = decodeBitmap(resolver, uri, decodeOptions);
        if (bitmap == null) {
            setPlaceholder(imageView);
            return;
        }

        imageView.setImageBitmap(bitmap);
    }

    private static boolean decodeBounds(ContentResolver resolver, Uri uri, BitmapFactory.Options options) {
        try (InputStream inputStream = resolver.openInputStream(uri)) {
            if (inputStream == null) {
                return false;
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            return options.outWidth > 0 && options.outHeight > 0;
        } catch (IOException | SecurityException exception) {
            return false;
        }
    }

    private static Bitmap decodeBitmap(ContentResolver resolver, Uri uri, BitmapFactory.Options options) {
        try (InputStream inputStream = resolver.openInputStream(uri)) {
            if (inputStream == null) {
                return null;
            }
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException | SecurityException exception) {
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        while ((height / inSampleSize) > reqHeight * 2 || (width / inSampleSize) > reqWidth * 2) {
            inSampleSize *= 2;
        }

        return Math.max(1, inSampleSize);
    }

    private static void setPlaceholder(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
