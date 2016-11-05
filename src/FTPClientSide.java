
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

public class FTPClientSide {

	/*
	 * 1.C opens a stream (TCP) server socket on port 2000 for receiving the
	 * file from S.
	 *
	 * 2. Client sends an UDP datagram to Server at destination port 4000 with
	 * the following information: File name Host name of Client Port number of
	 * Client's server-Socket (TCP port waiting for receiving from server, 2000)
	 *
	 * 3. Server receives the datagram from Client and opens a stream (TCP)
	 * connection to Client (in the example, Server opens a connections to
	 * machine “PC45253.de.softlab.net”, destination port 2000). Server reads
	 * the wanted file from its hard disk and sends it to Client via the opened
	 * connection.
	 * 
	 * 4. Client receives the file from its stream socket and writes it to the
	 * local hard disk.
	 * 
	 * 
	 */
	private static final int SERVER_DATAGRAM_PORT = 4000;
	private static final int CLIENT_TCP_PORT = 2000;
//	private static final String CLIENT_HOSTNAME = "PC45253.de.softlab.net";
//	private static final String SERVER_HOSTNAME = "fileServer.com";
	private static final String CLIENT_HOSTNAME = "localhost";
	private static final String SERVER_HOSTNAME = "localhost";
	private static  String PATH_TO_STORE = "C:\\clientRepo\\";
	private static  String FILE_ON_SERVER;
	
	static void copyStream(InputStream in, OutputStream out, int buffer) throws IOException {
        byte[] buf = new byte[buffer];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
	
	static void copyStream(BufferedInputStream  in, BufferedOutputStream  out, int buffer) throws IOException {
        byte[] buf = new byte[buffer];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
	

	public static void main(String[] args) throws IOException {
		String newLine = System.getProperty("line.separator");
		
		//*********get file name from user************
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.print("Enter a file name: ");
		FILE_ON_SERVER = reader.next();
		reader.close();
		
		try {
						
			//***********Sending control info via UDP connection***************
			try (DatagramSocket clientSocket = new DatagramSocket();

			) {
				
				// Control_Info

				Map<String, String> controlInfoItems = new HashMap<String, String>();

				// System.out.println("Size of HashMap : " +
				// ObjectSizeFetcher.getObjectSize(controlInfoItems));
				controlInfoItems.put("fileName", FILE_ON_SERVER);
				controlInfoItems.put("clientHostName", CLIENT_HOSTNAME);
				controlInfoItems.put("clientResponsePort", String.valueOf(CLIENT_TCP_PORT));
				// System.out.println("Size of HashMap after addition : " +
				// ObjectSizeFetcher.getObjectSize(controlInfoItems));

				// Serialize Control_Info Map
				ByteArrayOutputStream serializedMap = new ByteArrayOutputStream();
				ObjectOutputStream outMap = new ObjectOutputStream(serializedMap);
				outMap.writeObject(controlInfoItems);
				outMap.flush();

				// Send control info to Server via UDP
				InetAddress IPAddress = InetAddress.getByName(SERVER_HOSTNAME);
				// System.out.println(IPAddress.toString());
				byte[] sendData = new byte[1024];
				sendData = serializedMap.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
						SERVER_DATAGRAM_PORT);
				clientSocket.send(sendPacket);
				
				System.out.println(newLine + "Request is sent... \r\n" + "To: " + SERVER_HOSTNAME + ":" + SERVER_DATAGRAM_PORT +
						"\r\n" + "For: " + FILE_ON_SERVER);
				System.out.print(newLine + "Waiting for receiving the file(" + FILE_ON_SERVER + ")...");
			} catch (IOException udpConnection) {
				throw new Exception("Error in sending UDP packet!\r\n" + udpConnection.getMessage());
			}

			//*****************waiting for TCP message to download the file
			try (ServerSocket tcpReceiveSocket = new ServerSocket(CLIENT_TCP_PORT);

					Socket clientTCPSocket = tcpReceiveSocket.accept();

					BufferedInputStream incomingStream = new BufferedInputStream(clientTCPSocket.getInputStream());

					
			) {
				System.out.println(incomingStream.available());
				if(incomingStream.available() > 0)
				{
					try(
							BufferedOutputStream toFileStream = new BufferedOutputStream(new FileOutputStream(PATH_TO_STORE + FILE_ON_SERVER));
							)
					{
						System.out.println(newLine + "File is being downloaded...");
						copyStream(incomingStream, toFileStream, 8192);
					}
					catch (IOException clientFile) {
						throw new Exception("Could not create the file! \r\n" + clientFile.getMessage());
					}
				}
				else
					throw new Exception("Error: Input Stream is empty! \r\n");
				
				System.out.println("File downloaded succesfully and saved under (" + PATH_TO_STORE + FILE_ON_SERVER + ")" + newLine);
				Runtime.getRuntime().exec("explorer.exe /select," + PATH_TO_STORE + FILE_ON_SERVER);
				
			} catch (Exception socketOrFileInputStreamError) {
				throw new Exception(socketOrFileInputStreamError);
			}
		} catch (Exception overall) {
			System.out.println("Ooops...." + overall.getMessage());
		}

	}

}
