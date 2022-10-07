import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender 
{
	static InetAddress IP_ADDRESS;
	static int RCV_PORT;
	static int SND_PORT;
	static String FILENAME;
	static int MDS;
	static int TIMEOUT;
	
	public static void main(String[] args) 
	{
		// Command Line Arguments		
		try { 
		IP_ADDRESS = InetAddress.getByName(args[0]);} catch (UnknownHostException e1) { System.out.println("Unknown Host");} 											// Ip Address	
		RCV_PORT = Integer.parseInt(args[1]); 	// Receiver Port	
		SND_PORT = Integer.parseInt(args[2]); 	// Sender Port			
		FILENAME = args[3]; 					// FILENAME	
		MDS = Integer.parseInt(args[4]); 		// Max Data Size			
		TIMEOUT = Integer.parseInt(args[5]);	// Timeout	
		
		try 
		{
			// Gets array of byte from the text file
			byte[] msg = getMsgBtyeArray();
			// Sends the entire message
			
			long startTime = System.currentTimeMillis();
			sendMsg(msg);
			long endTime = System.currentTimeMillis();

			long ttt = (endTime - startTime) * 1000;
			
			System.out.println(ttt);
		}
		
		catch (FileNotFoundException e) { System.out.println("File Not Found"); } 
		catch (IOException e) 			{ System.out.println("IO Error"); }
		
	}
	
	private static byte[] getMsgBtyeArray() throws FileNotFoundException
	{
		// Read message as string from file
		String file_string = "";
		Scanner s = new Scanner(new File(FILENAME));
		while (s.hasNextLine()) file_string = file_string + s.nextLine();
		
		// Converts the string in the file to a byte array
		byte[] byte_array = file_string.getBytes();
		
		return byte_array;
	}

	private static void sendMsg(byte[] msg_byte_array) throws IOException 
	{
		// Packet to be sent (set later)
		DatagramPacket pkt = null;
		// Socket used to send and receive
		DatagramSocket socket = new DatagramSocket(SND_PORT);
		// Sets the timeout for socket.receive() (divided by 1000 to convert milliseconds to microseconds)
		socket.setSoTimeout(TIMEOUT/1000);
		
		// Index within "msg_byte_array"
		int i_mba = 0; 
		// While there is still data to send
		while (i_mba < msg_byte_array.length)
		{
			// Create the byte array the packet will carry
			byte[] packet_byte_array = new byte[MDS + 1];
			
			// Adds sequence number
			if(i_mba % 2 == 0) packet_byte_array[0] = (byte) 0; 
			else packet_byte_array[0] = (byte) 1;
			
			// Index within "packet_byte_array"
			int i_pba = 1;
			// Fills packet_byte_array with bytes from msg_byte_array (max "MDS" bytes)
			while (i_pba < MDS + 1 && i_mba < msg_byte_array.length)
			{
				packet_byte_array[i_pba] = msg_byte_array[i_mba];
				i_pba++; i_mba++;
			}
			
			// Create packet with proper data and destination
			pkt = new DatagramPacket(packet_byte_array,packet_byte_array.length,IP_ADDRESS,RCV_PORT);
			
			// Empty packet, will be set to whatever we receive later
			DatagramPacket get_ack = new DatagramPacket(new byte[MDS],MDS);
			
			// Stop and wait for ACK
			boolean ack = false;
			while (!ack)
			{
				// Sends a single packet
				socket.send(pkt);
				try 
				{ 
					// Waits to receive an acknowledgment, if so "ack" is set to true
					socket.receive(get_ack); 
					ack = true;
				}
				// If receive times out it will throw an error caught here
				catch (Exception e) 
				{
					System.out.println("Timed Out!");
					// This will go back to top of loop to send again
					continue;
				}
			}
			// Done sending single packet
		}
		// Done sending all packets
		socket.close();
	}
}
