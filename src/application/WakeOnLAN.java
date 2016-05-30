package application;

import gui.entry.ComboBoxEntry;
import gui.entry.Entry;
import gui.entry.ListPanel;
import gui.props.UIEntryProps;
import gui.props.variable.StringVariable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import statics.GU;
import ui.log.LogPanel;

/**
 * @author Daniel J. Rivers
 *         2014
 *
 * Created: Jul 4, 2014, 11:59:49 PM 
 */
public class WakeOnLAN extends JFrame {

	private static final long serialVersionUID = 1L;

	public static String IP = "IP";

	public static String PORT = "PORT";

	public static String MAC = "MAC";
	
	private UIEntryProps props = new UIEntryProps();
	
	private static String pName = "Wake On Lan";
	
	private static String NAMES = "NAMES";
	
	private List<String> comps = new ArrayList<String>();
	
	private ListPanel group;

	public WakeOnLAN() {
		this.setTitle( pName );
		this.setIconImage( new ImageIcon( getClass().getResource( "WoL.gif" ) ).getImage() );
		this.setSize( new Dimension( 400, 300 ) );
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
		this.setLayout( new BorderLayout() );
		setupProps();
		JTabbedPane tab = new JTabbedPane();
		this.add( tab, BorderLayout.CENTER );
		tab.add( "Entry", getEntryPanel() );
		tab.add( "Log", new LogPanel( pName ) );
		this.add( getButtonPanel(), BorderLayout.SOUTH );
		this.setVisible( true );
	}

	private void setupProps() {
		props.addVariable( MAC, new StringVariable( "" ) );
		props.addVariable( IP, new StringVariable( "192.168.10.255" ) );
		props.addVariable( PORT, new StringVariable( "7" ) );
		Properties p = new Properties();
		try {
			p.load( new FileInputStream( "WoL.props" ) );
			for ( String s : p.getProperty( NAMES ).split( "," ) ) {
				String n = s + " (" + p.getProperty( s ) + ")";
				comps.add( n );
				props.addVariable( n, new StringVariable( p.getProperty( s ) ) );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	private JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.setLayout( new BoxLayout( p, BoxLayout.X_AXIS ) );
		GU.hg( p );
		JButton b = new JButton( "Wake Selected" );
		GU.sizes( b, GU.FIELD );
		b.addActionListener( e -> new ProcessWrapper( pName, props, null ).execute() );
		p.add( b );
		GU.spacer( p );
		b = new JButton( "Wake Group" );
		GU.sizes( b, GU.FIELD );
		b.addActionListener( e -> new ProcessWrapper( pName, props, group.getAllItems() ).execute() );
		p.add( b );
		GU.hg( p );
		return p;
	}

	private JPanel getEntryPanel() {
		JPanel p = new JPanel();
		p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
		ComboBoxEntry e = new ComboBoxEntry( "Destination MAC Address:", props.getVariable( MAC ), new Dimension[] { GU.FIELD, new Dimension( 180, 24 ) } );
		e.setContents( comps );
		p.add( e );
		GU.spacer( p );
		group = new ListPanel( "group", false ) {
			private static final long serialVersionUID = 1L;
			public void add() {
				addItem( props.getString( props.getString( WakeOnLAN.MAC ) ) );
			}
		};
		p.add( group );
		GU.spacer( p );
		JPanel address = new JPanel();
		address.setLayout( new BoxLayout( address, BoxLayout.X_AXIS ) );
		address.add( new Entry( "IP:", props.getVariable( IP ), new Dimension( 3, 24 ), new Dimension( 3, 24 ),  new Dimension[] { GU.SQUARE, new Dimension( 90, 24 ) } ) );
		GU.spacer( address );
		address.add( new Entry( "Port:", props.getVariable( PORT ), new Dimension( 37, 24 ), GU.SQUARE, new Dimension[] { new Dimension( 35, 24 ), GU.SQUARE } ) );
		p.add( address );
		GU.spacer( p, GU.SQUARE );
		return p;
	}

	public static void main( String args[] ) {
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e ) {
			System.err.println( "Critical JVM Failure!" );
			e.printStackTrace();
		}
		new WakeOnLAN();
	}
}