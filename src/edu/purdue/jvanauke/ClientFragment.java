package edu.purdue.jvanauke;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * This fragment is the "page" where the user inputs information about the
 * request, he/she wishes to send.
 *
 * @author Joel Van Auken <jvanauke@purdue.edu>
 */
public class ClientFragment extends Fragment implements OnClickListener {

	/**
	 * Activity which have to receive call backs.
	 */
	private SubmitCallbackListener activity;

	/**
	 * Views from the fragment
	 */

	public EditText name;
	public RadioGroup radGrp;
	public RadioButton req;
	public RadioButton vol;
	public RadioButton noPref;
	public Spinner from;
	public Spinner to;

	/**
	 * Creates a ProfileFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the submit Button.
	 * 
	 *            ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	 * 
	 * @return the fragment initialized.
	 */
	// ** DO NOT CREATE A CONSTRUCTOR FOR ProfileFragment **
	public static ClientFragment newInstance(SubmitCallbackListener activity) {
		ClientFragment f = new ClientFragment();

		f.activity = activity;
		return f;
	}

	/**
	 * Called when the fragment will be displayed.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View view = inflater.inflate(R.layout.client_fragment_layout, container, false);

		/**
		 * Register this fragment to be the OnClickListener for the submit
		 * Button.
		 */
		view.findViewById(R.id.bu_submit).setOnClickListener(this);

		this.name = (EditText) view.findViewById(R.id.edtTxtName);
		this.radGrp = (RadioGroup) view.findViewById(R.id.radGrpType);
		this.vol = (RadioButton) view.findViewById(R.id.radVol);
		this.req = (RadioButton) view.findViewById(R.id.radReq);
		this.noPref = (RadioButton) view.findViewById(R.id.radNoPref);
		this.from = (Spinner) view.findViewById(R.id.spinFrom);
		this.to = (Spinner) view.findViewById(R.id.spinTo);

		return view;
	}

	public int getType() {
		if (req.isChecked()) {
			return 1;
		} else if (vol.isChecked()) {
			return 2;
		} else {
			return 0;
		}
	}

	public String getFrom() {
		switch (from.getSelectedItemPosition()) {
		case 0:
			return "CL50";
		case 1:
			return "EE";
		case 2:
			return "LWSN";
		case 3:
			return "PMU";
		case 4:
			return "PUSH";
		default:
			return "";
		}
	}

	public String getTo() {
		switch (to.getSelectedItemPosition()) {
		case 0:
			return "CL50";
		case 1:
			return "EE";
		case 2:
			return "LWSN";
		case 3:
			return "PMU";
		case 4:
			return "PUSH";
		case 5:
			return "*";
		default:
			return "";
		}
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		this.activity.onSubmit();
	}
}
