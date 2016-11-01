
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class FTPClientSide {

	/*1.C opens a stream (TCP) server socket on port 2000 for receiving the file from S. 
	 *
	 * 2. Client sends an UDP datagram to Server at destination port 3000 with the following information:
	 * File name
	 * Host name of Client
	 * Port number of Client's server-Socket (TCP port waiting for receiving from server, 2000)
	 *
	 * 3. Server receives the datagram from Client and opens a stream (TCP) connection to Client (in the example, Server
	 * opens a connections to machine “PC45253.de.softlab.net”, destination port 2000). Server reads the wanted file
	 * from its hard disk and sends it to Client via the opened connection.
	 * 
	 * 4. Client receives the file from its stream socket and writes it to the local hard disk.
	 * 
	 * 
	 */
	private static final int SERVER_DATAGRAM_PORT = 3000;
	private static final int CLIENT_TCP_PORT = 2000;
	private static final String CLIENT_HOSTNAME = "PC45253.de.softlab.net";
	private static final String SERVER_HOSTNAME = "fileServer.com";
	private static final String FILE_PATH_TO_STORE= "C:\\message.txt";
	private static final String FILE_ON_SERVER = "myFile.txt";
	public static void main(String[] args) throws IOException {
		try (
				DatagramSocket clientSocket =
					new DatagramSocket();
				
	            ServerSocket tcpReceiveSocket =
	                new ServerSocket(CLIENT_TCP_PORT);
	            Socket clientTCPSocket = 
	            	tcpReceiveSocket.accept();     
	            PrintWriter out =
	                new PrintWriter(clientTCPSocket.getOutputStream(), true);                   
	            BufferedReader incomingFileStream = new BufferedReader(
	                new InputStreamReader(clientTCPSocket.getInputStream()));
				BufferedWriter fileWriter = new BufferedWriter(
					new FileWriter(FILE_PATH_TO_STORE));	
	        ) 
	        {
			
			//COntrol_Info
			Map<String, String> controlInfoItems = new HashMap<String, String>();
			controlInfoItems.put("fileName", FILE_ON_SERVER);
			controlInfoItems.put("clientHostName",CLIENT_HOSTNAME);
			controlInfoItems.put("clientResponsePort", String.valueOf(CLIENT_TCP_PORT));
			
			//Serialize Control_Info Map
			ByteArrayOutputStream serializedMap = new ByteArrayOutputStream();
		    ObjectOutputStream outMap = new ObjectOutputStream(serializedMap);
		    outMap.writeObject(controlInfoItems);
		    outMap.flush();
		    
		    //Send control info to Server via UDP
		    InetAddress IPAddress = InetAddress.getByName(SERVER_HOSTNAME);
		    byte[] sendData = new byte[1024];
		    sendData = serializedMap.toByteArray();
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_DATAGRAM_PORT);
			clientSocket.send(sendPacket);
			
			//Waiting for receiving the file stream from Server via TCP
			
	        }
		catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
	                + CLIENT_TCP_PORT + " or listening for a connection");
		}
		

	}

}
