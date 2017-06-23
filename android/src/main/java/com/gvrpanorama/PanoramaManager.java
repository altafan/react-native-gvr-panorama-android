package com.gvrpanorama;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by piero on 22/06/17.
 */

public class PanoramaManager extends SimpleViewManager<VrPanoramaView> {
    private static final String CLASS_NAME = "Panorama";
    private static final String TAG = SimpleViewManager.class.getSimpleName();

    private String imageUrl;
    private VrPanoramaView view;
    private Map<String, Bitmap> imageCache = new HashMap<>();

    public PanoramaManager(ReactApplicationContext context) {
        super();
    }

    @Override
    public String getName() {
        return CLASS_NAME;
    }

    @Override
    protected VrPanoramaView createViewInstance(ThemedReactContext reactContext) {
        view = new VrPanoramaView(reactContext.getCurrentActivity());

        view.setInfoButtonEnabled(true);
        view.setTouchTrackingEnabled(true);
        view.setTransitionViewEnabled(true);
        view.setFullscreenButtonEnabled(true);
        view.setStereoModeButtonEnabled(true);
        view.setEventListener(new ActivityEventListener());

        return view;
    }

    @ReactProp(name = "enableFullscreenButton")
    public void setFullscreenButtonEnabled(VrPanoramaView view, Boolean enabled) {
        view.setFullscreenButtonEnabled(enabled);
    }

    @ReactProp(name = "enableCardboardButton")
    public void setCardboardButtonEnabled(VrPanoramaView view, Boolean enabled) {
        view.setStereoModeButtonEnabled(enabled);
    }

    @ReactProp(name = "enableTouchTracking")
    public void setTouchTrackingEnabled(VrPanoramaView view, Boolean enabled) {
        view.setTouchTrackingEnabled(enabled);
    }

    @ReactProp(name = "enableInfoButton")
    public void setInfoButtonEnabled(VrPanoramaView view, Boolean enabled) {
        view.setInfoButtonEnabled(enabled);
    }

    @ReactProp(name= "hidesTransitionView")
    public void setTransitionViewEnabled(VrPanoramaView view, Boolean enabled) {
        view.setTransitionViewEnabled(!enabled);
    }

    @ReactProp(name = "displayMode")
    public void setDisplayMode(VrPanoramaView view, String mode) {
        switch(mode) {
            case "embedded":
                view.setDisplayMode(VrWidgetView.DisplayMode.EMBEDDED);
                break;
            case "fullscreen":
                view.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);
                break;
            case "cardboard":
                view.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);
                break;
            default:
                view.setDisplayMode(VrWidgetView.DisplayMode.EMBEDDED);
                break;
        }
    }

    @ReactProp(name = "image")
    public void setImage(VrPanoramaView view, ReadableMap options) {
        String uri = options.getString("uri");
        String type = options.getString("type");

        if (imageUrl != null && imageUrl.equals(uri)) {
            return;
        }

        imageUrl = uri;
        
        VrPanoramaView.Options imageOptions = new VrPanoramaView.Options();

        switch(type) {
            case "mono":
                imageOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                
                break;
            case "stereo":
                imageOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
                
                break;
            default:
                imageOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                
                break;
        }

        ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
        imageLoaderTask.execute(Pair.create(uri, imageOptions));
    }

    private class ActivityEventListener extends VrPanoramaEventListener {
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "Successfully loaded image");
        }

        @Override
        public void onLoadError(String errorMessage) {
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }

    class ImageLoaderTask extends AsyncTask<Pair<String, VrPanoramaView.Options>, Void, Boolean> {
        @SuppressWarnings("WrongThread")
        protected Boolean doInBackground(Pair<String, VrPanoramaView.Options>... fileInformation) {
            String uri = fileInformation[0].first;

            InputStream is = null;
            Bitmap image;

            if (!imageCache.containsKey(imageUrl)) {
                if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("file://")) {
                    try {
                        URL url = new URL(uri);

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        is = connection.getInputStream();

                        imageCache.put(imageUrl, BitmapFactory.decodeStream(is));
                    } catch (IOException e) {
                        Log.e(TAG, "Could not load file: " + e);
                        return false;
                    } finally {
                        try {
                            if (is != null) { is.close(); }
                        } catch (IOException e) {
                            Log.e(TAG, "Could not close input stream: " + e);
                        }
                    }
                } else {
                    imageCache.put(imageUrl, BitmapFactory.decodeFile(uri));
                }
            }

            image = imageCache.get(imageUrl);

            view.loadImageFromBitmap(image, fileInformation[0].second);

            return true;
        }

        private Bitmap decodeSampledBitmap(InputStream inputStream) throws IOException {
            final byte[] bytes = getBytesFromInputStream(inputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }

        private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            return baos.toByteArray();
        }
    }
}
