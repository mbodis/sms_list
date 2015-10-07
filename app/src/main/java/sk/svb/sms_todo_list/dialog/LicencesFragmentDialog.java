package sk.svb.sms_todo_list.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.helper.MySystemHelper;


public class LicencesFragmentDialog extends DialogFragment {

	public static final String TAG = LicencesFragmentDialog.class.getName();

	public static LicencesFragmentDialog newInstance() {
		LicencesFragmentDialog frag = new LicencesFragmentDialog();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_license, null, false);


		((TextView) view.findViewById(R.id.licence_android)).setText(MySystemHelper
				.loadFile(getResources(), R.raw.license_android_support_v4));

		((TextView) view.findViewById(R.id.licence_sugarorm)).setText(MySystemHelper
				.loadFile(getResources(), R.raw.license_sugar_orm));

		
		final AlertDialog d = new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(
						getActivity().getString(R.string.licence_dialog_title))
				.setPositiveButton(R.string.ok, null).create();

		return d;
	}
}