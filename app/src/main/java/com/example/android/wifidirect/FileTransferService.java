

package com.example.android.wifidirect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/*
  A service that process each file transfer request i.e Intent by opening a
  socket connection with the WiFi Direct Host
 */
public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
	//public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_NUMBER_STREAM = "number_stream";
	public static final String EXTRAS_ADDRESS = "go_host";
	public static final String EXTRAS_PORT = "go_port";

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = getApplicationContext();
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String line = intent.getExtras().getString(EXTRAS_NUMBER_STREAM);
			String host = intent.getExtras().getString(EXTRAS_ADDRESS);
			Socket socket = new Socket();
			int port = intent.getExtras().getInt(EXTRAS_PORT);

			try {
				Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
				socket.setReuseAddress(true);
                socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
				Log.d(WiFiDirectActivity.TAG, "Client sock" +
                        "et - " + socket.isConnected());

                BufferedWriter ostream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ostream.write(line);
                ostream.flush();
                ostream.close();
				//ContentResolver cr = context.getContentResolver();
				//BufferedReader istream = new BufferedReader(new InputStreamReader(socket.getInputStream()));



				//DeviceDetailFragment.copyFile(istream, ostream);
				Log.d(WiFiDirectActivity.TAG, "Client: Data written");
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
                            Log.d("FileTransferService", "Client socket closed");
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
