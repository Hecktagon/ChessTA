package client;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade serverFacade;
    private String username = null;
    private String authToken = null;

    public Client(String serverUrl){
        serverFacade = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println(WHITE_KING + WHITE_QUEEN + " Welcome to chess! " + BLACK_QUEEN + BLACK_KING);
        System.out.println(SET_TEXT_COLOR_DARK_GREY + "Type 'help' for options" + RESET_TEXT_COLOR);

        // initialize scanner for getting input from user
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")){

        }
    }

    public void execute(){

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }
}
