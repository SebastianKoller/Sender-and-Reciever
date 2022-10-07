import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Reciever 
{	
	// GUI
	private JFrame frmAReceiver;
	private JTextField ipTextField;
	private JTextField rPortTextField;
	private JTextField sPortTextField;
	private JTextField fnTextField;
	private JLabel rcvPacketCount = new JLabel("0");
	private JCheckBox checkBox = new JCheckBox("Unreliable Transfer (Drop every 10th packet)");
	private JButton rcvButton = new JButton("Receive");
	
	// VARIABLES
	InetAddress ip_address;
	int rcv_port;
	int snd_port;
	String filename;
	
	boolean reliable_transfer = true;
	
	// CONSTRUCTOR
	public Reciever() 
	{
		this.initialize();
		this.listeners();
		frmAReceiver.setVisible(true);
	}
	
	// MAIN
	public static void main(String[] args) { Reciever r = new Reciever(); }
	
	// FUNCTIONS
	public void receiveMsg() throws IOException
	{
		// Packet to be receive (set later)
		DatagramPacket pkt_msg = null;
		// Socket used to send and receive
		DatagramSocket socket = new DatagramSocket(rcv_port);
		// Receive the packet
		socket.receive(pkt_msg);
		// Get seq# of msg
		byte[] pkt_ack = {pkt_msg.getData()[0]};
		// Send ACK with seq#
		socket.send(new DatagramPacket((pkt_ack) ,1));
	}
	
	// SETUP GUI
	private void initialize() 
	{
		frmAReceiver = new JFrame();
		frmAReceiver.setResizable(false);
		frmAReceiver.setTitle("A1-45 Receiver");
		frmAReceiver.setBounds(100, 100, 330, 295);
		frmAReceiver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAReceiver.getContentPane().setLayout(null);
		
		ipTextField = new JTextField();
		ipTextField.setBounds(170, 26, 130, 27);
		frmAReceiver.getContentPane().add(ipTextField);
		ipTextField.setColumns(10);
		
		JLabel rPortLabel = new JLabel("Port Number");
		rPortLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rPortLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rPortLabel.setBounds(10, 63, 150, 30);
		frmAReceiver.getContentPane().add(rPortLabel);
		
		rPortTextField = new JTextField();
		rPortTextField.setColumns(10);
		rPortTextField.setBounds(170, 66, 130, 27);
		frmAReceiver.getContentPane().add(rPortTextField);
		
		sPortTextField = new JTextField();
		sPortTextField.setColumns(10);
		sPortTextField.setBounds(170, 106, 130, 27);
		frmAReceiver.getContentPane().add(sPortTextField);
		
		fnTextField = new JTextField();
		fnTextField.setColumns(10);
		fnTextField.setBounds(170, 146, 130, 27);
		frmAReceiver.getContentPane().add(fnTextField);
		
		JLabel ipLabel = new JLabel("IP Address");
		ipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ipLabel.setBounds(10, 26, 150, 30);
		frmAReceiver.getContentPane().add(ipLabel);
		
		JLabel sPortLabel = new JLabel("Sender's Port Number");
		sPortLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sPortLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		sPortLabel.setBounds(10, 103, 150, 30);
		frmAReceiver.getContentPane().add(sPortLabel);
		
		JLabel filenameLabel = new JLabel("Message Filename");
		filenameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		filenameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		filenameLabel.setBounds(10, 143, 150, 30);
		frmAReceiver.getContentPane().add(filenameLabel);
		
		JPanel comPanel = new JPanel();
		comPanel.setBorder(new TitledBorder(null, "Communication", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comPanel.setBounds(10, 10, 296, 177);
		frmAReceiver.getContentPane().add(comPanel);
		
		JLabel rcvPacketLabel = new JLabel("Received Packets:");
		rcvPacketLabel.setHorizontalAlignment(SwingConstants.LEFT);
		rcvPacketLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rcvPacketLabel.setBounds(154, 197, 100, 30);
		frmAReceiver.getContentPane().add(rcvPacketLabel);
		
		rcvButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rcvButton.setBounds(40, 197, 100, 30);
		frmAReceiver.getContentPane().add(rcvButton);
		
		rcvPacketCount.setBackground(new Color(240, 240, 240));
		rcvPacketCount.setHorizontalAlignment(SwingConstants.CENTER);
		rcvPacketCount.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rcvPacketCount.setBounds(254, 197, 56, 30);
		frmAReceiver.getContentPane().add(rcvPacketCount);
		
		JCheckBox checkBox = new JCheckBox("Unreliable Transfer (Drop every 10th packet)");
		checkBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
		checkBox.setBounds(56, 233, 244, 21);
		frmAReceiver.getContentPane().add(checkBox);
	}

	private void listeners() 
	{
		rcvButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				try { ip_address = InetAddress.getByName(ipTextField.getText()); } 
				catch (UnknownHostException e) { e.printStackTrace(); }
				
				rcv_port = Integer.parseInt(rPortTextField.getText());
				snd_port = Integer.parseInt(sPortTextField.getText());
				filename = fnTextField.getText();
				
				try {receiveMsg();} catch(Exception e) {e.printStackTrace();}
			}
		});
		
		checkBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if (reliable_transfer) reliable_transfer = false;
				else reliable_transfer = true;
			}
		});

	}
}
