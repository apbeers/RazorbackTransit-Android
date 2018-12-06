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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoutesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoutesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutesFragment extends ListFragment {

    private PDF[] allRoutes;

    private OnFragmentInteractionListener mListener;

    public RoutesFragment() {
        // Required empty public constructor
    }

    public static RoutesFragment newInstance(String param1, String param2) {
        return new RoutesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allRoutes = new PDFFactory().getAllRoutes();

        String[] values = new String[allRoutes.length];

        for (int i = 0; i < allRoutes.length; i++) {
            values[i] = allRoutes[i].getTitle();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routes, container, false);
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
        bundle.putString("filepath", allRoutes[position].getFilePath());
        ViewRouteFragment viewRouteFragment = new ViewRouteFragment();
        viewRouteFragment.setArguments(bundle);

        mListener.onFragmentInteraction(viewRouteFragment, allRoutes[position].getTitle());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ViewRouteFragment viewRouteFragment, String title);
    }
}
