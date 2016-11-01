import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class FTPSrverSide {

	private static final int SERVER_DATAGRAM_PORT = 3000;
	private static final String DEFAULT_FILES_PATH = "C:\\allFiles\\";
	public static void main(String[] args) throws IOException {
		
		
		String clientHostName = "";
		int clientHostPort;
		String fileName = "";
		
		while (true) {
					
			try(
					DatagramSocket serverSocket = 
						new DatagramSocket(SERVER_DATAGRAM_PORT);
					byte[] receiveData = 
						new byte[1024];
					DatagramPacket receivePacket = 
						new DatagramPacket(receiveData, receiveData.length);
					)
			{
				
				//Receiving the UDP packet
				
				serverSocket.receive(receivePacket);
				
				try(
						//Deserializing to Map object
						ByteArrayInputStream deserializedMapObject = 
							new ByteArrayInputStream(receivePacket.getData());
						ObjectInputStream inMap = 
							new ObjectInputStream(deserializedMapObject);
						Map<String, String> controlInfoItems = 
							new HashMap<String, String>();
						)
				{
					
					
					
					//*****************how to get the map object out of this byte array
					
					
					controlInfoItems = (HashMap<String, String>)inMap.readObject();
					
					//Retrieve the control info Items 
					for (Map.Entry<String, String> entry : controlInfoItems.entrySet()) {
						switch (entry.getKey()) {
						case "fileName":
							fileName = entry.getValue();
							break;
						case "clientHostName":
							clientHostName = entry.getValue();
							break;
						case "clientResponsePort":
							clientHostPort = Integer.parseInt(entry.getValue());
						default:
							break;
						}
					}
					
					try(
							File inputFile = 
								new File(DEFAULT_FILES_PATH + fileName);
							)
					{
						//Now this is the time to go for given fileName 
						
						
						//*****************keep coding for creating the TCP socket and communication
						try(
								
								)
						{
							
						}
						catch (IOException tcpSocket) {
							System.out.println("Something went wrong with the TCP socket! \r\n" + tcpSocket.getMessage());
						}
					}
					catch (Exception fileNotFound) {
						System.out.println("File not Found! \r\n" + fileNotFound.getMessage()); 
					}
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
