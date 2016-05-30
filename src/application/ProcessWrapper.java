package application;

import gui.props.UIEntryProps;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import process.NonStandardProcess;

/**
 * @author Daniel J. Rivers
 *         2014
 *
 * Created: Jul 5, 2014, 1:04:54 AM 
 */
public class ProcessWrapper extends NonStandardProcess {

	private UIEntryProps props;
	
	private List<String> comps;
	
	public ProcessWrapper( String name, UIEntryProps props, List<String> comps ) {
		super( name );
		this.comps = comps;
		this.props = props;
	}
	
	@Override
	public void execute() {
		wakeup();
	}
	
	private void wakeup() {
		sendMessage( "Attempting to send..." );
		List<String> macs = new ArrayList<String>();		
		if ( comps != null ) {
			comps.forEach( s -> macs.add( s ) );
		} else {
			macs.add( props.getString( props.getString( WakeOnLAN.MAC ) ) );
		}
		try ( DatagramSocket socket = new DatagramSocket() ) {
			for ( String macAdd : macs ) {
				byte[] bytes = getWOLBytes( getMacBytes( macAdd ) );
				InetAddress address = InetAddress.getByName( props.getString( WakeOnLAN.IP ) );
				socket.send( new DatagramPacket( bytes, bytes.length, address, Integer.parseInt( props.getString( WakeOnLAN.PORT ) ) ) );
				sendMessage( "Wake-on-LAN packet sent: " + macAdd + " (" + address.getHostName() + ")" );
				if ( macs.size() > 1 ) {
					Thread.sleep( 500 );
				}
			}
		} catch ( Exception e ) {
			sendMessage( "Failed to send Wake-on-LAN packet: " + e.getMessage() );
		}
	}
	
	private byte[] getWOLBytes( byte[] macBytes ) {
		byte[] bytes = new byte[6 + 16 * macBytes.length];
		for ( int i = 0; i < 6; i++ ) {
			bytes[ i ] = (byte)0xff;
		}
		for ( int i = 6; i < bytes.length; i += macBytes.length ) {
			System.arraycopy( macBytes, 0, bytes, i, macBytes.length );
		}
		return bytes;
	}

	private byte[] getMacBytes( String macStr ) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split( "(\\:|\\-)" );
		if ( hex.length != 6 ) {
			throw new IllegalArgumentException( "Invalid MAC address." );
		}
		try {
			for ( int i = 0; i < 6; i++ ) {
				bytes[ i ] = (byte)Integer.parseInt( hex[ i ], 16 );
			}
		} catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "Invalid hex digit in MAC address." );
		}
		return bytes;
	}

}
