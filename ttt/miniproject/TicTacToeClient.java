/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ttt.miniproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



 // Client that let a user play Tic-Tac-Toe with another across a network.
 
 import java.awt.BorderLayout;
import java.awt.Color;
 import java.awt.Dimension;
import java.awt.Font;
 import java.awt.Graphics;
import java.awt.GridLayout;
 import java.awt.event.MouseAdapter;
 import java.awt.event.MouseEvent;
 import java.net.Socket;
 import java.net.InetAddress;
 import java.io.IOException;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.SwingUtilities;
 import java.util.Formatter;
 import java.util.Scanner;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ExecutorService;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
/*<applet code="TicTacToeClient" width=400 height=300></applet>*/

public class TicTacToeClient extends JFrame implements Runnable 
 { 
   private JTextField idField; 
   private JTextArea displayArea;
    private JPanel boardPanel; 
    private JPanel panel2; 
    private Square board[][];
    private Square currentSquare;
    private Socket connection; 
    private Scanner input;
    private Formatter output;
    private String ticTacToeHost; 
    private String myMark; 
    private boolean myTurn;
    private final String X_MARK = "X"; 
    private final String O_MARK = "O"; 
    private Color c;
    
    public TicTacToeClient( String host )
    {
       ticTacToeHost = host; 
       displayArea = new JTextArea( 7, 30 );
       displayArea.setBackground(new Color(255,255,204));
       displayArea.setFont(new Font("Comic Sans MS",Font.BOLD,16));
       displayArea.setForeground(new Color(0,0,204));
       displayArea.setEditable( false );
       
       add( new JScrollPane( displayArea ), BorderLayout.SOUTH );
       boardPanel = new JPanel();
      boardPanel.setLayout( new GridLayout( 3, 3, 0, 0 ) );

       board = new Square[ 3 ][ 3 ];

      
       for ( int row = 0; row < board.length; row++ )
       {
                for ( int column = 0; column < board[ row ].length; column++ )
          {
      
             board[ row ][ column ] = new Square(  " ",row * 3 + column);
             boardPanel.add( board[ row ][ column ] ); // add square
           } 
       } 

       idField = new JTextField();
       idField.setEditable( false ); 
       idField.setBackground(Color.black);
       idField.setFont(new Font("Comic Sans MS",Font.ITALIC,15));
       idField.setForeground(Color.white);
       
       add( idField, BorderLayout.NORTH ); 

      panel2 = new JPanel();  
       panel2.add( boardPanel, BorderLayout.CENTER );
       add( panel2, BorderLayout.CENTER ); 

       setSize( 500, 490 ); 
       setVisible( true );  
      panel2.setBackground(Color.black);
       startClient(); 
        
    } 

   
    public void startClient() 
    { 
       try  
      { 
          // make connection to server 
          connection = new Socket(                      
             InetAddress.getLocalHost( ),12345);
             // connection = new Socket(" 127.0.0.1 ",12345);
          // get streams for input and output 
         input = new Scanner( connection.getInputStream() );    
          output = new Formatter( connection.getOutputStream() );
       }  
      catch ( IOException ioException ) 
      { 
          ioException.printStackTrace(); 
       }  

       
       ExecutorService worker = Executors.newFixedThreadPool( 1 ); 
       worker.execute( this ); 
    }  

     
    public void run() 
    { 
       myMark = input.nextLine(); 

       SwingUtilities.invokeLater( 
          new Runnable() 
         { 
              public void run() 
             {
                 
                 idField.setText( "You are player \"" + myMark + "\"" ); 
              } 
         }  
       ); 

      myTurn = ( myMark.equals( X_MARK ) ); 
               while ( true ) 
             {
               if ( input.hasNextLine() )
               processMessage( input.nextLine() );
              } 
    } 

    
    private void processMessage( String message )
    {
    
           if(message.equals("Game over %s won"))
             {
                          displayMessage(message+"\n" );
             }    

            else if ( message.equals( "Your move is done" ) )
            {
            displayMessage( message+"\n" );
            int i=input.nextInt();
            input.nextLine();
            setMark( board[i/3][i%3], myMark ); // set mark in square
       } 
          else if(message.equals("Valid move in "))
               displayMessage(message);

         else if ( message.equals( "Invalid move, try again" ) )
         {
          displayMessage( message + "\n" ); // display invalid move
          myTurn = true; // still this client's turn
       } 
       else if ( message.equals( "Opponent moved" ) )
        {
          int location = input.nextInt(); // get move location
          input.nextLine(); // skip newline after int location
          int row = location / 3; // calculate row
          int column = location % 3; // calculate column
          setMark( board[ row ][ column ],
             ( myMark.equals( X_MARK ) ? O_MARK : X_MARK ) ); 
          displayMessage( "Opponent moved. Your turn.\n" );
          myTurn = true; 
       } 
     else
          displayMessage( message + "\n" );
    } 

    
    private void displayMessage( final String messageToDisplay )
    {
      SwingUtilities.invokeLater(
          new Runnable()
          {
            public void run()
             {
                displayArea.append( messageToDisplay ); 
                
             } // end method run
          } 
        ); 
    } 

    private void setMark( final Square squareToMark, final String mark )
    {
       SwingUtilities.invokeLater(
          new Runnable()
          {
              public void run()
             {
                 squareToMark.setMark( mark ); 
              } 
          } 
        );
   } 

     
    public void sendClickedSquare( int location ) 
    { 
     
       if ( myTurn ) 
       { 
          output.format( "%d\n", location );
          output.flush();                                              
          myTurn = false; 
       }  
    } 

     
    public void setCurrentSquare( Square square ) 
    { 
       currentSquare = square; 
    }  

    private ImageIcon getMyImgIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private class Square extends JPanel 
    { 
       private String mark; 
       private int location; 

       public Square( String squareMark, int squareLocation ) 
       { 
          mark = squareMark;  
          location = squareLocation;  

          addMouseListener( 
             new MouseAdapter() 
             { 
               public void mouseReleased( MouseEvent e ) 
               {
                 currentSquare=Square.this; 
            
                sendClickedSquare( getSquareLocation() ); 
               } 
             }  
           );  
       } 

     
    public Dimension getPreferredSize() 
    { 
       return new Dimension( 81, 81 );  
    } 

     
    public Dimension getMinimumSize() 
    { 
       return getPreferredSize(); 
    }  
    
    public void setMark( String newMark ) 
    { 
       mark = newMark;
       repaint(); 
    }

    
    public int getSquareLocation() 
    { 
       return location; 
    }

    public void paintComponent( Graphics g ) 
    { 
       super.paintComponent( g ); 

       g.drawRect( 0, 0, 80, 80 ); 
       //Color c =new Color(34,139,34);
       g.setColor(new Color(204,255,204));
       
        
        g.fillRect( 0, 0, 80, 80 ); 
       g.setColor(Color.red);
       g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
       g.drawString( mark, 20, 45 );
       
      // g.setFont(Font.BOLD);
      // g.setFont(BOLD);
     
       
      // mark.setForeGround(
       
    } 
   } 
} 

