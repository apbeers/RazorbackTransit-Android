package razorbacktransit.arcu.razorbacktransit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewScheduleFragment extends Fragment {

    private static final String ARG_PARAM1 = "filepath";

    private String mParam1;
    private PDFView pdfView;

    private OnFragmentInteractionListener mListener;

    public ViewScheduleFragment() {
        // Required empty public constructor
    }

    public static ViewScheduleFragment newInstance(String param1, String param2) {
        ViewScheduleFragment fragment = new ViewScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);
        pdfView = (PDFView)view.findViewById(R.id.pdfView);

        InputStream inputStream = getResources().openRawResource(
                getResources().getIdentifier(mParam1,
                        "raw", getActivity().getPackageName()));

        pdfView.fromStream(inputStream)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int pages, float pageWidth,
                                                    float pageHeight) {
                        pdfView.fitToWidth(); // optionally pass page number
                    }
                })
                .enableAnnotationRendering(true)
                .load();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
