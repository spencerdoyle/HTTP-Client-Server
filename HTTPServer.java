import java.io.*;
import java.util.Scanner;
import java.net.*;

// Author: Spencer Doyle

public class HTTPServer {

	public static void main(String[] args) throws Exception {

		while (true) {

			try {

				String request;

				ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));

				while (true) {

					// LISTEN FOR SOCKET CONNECTION

					Socket connectionSocket = welcomeSocket.accept();

					// READ LINE FROM SOCKET

					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

					request = inFromClient.readLine();

					///////////////////////// PARSE LINE
					///////////////////////// //////////////////////////////

					int index_of_first_space = request.indexOf(" ");

					outToClient.writeBytes(request + "\n");

					String[] tokens = request.split("\\s+"); // expression for
																// one
																// or
																// more
																// whitespaces

					String method = tokens[0];
					String abs_path = tokens[1];
					String http_version = tokens[2];

					if (tokens.length != 3) {
						if (index_of_first_space == 0) { // checks for space
															// before
															// GET
							outToClient.writeBytes("ERROR -- Invalid Method token." + "\n");
							connectionSocket.close();
							continue;
						} else { // checks for remaining unwanted tokens
							outToClient.writeBytes("ERROR -- Spurious token before CRLF." + "\n");
							connectionSocket.close();
							continue;
						}
					}

					// CHECK "GET"

					if (method.compareTo("GET") != 0) {
						outToClient.writeBytes("ERROR -- Invalid Method token." + "\n");
						connectionSocket.close();
						continue;
					}

					// VALIDATE ABS_PATH
					String path_minus_first_slash;
					String file_type;

					if (abs_path.compareTo("/") == 0) {
						path_minus_first_slash = "";
						file_type = "";
					} else {
						String slash_check = abs_path.substring(0, 1);
						int last_idx = abs_path.length();
						path_minus_first_slash = abs_path.substring(1, last_idx);
						int last_dot = abs_path.lastIndexOf('.');

						if (last_dot == -1) {
							file_type = "";
						} else {
							file_type = abs_path.substring(last_dot, last_idx);
						}

						if (slash_check.compareTo("/") != 0) {
							outToClient.writeBytes("ERROR -- Invalid Absolute-Path token." + "\n");
							connectionSocket.close();
							continue;
						}

						if (!abs_path.matches("[a-zA-Z0-9_./]+")) {
							outToClient.writeBytes("ERROR -- Invalid Absolute-Path token." + "\n");
							connectionSocket.close();
							continue;
						}
					}

					// CHECK HTTP VERSION

					String[] http_split = http_version.split("/");

					if (http_split.length != 2) {
						outToClient.writeBytes("ERROR -- Invalid HTTP-Version token." + "\n");
						connectionSocket.close();
						continue;
					}

					String http = http_split[0];
					String version = http_split[1];

					if (http.compareTo("HTTP") != 0) {
						outToClient.writeBytes("ERROR -- Invalid HTTP-Version token." + "\n");
						connectionSocket.close();
						continue;
					}

					// check digits

					if (!version.matches("[0-9][.][0-9]")) {
						outToClient.writeBytes("ERROR -- Invalid HTTP-Version token." + "\n");
						connectionSocket.close();
						continue;
					}

					// print arguments
					outToClient.writeBytes("Method = " + method + "\n");
					outToClient.writeBytes("Request-URL = " + abs_path + "\n");
					outToClient.writeBytes("HTTP-Version = " + http_version + "\n");

					// open file if all is valid

					if (file_type.compareToIgnoreCase(".html") == 0 | file_type.compareToIgnoreCase(".htm") == 0
							| file_type.compareToIgnoreCase(".txt") == 0) {

						BufferedReader in;
						try {
							in = new BufferedReader(new FileReader(path_minus_first_slash));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							outToClient.writeBytes("404 Not Found: " + abs_path + "\n");
							connectionSocket.close();
							continue;
						}

						String line;

						try {
							while ((line = in.readLine()) != null) {
								outToClient.writeBytes(line + "\n");
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							outToClient.writeBytes("ERROR: " + e + "\n");
							connectionSocket.close();
							continue;
						}
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							outToClient.writeBytes("ERROR: " + e + "\n");
							connectionSocket.close();
							continue;
						}

					} else {
						outToClient.writeBytes("501 Not Implemented: " + abs_path + "\n");
					}

					///////////////////////// END PARSE LINE
					///////////////////////// /////////////////////////

					connectionSocket.close(); // CLOSE CONNECTION REDO THIS
												// BEFORE
												// CONTINUE STATEMENTS

				}

			} catch (Exception e) {
				System.out.println("Connection Error");
			}

		} // close

	}

}
