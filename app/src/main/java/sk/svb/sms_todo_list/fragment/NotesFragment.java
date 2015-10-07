package sk.svb.sms_todo_list.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.activity.EditNote;
import sk.svb.sms_todo_list.db_models.DbNoteModel;
import sk.svb.sms_todo_list.logic.Note;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotesFragment extends Fragment {

    private final String TAG = NotesFragment.class.getName();

    private ListView mListView;
    private ProgressBar loadingView;
    private ArrayAdapter<Note> mAdapter;
    private TextView emptyListTV;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotesFragment newInstance() {
        NotesFragment fragment = new NotesFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    public NotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        setupView(view);

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        reloadAdapter();
        super.onResume();
    }

    private void reloadAdapter(){
        loadingView.setVisibility(View.VISIBLE);
        new ReloadPromptsAsync().execute(new String[]{});
    }

    private void setupView(View view) {
        mAdapter = new ArrayAdapter<Note>(getActivity(), android.R.layout.activity_list_item, new ArrayList<Note>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                if (getItem(position).adapterType == Note.TYPE_NOTE) {
                    final View layout = inflater.inflate(
                            R.layout.widget_row, null);

                    TextView tv1 = ((TextView) layout.findViewById(android.R.id.text1));
                    tv1.setText(getItem(position).getMessage());

                    TextView tv2 = ((TextView) layout .findViewById(android.R.id.text2));
                    if (getItem(position).getTitle() == null || getItem(position).getTitle().length() == 0){
                        tv2.setVisibility(View.GONE);
                    }else{
                        tv2.setVisibility(View.VISIBLE);
                        tv2.setText(getItem(position).getTitle());
                    }
                    layout.findViewById(R.id.text3).setVisibility(View.GONE);
                    return layout;

                }else if (getItem(position).adapterType == Note.TYPE_ADAPTER_TITLE) {
                    final View layout = inflater.inflate(
                            R.layout.widget_row_adapter, null);
                    TextView tv1 = (TextView) layout.findViewById(android.R.id.text1);
                    tv1.setText(getItem(position).getTitle());
                    return layout;
                }


                return null;
            }
        };
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(getActivity(), EditNote.class);
                mIntent.putExtra("id", ((Note) mAdapter.getItem(position)).getId());
//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        makeCustomAnimation(getActivity(), R.transition.animation_in, R.anim.animation_out);
//                getActivity().startActivity(mIntent, options.toBundle());
                getActivity().startActivity(mIntent);
            }
        });
        mListView.setAdapter(mAdapter);

        loadingView = (ProgressBar) view.findViewById(R.id.loading);
        emptyListTV = (TextView) view.findViewById(R.id.empty_list);

        ((ImageButton)view.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), EditNote.class);
                mIntent.putExtra("new", true );
                getActivity().startActivity(mIntent);
            }
        });
    }

    private void reloadList(List<Note> list) {
        loadingView.setVisibility(View.GONE);
        emptyListTV.setVisibility((list.size()>0) ? View.GONE : View.VISIBLE);
        mAdapter.clear();
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private class ReloadPromptsAsync extends
            AsyncTask<String, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(String... params) {
            return DbNoteModel.getNotes(getActivity(), false);
        }

        @Override
        protected void onPostExecute(List<Note> result) {
            reloadList(result);
        }
    }
}
