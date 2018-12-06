package razorbacktransit.arcu.razorbacktransit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchedulesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchedulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchedulesFragment extends ListFragment {

    private PDF[] allSchedules;
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private FirebaseAnalytics mFirebaseAnalytics;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    public static SchedulesFragment newInstance(String param1, String param2) {

        return new SchedulesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allSchedules = new PDFFactory().getAllSChedules();

        String[] values = new String[allSchedules.length];

        for (int i = 0; i < allSchedules.length; i++) {
            values[i] = allSchedules[i].getTitle();
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.row_layout,R.id.text,values);
        // Bind adapter to the ListFragment
        setListAdapter(adapter);
        //  Retain the ListFragment instance across Activity re-creation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_schedules, container, false);
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
    public void onListItemClick(ListView l, View view, int position, long id) {

        Bundle bundle = new Bundle();
        bundle.putString("filepath", allSchedules[position].getFilePath());
        ViewScheduleFragment viewScheduleFragment = new ViewScheduleFragment();
        viewScheduleFragment.setArguments(bundle);

        if (mListener != null) {
            mListener.onFragmentInteraction(viewScheduleFragment, allSchedules[position].getTitle());
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ViewScheduleFragment viewScheduleFragment, String title);
    }
}
