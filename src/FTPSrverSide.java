import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class FTPSrverSide {

	private static final int SERVER_DATAGRAM_PORT = 3000;
	private static final String DEFAULT_FILES_PATH = "C:\\allFiles\\";
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		
		
		String clientHostName;
		int clientHostPort;
		String fileName;
		
		while (true) {
					
			try(
					DatagramSocket serverSocket = new DatagramSocket(SERVER_DATAGRAM_PORT);
					
					)
			{
				
				//Receiving the UDP packet
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				try(
						//Deserializing UDP stream to Map object
						ByteArrayInputStream udpByteStreamInput = new ByteArrayInputStream(receivePacket.getData());
						
						ObjectInputStream inMap = new ObjectInputStream(udpByteStreamInput);
						
						)
				{
					
					
					
					//*****************how to get the map object out of this byte array
					
					
					//found solution
					//HashMap y = (HashMap<Integer, String>)x;
					Map<String, String> controlInfoItems = new HashMap<String, String>();
					controlInfoItems = (HashMap<String, String>)inMap.readObject();
					
					//Retrieve the control info Items 
					for (Map.Entry<String, String> entry : controlInfoItems.entrySet()) {
						switch (entry.getKey()) {
						case "fileName":
							fileName = entry.getValue();
							System.out.println(fileName);
							break;
						case "clientHostName":
							clientHostName = entry.getValue();
							System.out.println(clientHostName);
							break;
						case "clientResponsePort":
							clientHostPort = Integer.parseInt(entry.getValue());
							System.out.println(clientHostPort);
						default:
							break;
						}
					}
					
					
					
					//Locating requested file
//					try
//					{
//						File inputFile = new File(DEFAULT_FILES_PATH + fileName);
//						//Now this is the time to go for given fileName 
//						
//						
//						//*****************keep coding for creating the TCP socket and communication
//						try
//						{
//							System.out.println("Hi");
//						}
//						catch (IOException tcpSocket) {
//							System.out.println("Something went wrong with the TCP socket!\n" + tcpSocket.getMessage());
//						}
//						
//					}
//					catch (Exception fileNotFound) {
//						System.out.println("File not Found! \r\n" + fileNotFound.getMessage()); 
//					}
					
				}
				catch (Exception cntInfoItems) {
					System.out.println("Something went wrong with Control Info Items deSerialization! \r\n" + cntInfoItems.getMessage());
					
				}
				finally {
					
				}
				
			}
			catch (IOException udpSocket) {
				System.out.println("Something went wrong with the udp socket! \r\n" + udpSocket.getMessage());
			}
		}
		

	}

}
