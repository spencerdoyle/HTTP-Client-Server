import java.io.*;
import java.util.Scanner;
import java.net.*;

// Author: Spencer Doyle

public class HTTPClient {

	public static void main(String[] args) throws Exception {

		String sentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		int count;

		while ((sentence = inFromUser.readLine()) != null) {

			try {

				count = 0;

				// CREATE SOCKET

				Socket clientSocket = new Socket("comp431afa19.cs.unc.edu", Integer.parseInt(args[0]));

				///////// Establish BufferedReader to read from Server
				///////// Establish DataoutputStream to send to Server

				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

				///////// SEND TO SERVER
				outToServer.writeBytes(sentence + '\n');

				///////// PRINT RESPONSES

				String line = null;
				while ((line = inFromServer.readLine()) != null) {
					if (count == 0) {
						System.out.print(line + "\r" + "\n");
					} else {
						System.out.println(line);
					}
					count++;
				}

				// CLOSE SOCKET

				clientSocket.close();

			} catch (Exception e) {
				System.out.println("Connection Error");
				continue;
			}

		}

	}

}
