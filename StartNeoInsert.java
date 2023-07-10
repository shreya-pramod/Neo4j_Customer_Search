package neo4jtest;

// GUI imports
import javax.swing.*; // GUI toolkit
import java.awt.*;    // Abstract Window Toolkit
import java.awt.event.*;

//Neo imports	
import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

public class StartNeoInsert extends JFrame {

  JTextField ipCustID;
  JTextField ipName;
  JTextField ipAddr1;
  JTextField ipCity;
  JTextField ipState;
  JTextField ipZip;
	JTextField msg;
	
	Driver driver = null;
  Session session = null;
  Result result = null;
  WindowListener exitListener = null;
	
	public StartNeoInsert() {
		setSize( 600, 200 );
		setLocation( 400, 500 );
		setTitle( "Neo4j Insert" );
		
		Container cont = getContentPane();
		cont.setLayout( new BorderLayout() );
		
		JButton insert = new JButton( "Insert" );
		JButton connect = new JButton( "Connect" );
		JButton clear = new JButton( "Clear" );

		ipCustID = new JTextField( 20 );
    ipName   = new JTextField( 20 );
    ipAddr1  = new JTextField( 20 );
    ipCity   = new JTextField( 20 );
    ipState  = new JTextField( 20 );
    ipZip    = new JTextField( 20 );
    msg      = new JTextField( 20 );
      
    JLabel lblCustID = new JLabel( "CustID:", JLabel.RIGHT );
    JLabel lblName   = new JLabel( "Name:", JLabel.RIGHT );
    JLabel lblAddr1  = new JLabel( "Addr1:", JLabel.RIGHT );
    JLabel lblCity   = new JLabel( "City:", JLabel.RIGHT );
    JLabel lblState  = new JLabel( "State:", JLabel.RIGHT );
    JLabel lblZip    = new JLabel( "Zip:", JLabel.RIGHT );

		//output = new JTextArea(10, 30);
		//JScrollPane spOutput = new JScrollPane(output);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout( new FlowLayout() );
		northPanel.add( connect );
    northPanel.add( msg );
		northPanel.add( insert );
		northPanel.add( clear );
		
    JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new GridLayout( 0, 2 ) );
    centerPanel.add( lblCustID );
    centerPanel.add( ipCustID );
    centerPanel.add( lblName );
    centerPanel.add( ipName );
    centerPanel.add( lblAddr1 );
    centerPanel.add( ipAddr1 );
    centerPanel.add( lblCity );
    centerPanel.add( ipCity );
    centerPanel.add( lblState );
    centerPanel.add( ipState );
    centerPanel.add( lblZip );
    centerPanel.add( ipZip );

		cont.add( northPanel, BorderLayout.NORTH );
		cont.add( centerPanel, BorderLayout.CENTER );
		
		connect.addActionListener( new ConnectNeo() );
		insert.addActionListener( new InsertNeo() );
		clear.addActionListener( new ClearNeo() );
		
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

    exitListener = new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent e ) {
        int confirm = JOptionPane.showOptionDialog(
          null, "Are You Sure to Close Application?", 
          "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
          JOptionPane.QUESTION_MESSAGE, null, null, null
        );
        
        if ( confirm == 0 ) {
            System.exit( 0 );
        }
      }
    };
      
    addWindowListener( exitListener );
		setVisible( true );
		
	
	} // StartAccessNeo
	
	public static void main( String [] args ) {      
      
      new StartNeoInsert();
	
	} // main
	
  class ConnectNeo implements ActionListener {

    public void actionPerformed( ActionEvent event ) {
      
      // Connect to the server and Neo4j database
      driver = GraphDatabase.driver( "bolt://localhost:7687",
          AuthTokens.basic( "neo4j", "student1" ) );
      
      session = driver.session( SessionConfig.forDatabase("earnings") );
      
      
      msg.setText( "Connection to server completed" );          				
    } // actionPerformed
  } // class ConnectNeo
  
  class InsertNeo implements ActionListener {

    public void actionPerformed( ActionEvent event ) {
      // In this section you should insert the new customer data
      String query = "CREATE (c: Customer { custID: $insertID, name: $insertName, " +
                        "addr1: $insertAddr, city: $insertCity, state: $insertState, " +
                        "zip: $insertZip}) ";
      
      result = session.run( query, parameters( "insertID", ipCustID.getText() ,
                                              "insertName", ipName.getText() ,
                                              "insertAddr", ipAddr1.getText() ,
                                              "insertCity", ipCity.getText() ,
                                              "insertState", ipState.getText() ,
                                              "insertZip", ipZip.getText() ) );
      System.out.println( "Customer successfully inserted" );

      query = "MATCH (c: Customer { custID: $searchID}) "+
              "RETURN c.custID as ID, c.name as custName, c.addr1 as addr, "+
                "c.city as city, c.state as state, c.zip as zip";
      
      result = session.run(query, parameters( "searchID", ipCustID.getText() ));
      
      System.out.println("\nBelow customer added: \n");
      while ( result.hasNext() ){
        org.neo4j.driver.Record record = result.next();

        System.out.println( "Customer ID: " + record.get("ID").asString() + "\n"+
                            "Name: " +record.get("custName").asString() + "\n"+
                            "Address: " +record.get("addr").asString() + "\n"+
                            "City: " +record.get("city").asString() + "\n"+
                            "State: " +record.get("state").asString() + "\n"+
                            "Zip Code: " +record.get("zip").asString() + "\n");
      }

    } // actionPerformed
  } // class GetNeo
  
  class ClearNeo implements ActionListener {
    public void actionPerformed( ActionEvent event ) {
      // Do cleanup
      msg.setText( "" );
      ipCustID.setText( "" );
      ipName.setText( "" );
      ipAddr1.setText( "" );
      ipCity.setText( "" );
      ipState.setText( "" );
      ipZip.setText( "" );
      session.close();
      driver.close();
    
    }// actionPerformed
  }// class ClearNeo

} //class
