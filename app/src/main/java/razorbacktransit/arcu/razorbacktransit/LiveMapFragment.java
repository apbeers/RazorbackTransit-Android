package razorbacktransit.arcu.razorbacktransit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LiveMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private OnFragmentInteractionListener mListener;
    private GoogleMap googleMap;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final OkHttpClient client = new OkHttpClient();
    private final List<Marker> busMarkers = new ArrayList<>();
    private final HashMap<Marker, String> stopMarkerHashMap = new HashMap<>();
    private List<String> routeIDsForBusses = new ArrayList<>();
    private Timer busTimer;

    private int stopImageWidth;
    private int stopImageHeight;
    private int busImageWidth;
    private int busImageHeight;

    public LiveMapFragment() {
        // Required empty public constructor
    }

    public static LiveMapFragment newInstance(String param1, String param2) {
        return new LiveMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        editor = sharedPreferences.edit();

        final int widthPixels = getActivity().getResources().getDisplayMetrics().widthPixels;
        
        stopImageWidth = (int) (widthPixels * 0.02638888889);
        stopImageHeight = (int) (widthPixels * 0.02638888889);

        busImageWidth = (int) (widthPixels * 0.05833333333);
        busImageHeight = (int) (widthPixels * 0.05833333333 * 1.6153846154);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.googleMap != null) {
            this.googleMap.clear();
        }

        try {
            loadRoutes();
            loadBusses();
        } catch (Exception e) {
            e.printStackTrace();
        }

        busTimer = new Timer();
        busTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    loadBusses();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 5000, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();

        editor.apply();
        busTimer.cancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.09, -94.1785)));
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
        this.googleMap.setMinZoomPreference(10);
        this.googleMap.setOnMarkerClickListener(this);
        UiSettings uiSettings = this.googleMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(false);

    }

    public void loadRoutes() throws Exception {

        Request request = new Request.Builder()
                .url(getString(R.string.route_url))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseText = responseBody.string();
                    responseText = responseText.substring(7, responseText.length() - 2);

                    try {
                        final JSONArray jsonArray = new JSONArray(responseText);

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {

                                List<String> routeIDs = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if (jsonObject.getString("inService").equals("1")) {

                                            List<LatLng> points = new ArrayList<>();
                                            String[] coordinates = jsonObject.getString("shape").split(",");

                                            if (coordinates.length > 1) {
                                                for (String coordinate : coordinates) {
                                                    String[] latlong = coordinate.split(" ");
                                                    points.add(new LatLng(
                                                            Double.parseDouble(latlong[0]),
                                                            Double.parseDouble(latlong[1])));
                                                }
                                                final Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                                                        .color((int) Long.parseLong(("99" + jsonObject.getString("color")
                                                                .substring(1)), 16)));
                                                polyline.setPoints(points);
                                            }
                                            if (!routeIDs.contains(jsonObject.getString("id"))) {
                                                routeIDs.add(jsonObject.getString("id"));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    routeIDsForBusses = new ArrayList<>(routeIDs);
                                    loadBusses();
                                    loadStops(routeIDs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(myRunnable);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String buildStopURL(List<String> routeIDs) {
        String stopString = getString(R.string.stop_url);
        for (String id : routeIDs) {
            stopString = stopString.concat("-" + id);
        }
        return stopString;
    }

    private String buildStopImageULR(String id, List<String> routeIDs) {
        String urlString = "https://campusdata.uark.edu/api/stopimages?stopId=" + id + "&routeIds=undefined";
        for (String routeID : routeIDs) {
            urlString = urlString.concat("-" + routeID);
        }
        return urlString;
    }

    public void loadStops(final List<String> routeIDs) throws Exception {

        Request request = new Request.Builder()
                .url(buildStopURL(routeIDs))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseText = responseBody.string();
                    responseText = responseText.substring(10, responseText.length() - 2);

                    try {
                        final JSONArray jsonArray = new JSONArray(responseText);
                        stopMarkerHashMap.clear();

                        if (getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        final JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        final Marker marker = googleMap.addMarker(new MarkerOptions()
                                                .flat(true)
                                                .alpha(0)
                                                .snippet(jsonObject.getString("name"))
                                                .title("Next Arrival: ...")
                                                .position(new LatLng(
                                                        Double.parseDouble(jsonObject.getString("latitude")),
                                                        Double.parseDouble(jsonObject.getString("longitude")))));

                                        stopMarkerHashMap.put(marker, jsonObject.getString("id"));
                                        String encodedImage = sharedPreferences.getString(jsonObject.getString("id") + "1", "");

                                        if (!encodedImage.equals("")) {
                                            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                                            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.
                                                    fromBitmap(bitmap);
                                            marker.setIcon(bitmapDescriptor);
                                            marker.setAlpha(1);
                                        }

                                        Request iconRequest = new Request
                                                .Builder()
                                                .url(buildStopImageULR(jsonObject.getString("id"), routeIDs))
                                                .build();

                                        client.newCall(iconRequest).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                try (ResponseBody responseBody = response.body()) {
                                                    if (!response.isSuccessful())
                                                        throw new IOException("Unexpected code " + response);

                                                    InputStream inputStream = response.body().byteStream();
                                                    final Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), stopImageWidth, stopImageHeight, true);
                                                    final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.
                                                            fromBitmap(bitmap);

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    byte[] bytes = byteArrayOutputStream.toByteArray();

                                                    editor.putString(jsonObject.getString("id") + "1", Base64.encodeToString(bytes, Base64.DEFAULT));

                                                    if (getActivity() == null) {
                                                        return;
                                                    }

                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                marker.setIcon(bitmapDescriptor);
                                                                marker.setAlpha(1);
                                                            } catch (IllegalArgumentException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                    for (Marker marker : stopMarkerHashMap.keySet().toArray(new Marker[]{})) {
                                        builder.include(marker.getPosition());
                                    }
                                    LatLngBounds bounds = builder.build();
                                    int padding = busImageHeight; // offset from edges of the map in pixels
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    googleMap.animateCamera(cu);
                                }
                            }

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getBusImageKey(String heading, String color) {
        Double x = Double.parseDouble(heading);
        return color + Integer.toString(((x.intValue() + 29) / 30) * 30);
    }

    private String buildBusURL() {
        String part1 = "https://campusdata.uark.edu/api/buses?callback=jQuery18002674589609856972_1510069338014&routeIds=undefined";
        String part2 = "&_=1510069339459";
        for (String id : routeIDsForBusses) {
            part1 = part1.concat("-" + id);
        }
        part1 = part1.concat(part2);
        return part1;
    }

    public void loadBusses() throws Exception {

        Request request = new Request.Builder()
                .url(buildBusURL())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseText = responseBody.string();
                    responseText = responseText.substring(41, responseText.length() - 2);

                    try {
                        final JSONArray jsonArray = new JSONArray(responseText);

                        if (getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                List<Marker> oldMarkers = new ArrayList<>(busMarkers);
                                busMarkers.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        final JSONObject jsonObject = jsonArray.getJSONObject(i);



                                        final Marker marker = googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(
                                                        Double.parseDouble(jsonObject.getString("latitude")),
                                                        Double.parseDouble(jsonObject.getString("longitude"))))
                                                .flat(true)
                                                .alpha(0)
                                                .title(jsonObject.getString("routeName")));

                                        busMarkers.add(marker);
                                        final String key = getBusImageKey(jsonObject.getString("heading"), jsonObject.getString("color"));
                                        final String encodedImage = sharedPreferences.getString(key, "");

                                        if (!encodedImage.equals("")) {

                                            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                                            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.
                                                    fromBitmap(bitmap);
                                            marker.setIcon(bitmapDescriptor);
                                            marker.setAlpha(1);

                                        } else {

                                            Request iconRequest = new Request
                                                    .Builder()
                                                    .url("https://campusdata.uark.edu/api/busimages?color=" + jsonObject.getString("color").substring(1) + "&heading=" + jsonObject.getString("heading"))
                                                    .build();

                                            client.newCall(iconRequest).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    try (ResponseBody responseBody = response.body()) {
                                                        if (!response.isSuccessful())
                                                            throw new IOException("Unexpected code " + response);

                                                        InputStream inputStream = response.body().byteStream();
                                                        final Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), busImageWidth, busImageHeight, true);
                                                        final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.
                                                                fromBitmap(bitmap);

                                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                        byte[] bytes = byteArrayOutputStream.toByteArray();

                                                        editor.putString(key, Base64.encodeToString(bytes, Base64.DEFAULT));
                                                        editor.apply();

                                                        if (getActivity() == null) {
                                                            return;
                                                        }

                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (marker.isVisible()) {
                                                                    try {
                                                                        marker.setIcon(bitmapDescriptor);
                                                                        marker.setAlpha(1);
                                                                    } catch (IllegalArgumentException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                for (Marker temp : oldMarkers) {
                                    try {
                                        temp.remove();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (!stopMarkerHashMap.containsKey(marker)) {
            return false;
        }

        marker.setTitle("Next Arrival: Loading...");
        marker.showInfoWindow();

        final String url = "https://campusdata.uark.edu/api/routes?callback=jQuery18004251280482585251_1507605405541&stopId=" + stopMarkerHashMap.get(marker) + "&_=1507605550296";

        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String responseText = responseBody.string();
                    responseText = responseText.substring(41, responseText.length() - 2);
                    try {

                        final JSONArray jsonArray = new JSONArray(responseText);

                        if (getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        final String nextArrival = jsonObject.getString("nextArrival");

                                        if (!nextArrival.equals("...") && !nextArrival.equals("null")) {

                                            marker.setTitle("Next Arival: " + nextArrival);
                                            marker.showInfoWindow();

                                            return;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                marker.setTitle("Next Arrival: None");
                                marker.showInfoWindow();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        return false;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
