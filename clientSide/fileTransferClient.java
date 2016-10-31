
import java.io.*;
import java.net.*;
//import sun.misc.IOUtils;
//import org.apache.commons.io;

class FileTransferClient {

	public static void main(String[] args) throws IOException {
        
        if (args.length != 2) 
        {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
		
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        //FileReader inputFile = new FileReader("C:/message.txt");
        File inputFile = new File("C:\\message.txt");
		System.out.println(inputFile.length());
        try (
            Socket clientSocket = new Socket(hostName, portNumber);
            OutputStream out = clientSocket.getOutputStream();
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            InputStream inputFileStream =
                new FileInputStream(inputFile);
        )
        {
        	int bytes = IOUtils.copy(inputFileStream, out);
			System.out.println("Done!" + bytes);
        	
            //String fileInput = String.format("[#                    ] 1%\r");
			/*String fileInput;
			int progress = 0;
            while ((fileInput = inputFileStream.readLine()) != null) 
            {
                out.write(fileInput);
				System.out.print("echo: " + in.readLine() + "\r");
            }*/
			
        } catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) 
        {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}
