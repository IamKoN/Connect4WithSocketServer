/*
Nathan Robinson
Intro to Networks
Dr. An
04/26/2017

Network server for java-based multiplayer Connect Four game.
*/

//Used for socket connection
import java.net.Socket;
import java.net.ServerSocket;

//Used for communication stream
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

//Create board, check for end game conditions, simulate user
class Match {

	/**
	ConnectFour board simulated by an array of 42 cells that are
	initialized as null( = unclaimed). When claimed	by a player,
	the	array cell stores a reference to the player that owns it */
	User liveUser;
	private User[] board = {
        null, null, null, null, null, null, null,
        null, null, null, null, null, null, null,
        null, null, null, null, null, null, null,
        null, null, null, null, null, null, null,
        null, null, null, null, null, null, null,
        null, null, null, null, null, null, null};


	//Checks each cell to see if board is full
	public boolean boardFull() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == null)
				return false;

		//Board is full
		} return true;
	}

	//Called by user thread when a move is attempted to check validity
	public synchronized boolean validMove(int location, User player) {

		//Move is valid: must be players turn, cell must be empty,
		//and must be on the lowest row or have a filled cell below it
		if (player == liveUser && board[location] == null &&
			(location > 34 || board[location+7] != null)) {

			//If legal, cell is claimed for by user, appears on both clients
			board[location] = liveUser;
			liveUser = liveUser.foe;
			liveUser.foePlayed(location);
			return true;

		//Move is invalid
		} return false;
	}

    //Checks the whole board array to see if there is four in a row
    public boolean checkWin() {
		for(int i = 0; i < board.length; i++) {
			if (board[i] == null)
				continue;

			//horizontal: right
			if(((i < 4) || (i > 6 && i < 11) || (i > 13 && i < 18) ||
				(i > 20 && i < 25) || (i > 27 && i < 32) || (i > 34 && i < 39))
				&& (board[i] == board[i+1] && board[i] == board[i+2] &&
				board[i] == board[i+3]))
				return true;

			//vertical: down
			if((i < 21) && (board[i] == board[i+7] && board[i] == board[i+14]
				&& board[i] == board[i+21]))
				return true;

			//diagonal: down-right
			if(((i < 21) &&  ((i < 4) || (i > 6 && i < 11) ||
				(i > 13 && i < 18))) &&	(board[i] == board[i+8] &&
				board[i] == board[i+16] && board[i] == board[i+24]))
					return true;

			//diagonal: up-right
			if(((i > 20) &&  ((i > 20 && i < 25) || (i > 27 && i < 32) ||
				(i > 34 && i < 39))) && (board[i] == board[i-6] && board[i]
				== board[i-12] && board[i] == board[i-18]))
					return true;

		//No four connected on board yet
		} return false;
	}

    //Server uses socket to communicate w/ user(client) via I/O text stream
	class User extends Thread {

		//Only text is transfered, only reader and writer needed
		BufferedReader inp;
		PrintWriter outp;

		User foe;
		Socket socket;
		char chip;
		//C4Client c4c = new C4Client();

		//Given socket and chip
		public User(Socket socket, char chip) {
			this.socket = socket;
			this.chip = chip;

			try {

				//Initialize Input/Output stream
				inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				outp = new PrintWriter(socket.getOutputStream(), true);

				//Display welcome message
				outp.println("WELCOME " + chip);
				outp.println("MESSAGE Waiting for opponent to connect");
			} catch (IOException e) {
				System.out.println("Player died: " + e);
				//outp.println("TIE");
			}
		}

		//When new client initialized, opponent is set
		public void setFoe(User foe) {
			this.foe = foe;
		}

		//When other player moves, end game conditions checked
		public void foePlayed(int location) {
			outp.println("OPPONENT_MOVED " + location);
			outp.println(checkWin() ? "DEFEAT" : boardFull() ? "TIE" : "");
		}

		//Runs User thread
		public void run() {
			try {

				//Displays when both opponents connect and are matched
				outp.println("MESSAGE All players connected");

				//Displays for player who connected first
				if (chip == 'R')
					outp.println("MESSAGE Your move");

				//Recieve and process prompt messages from the client
				while (true) {
					String prompt = inp.readLine();

                    //'MOVE <n>' Sent from client to server
					if (prompt.startsWith("MOVE")) {
						int location = Integer.parseInt(prompt.substring(5));
						if (validMove(location, this)) {

							//'WELCOME <char>','VALID_MOVE','FOE_PLAYED <n>',
							//'MESSAGE <text>','WIN','LOSE', & 'TIE' sent by server to client
							outp.println("VALID_MOVE");
							outp.println(checkWin() ? "VICTORY" : boardFull() ? "TIE" : "");

						//Invalid move
						} else
							outp.println("MESSAGE Invalid move");

					//'QUIT' Sent from Client to Server
					} else {// if (prompt.startsWith("QUIT")) {
						outp.println("TIE");
						//return;
					}
				}
			} catch (IOException e) {
				System.out.println("Player died: " + e);
				outp.println("TIE");


			//close User socket for client
			} finally {
				try {socket.close();} catch (IOException e) {}
			}
		}
	}
}

//Connects clients
public class C4Server2 {

    //Run server as application
    public static void main(String[] args) throws Exception {

        //Setup socket
        ServerSocket finder = new ServerSocket(1013);
        System.out.println("ConnectFour server online");
        try {

			//unlimited pairs can connect to server socket via while loop
            while (true) {
                Match game = new Match();

                //Establish clients
                Match.User red = game.new User(finder.accept(), 'R');
                Match.User yellow = game.new User(finder.accept(), 'Y');

                //Pair up clients
                red.setFoe(yellow);
                yellow.setFoe(red);

                //First user to connect plays first
                game.liveUser = red;
                red.start();
                yellow.start();
            }
        } finally {
            finder.close();
        }
    }
}