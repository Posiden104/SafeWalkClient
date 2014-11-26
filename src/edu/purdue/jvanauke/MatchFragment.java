package edu.purdue.jvanauke;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This fragment is the "page" where the application display the log from the
 * server and wait for a match.
 *
 * @author YL
 */
public class MatchFragment extends Fragment implements OnClickListener {

	private static final String DEBUG_TAG = "DEBUG";

	/**
	 * Activity which have to receive callbacks.
	 */
	private StartOverCallbackListener activity;

	/**
	 * AsyncTask sending the request to the server.
	 */
	private Client client;

	/**
	 * Coordinate of the server.
	 */
	private String host;
	private int port;

	/**
	 * Command the user should send.
	 */
	private String command;

	private TextView log;
	private TextView prompt;
	private TextView match;
	private TextView congrats;

	// Class methods
	/**
	 * Creates a MatchFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the start over
	 *            Button.
	 * @param host
	 *            address or IP address of the server.
	 * @param port
	 *            port number.
	 * 
	 * @param command
	 *            command you have to send to the server.
	 * 
	 * @return the fragment initialized.
	 */
	// ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	public static MatchFragment newInstance(StartOverCallbackListener activity, String host, int port, String command) {
		MatchFragment f = new MatchFragment();

		f.activity = activity;
		f.host = host;
		f.port = port;
		f.command = command;

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

		View view = inflater.inflate(R.layout.match_fragment_layout, container, false);

		/**
		 * Register this fragment to be the OnClickListener for the start over
		 * button.
		 */
		view.findViewById(R.id.bu_start_over).setOnClickListener(this);

		log = (TextView) view.findViewById(R.id.lblLog);
		prompt = (TextView) view.findViewById(R.id.lblMatch1);
		match = (TextView) view.findViewById(R.id.matchTxtView);
		congrats = (TextView) view.findViewById(R.id.lblCongrats);

		/**
		 * Launch the AsyncTask
		 */
		this.client = new Client();
		this.client.execute(new String[] { host, "" + port, command });

		return view;
	}

	public void error(String message) {
		// TODO: error message
		// Alert Dialog
		System.err.println(message);
	}

	public void log(String message) {
		log.append(message);
	}

	public void match(String s) {
		String s2 = s.substring(s.indexOf(" "));
		String[] response = s2.split(",");
		match.setVisibility(View.VISIBLE);
		prompt.setVisibility(View.VISIBLE);
		match.append(response[0] + "\n" + response[1] + "\n " + response[3]);
		congrats.setVisibility(View.VISIBLE);
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		/**
		 * Close the AsyncTask if still running.
		 */
		this.client.close();

		/**
		 * Notify the Activity.
		 */
		this.activity.onStartOver();
	}

	class Client extends AsyncTask<String, String, String> implements Closeable {

		/**
		 * NOTE: you can access MatchFragment field from this class:
		 * 
		 * Example: The statement in doInBackground will print the message in
		 * the Eclipse LogCat view.
		 */

		PrintWriter out;
		BufferedReader in;
		Socket s;

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected String doInBackground(String... params) {
			String result;

			Log.d(DEBUG_TAG, String.format("The Server at the address %s uses the port %d", host, port));

			Log.d(DEBUG_TAG, String.format("The Client will send the command: %s", command));

			try {
				log("> Connecting to server");

				s = new Socket(host, port);

				log("Success!\n");
				out = new PrintWriter(s.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));

				out.println(command);
				log("> Waiting for response\n");
				for (;;) {
					result = in.readLine();
					if (result.startsWith("RESPONSE: ")) {
						log("> Response recieved!\n");
						out.println(":ACK");
						match(result);
					}
					break;
				}

			} catch (IOException e) {
				error("Server could not be reached");
				e.printStackTrace();
				return "";
			}

			return "";
		}

		public void close() {
			out.close();
			try {
				in.close();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */

		// TODO: use the following method to update the UI.
		// ** DO NOT TRY TO CALL UI METHODS FROM doInBackground!!!!!!!!!! **

		/**
		 * Method executed just before the task.
		 */
		@Override
		protected void onPreExecute() {
		}

		/**
		 * Method executed once the task is completed.
		 */
		@Override
		protected void onPostExecute(String result) {
		}

		/**
		 * Method executed when progressUpdate is called in the doInBackground
		 * function.
		 */
		@Override
		protected void onProgressUpdate(String... result) {
		}
	}

}
