package sk.svb.sms_todo_list.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.activity.EditNote;
import sk.svb.sms_todo_list.activity.MainActivity;
import sk.svb.sms_todo_list.db_models.DbNoteModel;
import sk.svb.sms_todo_list.dialog.DeleteAllTrashBinNotesDialogFragment;
import sk.svb.sms_todo_list.logic.Note;

/**
 * Created by mbodis on 9/20/15.
 */
public class TrashBinFragment extends Fragment {

    public static final String TAG = TrashBinFragment.class.getName();
    public static final String FILTER_BROADCAST = TAG;

    private ListView mListView;
    private ProgressBar loadingView;
    private ArrayAdapter<Note> mAdapter;
    private TextView emptyListTV;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TrashBinFragment newInstance() {
        TrashBinFragment fragment = new TrashBinFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    public TrashBinFragment() {
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                // reload adapter
                if (intent.hasExtra("reload_adapter")) {
                    reloadAdapter();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_bin, container, false);
        setupView(view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        reloadAdapter();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(FILTER_BROADCAST));
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    private void reloadAdapter() {
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

                    TextView tv2 = ((TextView) layout.findViewById(android.R.id.text2));
                    if (getItem(position).getTitle() == null || getItem(position).getTitle().length() == 0) {
                        tv2.setVisibility(View.GONE);
                    } else {
                        tv2.setVisibility(View.VISIBLE);
                        tv2.setText(getItem(position).getTitle());
                    }
                    layout.findViewById(R.id.text3).setVisibility(View.GONE);
                    return layout;

                } else if (getItem(position).adapterType == Note.TYPE_ADAPTER_TITLE) {
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
                mIntent.putExtra("deleted_note", true);
                getActivity().startActivity(mIntent);
            }
        });
        mListView.setAdapter(mAdapter);

        loadingView = (ProgressBar) view.findViewById(R.id.loading);
        emptyListTV = (TextView) view.findViewById(R.id.empty_list);

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
            return DbNoteModel.getNotes(getActivity(), true);
        }

        @Override
        protected void onPostExecute(List<Note> result) {
             reloadList(result);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).mNavigationDrawerFragment.isDrawerOpen()
                && mAdapter.getCount() > 0) {
            getActivity().getMenuInflater().inflate(R.menu.menu_trash_bin, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            DialogFragment newFragment = DeleteAllTrashBinNotesDialogFragment
                    .newInstance();
            newFragment.show(getActivity().getFragmentManager(), DeleteAllTrashBinNotesDialogFragment.TAG);
        }

        return super.onOptionsItemSelected(item);
    }
}