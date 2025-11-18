package client;

import errors.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String serverUrl){
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        client = new Client(serverFacade);
    }

    public void run(){
        System.out.println(WHITE_KING + WHITE_QUEEN + " Welcome to chess! " + BLACK_QUEEN + BLACK_KING);
        System.out.println(SET_TEXT_COLOR_GREEN + "Type 'help' for options" + RESET_TEXT_COLOR);

        // initialize scanner for getting input from user
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quitting...")){
            printPrompt();
            // grab the user input
            String userInput = scanner.nextLine();

            try{
                result = execute(userInput);
                System.out.println(RESET_TEXT_COLOR + RESET_BG_COLOR + result);
            } catch (Exception e) {
                String message = e.getMessage() == null ? "Server encountered an error :(" : e.getMessage();
                System.out.println(SET_TEXT_COLOR_RED + message);
            }
        }
    }

    private String execute(String input) throws ResponseException {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch(command){
                case "quit" -> "quitting...";
                case "login" -> client.login(params);
                case "register" -> client.register(params);
                case "logout" -> client.logout();
                case "create" -> client.createGame(params);
                case "list" -> client.listGames();
                case "play" -> client.playGame(params);
                case "observe" -> client.observeGame(params);
                case "help" -> client.help();
                default -> "Unrecognized command, try:\n" + client.help();
            };
        } catch (ResponseException e){
            if (e.getType().equals(ResponseException.Type.CLIENT_ERROR)){
                return e.getMessage();
            }
            throw e;
        }
    }

    private void printPrompt() {
        System.out.print(RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }
}
