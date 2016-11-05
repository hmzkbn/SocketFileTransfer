import java.io.IOException;

public class Mixed {

	public static void main(String[] args) {
		try{
			
			
			ftpThread tServer = new ftpThread(new FTPServerSide());
			
			ftpThread tclient = new ftpThread(new FTPClientSide());
			
			System.out.println("inja");
			tServer.run();
			tclient.run();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
