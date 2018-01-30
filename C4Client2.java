/*
Nathan Robinson
Intro to Networks
Dr. An
04/26/2017

Network client for Connect Four game. Connects to server using server socket
 */

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//Connects client to server, initialize class data
public class C4Client2 {

	//for GUI
    private JFrame frame = new JFrame("C4Client2");
    private JLabel messageBar = new JLabel("");
    private ImageIcon icon;
    private ImageIcon foeIcon;

    //for board set up
    private Square[] board = new Square[42];//7 width, 6 height
    private Square clickSquare;

	//to communicate with server and other clients
    private static int port = 1013;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //Runs the client as an application
    public static void main(String[] args) throws Exception {
        while (true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            C4Client2 client = new C4Client2(serverAddress);

            //set up game window
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(640, 480);
            client.frame.setResizable(false);
            client.frame.setVisible(true);

            //play or replay
            client.play();
            if (!client.playAgain()) {
                break;
            }
        }
    }

	//Creates grapical squares in client window
	static class Square extends JPanel {
		JLabel cell = new JLabel((Icon)null);

		//makes squares white panels
		public Square() {
			setBackground(Color.white);
			add(cell);
		}

		//Calls setIcon() to fill square with an red or yellow icon
		public void setIcon(Icon icon) {
			cell.setIcon(icon);
		}
    }

	//Creates client by establishing connection to server and setting up the GUI
    public C4Client2(String serverAddress) throws Exception {

        //Setup networking to server
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //GUI Setup
        messageBar.setBackground(Color.lightGray);
        frame.getContentPane().add(messageBar, "South");

        //board colors
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(6, 7, 3, 3));
        boardPanel.setBackground(Color.cyan);


        //Reads mouse clicks on board cells and send MOVE command
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    clickSquare = board[j];
                    out.println("MOVE " + j);}});
            boardPanel.add(board[i]);
        } frame.getContentPane().add(boardPanel, "Center");
    }

    //Listens for server messages
    public void play() throws Exception {
        String reply;
        try {
            reply = in.readLine();

            //first message "WELCOME": chip recieved
            if (reply.startsWith("WELCOME")) {

				//Player is identified by chip 'R'(red) or 'Y'(yellow).
                char chip = reply.charAt(8);
                icon = new ImageIcon(chip == 'R' ? "red.gif" : "yellow.gif");
                foeIcon  = new ImageIcon(chip == 'R' ? "yellow.gif" : "red.gif");
                frame.setTitle("Connect Four :: Player " + chip);
            }
            while (true) {
                reply = in.readLine();

                //you moved, opponent's turn
                if (reply.startsWith("VALID_MOVE")) {
                    messageBar.setText("You moved, opponent's turn");
                    clickSquare.setIcon(icon);
                    clickSquare.repaint();

                //opponent moved, your turn
                } else if (reply.startsWith("OPPONENT_MOVED")) {
                    int location = Integer.parseInt(reply.substring(15));
                    board[location].setIcon(foeIcon);
                    board[location].repaint();
                    messageBar.setText("Opponent moved, your turn");

                //'WIN', 'LOSE', 'TIE' messages prompt user to play another game
                } else if (reply.startsWith("VICTORY")) {
                    messageBar.setText("You win");
                    break;
                } else if (reply.startsWith("DEFEAT")) {
                    messageBar.setText("You lose");
                    break;
                } else if (reply.startsWith("TIE")) {
                    messageBar.setText("You tied");
                    break;
    			} else if (reply.startsWith("EXIT")) {
					messageBar.setText("Opponent left, you win");
					out.println("QUIT");
					break;
                } else if (reply.startsWith("MESSAGE")) {
                    messageBar.setText(reply.substring(8));
				}
            //if new game is refused or opponents leaves, 'QUIT' is sent
            } out.println("QUIT");

        } finally {
            socket.close();
        }
    }

    //Initiated by 'WIN', 'LOSE', 'TIE' command prompts
    private boolean playAgain() {
        int reply = JOptionPane.showConfirmDialog(frame, "Play again?",
            "Connect Four is fourtastic!", JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return reply == JOptionPane.YES_OPTION;
    }
}