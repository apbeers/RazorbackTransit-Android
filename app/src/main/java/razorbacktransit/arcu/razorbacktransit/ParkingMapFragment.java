package razorbacktransit.arcu.razorbacktransit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.InputStream;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParkingMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParkingMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingMapFragment extends Fragment {

    private PDFView pdfView;
    private Boolean loaded;

    private OnFragmentInteractionListener mListener;

    public ParkingMapFragment() {
        // Required empty public constructor
    }

    public static ParkingMapFragment newInstance(String param1, String param2) {
        ParkingMapFragment fragment = new ParkingMapFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaded = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_parking_map, container, false);

        pdfView = (PDFView)view.findViewById(R.id.pdfView);

        InputStream inputStream = getResources().openRawResource(
                getResources().getIdentifier("parkmap",
                        "raw", getActivity().getPackageName()));

        pdfView.fromStream(inputStream)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .load();

        loaded = false;
        return view;
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
