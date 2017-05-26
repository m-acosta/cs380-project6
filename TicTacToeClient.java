import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 * @author Michael Acosta
 * Cal Poly Pomona - CS 380
 * Project 6
 * Date Last Modified: 5/26/2017
 *
 */

public class TicTacToeClient {
	public static void main(String[] args) {
		try {
			Socket mySocket = new Socket("codebank.xyz", 38006);
			// Output streams are ObjectOutputStreams to handle the serializable objects
			ObjectOutputStream output = new ObjectOutputStream(mySocket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(mySocket.getInputStream());
			Scanner keyboard = new Scanner(System.in);
			// My identifying user name
			ConnectMessage connect = new ConnectMessage("mdacosta");
			output.writeObject(connect);
			CommandMessage newGame = new CommandMessage(CommandMessage.Command.NEW_GAME);
			output.writeObject(newGame);
			// Object returned must be cast to a board message
			BoardMessage currentBoard = (BoardMessage)input.readObject();
			System.out.println("The tic-tac-toe board holds a 0 for an empty square, "
					+ "1 for squares held by you, and 2 for squares held by the opponent.");
			System.out.println("Moves should be formatted as (row) (col)");
			// Loops runs while the status of the board doesn't change from IN_PROGRESS
			while (currentBoard.getStatus() == BoardMessage.Status.IN_PROGRESS) {
				printBoard(currentBoard.getBoard());
				System.out.println("Where would you like to move?");
				byte row = keyboard.nextByte();
				byte col = keyboard.nextByte();
				MoveMessage move = new MoveMessage(row, col);
				output.writeObject(move);
				Message temp = (Message)input.readObject();
				// If the type is BOARD, then it is stored back in the currentBoard variable
				// Otherwise it is cast into an ErrorMessage
				if (temp.getType() == MessageType.BOARD) {
					currentBoard = (BoardMessage)temp;
				} else {
					ErrorMessage errorMessage = (ErrorMessage)temp;
					System.out.println(errorMessage.getError());
				}
			}
			// Print out the final status and board state
			System.out.println(currentBoard.getStatus());
			printBoard(currentBoard.getBoard());
			keyboard.close();
			mySocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Print out the board as a 2D Array with proper spacing
	 */
	public static void printBoard(byte[][] b) {
		for (int i = 0; i < b.length; i++) {
			System.out.print("\t");
			for (int j = 0; j < b[0].length; j++) {
				System.out.print(b[i][j]);
			}
			System.out.println();
		}
	}
}