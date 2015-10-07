package sk.svb.sms_todo_list.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.db_models.DbNoteModel;
import sk.svb.sms_todo_list.fragment.TrashBinFragment;

/**
 * Created by mbodis on 9/22/15.
 */
public class DeleteAllTrashBinNotesDialogFragment extends DialogFragment {

    public static final String TAG = DeleteAllTrashBinNotesDialogFragment.class.getName();

    public static DeleteAllTrashBinNotesDialogFragment newInstance() {
        DeleteAllTrashBinNotesDialogFragment frag = new DeleteAllTrashBinNotesDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_delete_all)
                .setMessage(R.string.dialog_msg_delete_all)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // remove all
                        DbNoteModel.removeAllDeletedNotes();

                        // reload adapter
                        Intent mIntent = new Intent(TrashBinFragment.FILTER_BROADCAST);
                        mIntent.putExtra("reload_adapter", true);
                        getActivity().sendBroadcast(mIntent);

                        // toast
                        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        return d;
    }
}