
public class ftpThread extends Thread{

	private FTPServerSide inServer;
	private FTPClientSide inClient;
	
	public ftpThread(FTPClientSide client) throws Exception {
		System.out.println("client constructor of ftpThread is called!");
		inClient = client;
	}
	
	public ftpThread(FTPServerSide server) throws Exception{
		System.out.println("server constructor of ftpThread is called!");
		inServer = server;
	}
	
	@Override
	   public void run()
	   {
		try{
		String[] args = new String[0];
		if(inServer != null)
			inServer.main(args);
		else
			inClient.main(args);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	   }
}
