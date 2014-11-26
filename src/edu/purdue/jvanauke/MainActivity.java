package edu.purdue.jvanauke;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SubmitCallbackListener, StartOverCallbackListener {

	/**
	 * The ClientFragment used by the activity.
	 */
	private ClientFragment clientFragment;

	/**
	 * The ServerFragment used by the activity.
	 */
	private ServerFragment serverFragment;

	/**
	 * UI component of the ActionBar used for navigation.
	 */
	private Button left;
	private Button right;
	private TextView title;
	
	private String name;
	private String from;
	private String to;
	private int type;

	/**
	 * Called once the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		this.clientFragment = ClientFragment.newInstance(this);
		this.serverFragment = ServerFragment.newInstance();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Creates the ActionBar: - Inflates the layout - Extracts the components
	 */
	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar, null);

		// Set up the ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

		// Extract the UI component.
		this.title = (TextView) actionBarLayout.findViewById(R.id.tv_title);
		this.left = (Button) actionBarLayout.findViewById(R.id.bu_left);
		this.right = (Button) actionBarLayout.findViewById(R.id.bu_right);
		this.right.setVisibility(View.INVISIBLE);

		return true;
	}

	/**
	 * Callback function called when the user click on the right button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onRightClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.client));
		this.left.setVisibility(View.VISIBLE);
		this.right.setVisibility(View.INVISIBLE);
		ft.replace(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Callback function called when the user click on the left button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onLeftClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.server));
		this.left.setVisibility(View.INVISIBLE);
		this.right.setVisibility(View.VISIBLE);
		ft.replace(R.id.fl_main, this.serverFragment);
		ft.commit();

	}

	/**
	 * Callback function called when the user click on the submit button.
	 */
	@Override
	public void onSubmit() {
		name = clientFragment.name.getText().toString();
		type = clientFragment.getType();
		from = clientFragment.getFrom();
		to = clientFragment.getTo();
		
		if(name == null || name.equals("") || name.contains(",")) {
			error("Your name is invalid");
		}
		if(type < 0 || type > 2) {
			error("You must select the type of person you are");
			System.out.println(type);
			return;
		}
		if(from == null || from.equals("")) {
			error("You need to select where you are");
			return;
		}
		if(to == null || to.equals("")) {
			error("You need to select where you are");
			return;
		} else if(to.equals(from)) {
			error("You cannot pick the same location as where you are");
			return;
		} else if(type == 1 && to.equals("*")) {
			error("If you are a requester, you need to pick a location to travel to");
			return;
		}
		
		// Server info
		String host = this.serverFragment.getHost(getResources().getString(R.string.default_host));
		int port = this.serverFragment.getPort(Integer.parseInt(getResources().getString(R.string.default_port)));

		if(host.contains(",") || host.contains(" ")) {
			error("Your host name is invalid");
			return;
		}
		if(port < 1 || port > 65535) {
			error("Your port is out of range.\nPlease select a port in the range [1,65535]");
			return;
		}
		
		// TODO: Need to get command from client fragment
		String command = this.getResources().getString(R.string.default_command);

		command = String.format(Locale.US, "%s,%s,%s,%d", name, from, to, type);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(getResources().getString(R.string.match));
		this.left.setVisibility(View.INVISIBLE);
		this.right.setVisibility(View.INVISIBLE);

		MatchFragment frag = MatchFragment.newInstance(this, host, port, command);

		ft.replace(R.id.fl_main, frag);
		ft.commit();
	}
	
	public void error(String message) {
		//TODO: throw up toast
		//AlertDialog
		System.err.println(message);
	}

	/**
	 * Callback function call from MatchFragment when the user want to create a
	 * new request.
	 */
	@Override
	public void onStartOver() {
		onRightClick(null);
	}

}
