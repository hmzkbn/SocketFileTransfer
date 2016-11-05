import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class FTPServerSide {

	private static final int SERVER_DATAGRAM_PORT = 4000;
	private static final String DEFAULT_FILES_PATH = "C:\\serverRepo\\";

	public FTPServerSide () throws Exception {
		String[] args = new String[0];
		main(args);
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		String clientHostName = "";
		int clientTCPPort = 0;
		String fileName = "";
		String newLine = System.getProperty("line.separator");
		while (true) {

			try {
				
				//***********UDP Connection***************************
				try (DatagramSocket serverSocket = new DatagramSocket(SERVER_DATAGRAM_PORT);

				) {
					// Receiving the UDP packet
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					System.out.println("******************************************************************");
					System.out.print("Waiting for client request...");
					serverSocket.receive(receivePacket);

					try (
							// Deserializing UDP stream to Map object
							ByteArrayInputStream udpByteStreamInput = new ByteArrayInputStream(receivePacket.getData());
							ObjectInputStream inMap = new ObjectInputStream(udpByteStreamInput);

					) {

						Map<String, String> controlInfoItems = new HashMap<String, String>();
						controlInfoItems = (HashMap<String, String>) inMap.readObject();

						// Retrieve the control info Items
						for (Map.Entry<String, String> entry : controlInfoItems.entrySet()) {

							switch (entry.getKey()) {
							case "fileName":
								fileName = entry.getValue();
								break;
							case "clientHostName":
								clientHostName = entry.getValue();
								break;
							case "clientResponsePort":
								clientTCPPort = Integer.parseInt(entry.getValue());
							default:
								break;
							}
						}
					} catch (Exception cntInfoItems) {
						throw new Exception ("Error while Control Info Item deSerialization! \r\n"
								+ cntInfoItems.getMessage());
						//Will be caught by udpSocket

					}
				} catch (Exception udpSocket) {
					throw new Exception("Something went wrong with the UDP socket! \r\n" + udpSocket.getMessage());
					//Will be caught by overall
				}

							
				// *****************Locating requested File and sending via TCP connection**************
				
				// Check if the control information is complete
				if (fileName != null && !fileName.isEmpty() && clientHostName != null && !clientHostName.isEmpty()
						&& clientTCPPort != 0)

				{
					System.out.println(newLine + "Heyy...A new request is received... \r\n" + "from: " + clientHostName + ":"
							+ clientTCPPort + "\r\n" + "For: " + fileName + newLine);
					File ClientRequestedFile = null;
					
					try {
						ClientRequestedFile = new File(DEFAULT_FILES_PATH + fileName);
					} catch (Exception fileNotFound) {
						throw new Exception("Error: Requested File was not found!");
						//Will be caught by overall
					}

					InetAddress clientIPAddr = InetAddress.getByName(clientHostName);
					
					//**********sending file content via TCP connection*****************
					try (
							Socket clientSocket = new Socket(clientIPAddr, clientTCPPort);
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(ClientRequestedFile));
							BufferedOutputStream outputSocketStream = new BufferedOutputStream(clientSocket.getOutputStream());

					) {

						byte[] buffer = new byte[(int) ClientRequestedFile.length()];
						bis.read(buffer, 0, buffer.length);
						outputSocketStream.write(buffer, 0, buffer.length);
						outputSocketStream.flush();
						System.out.println("File has been successfully sent!" + newLine);
						System.out.println("******************************************************************");

					} catch (Exception socketOrFileInputStreamError) {
						throw new Exception(socketOrFileInputStreamError);
						//Will be caught by overall
					}

				} else
					throw new Exception("Missing control Information!");

			} catch (Exception overall) {
				System.out.println("Oops...." + overall.getMessage());
			}
		}

	}

}
