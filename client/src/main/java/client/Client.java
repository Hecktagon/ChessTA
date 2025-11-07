package client;

import errors.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade serverFacade;
    private String username = null;
    private String authToken = null;
    private final Commands commands;

    public Client(String serverUrl){
        serverFacade = new ServerFacade(serverUrl);
        commands = new Commands(serverFacade);
    }

    public void run(){
        System.out.println(WHITE_KING + WHITE_QUEEN + " Welcome to chess! " + BLACK_QUEEN + BLACK_KING);
        System.out.println(SET_TEXT_COLOR_DARK_GREY + "Type 'help' for options" + RESET_TEXT_COLOR);

        // initialize scanner for getting input from user
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")){
            printPrompt();
            // grab the user input
            String userInput = scanner.nextLine();

            try{
                result = execute(userInput);
                System.out.println(SET_TEXT_COLOR_GREEN + result);
            } catch (Exception e) {
                System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
            }
        }
    }

    public String execute(String input) throws ResponseException {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch(command){
                case "quit" -> "quit";
                case "login" -> commands.login(params);
                case "register" -> commands.register(params);
                case "logout" -> commands.logout();
                case "create" -> commands.createGame(params);
                case "list" -> commands.listGames();
                case "play" -> commands.playGame(params);
                case "observe" -> commands.observeGame(params);
                default -> commands.help();
            };
        } catch (ResponseException e){
            if (e.getType().equals(ResponseException.Type.CLIENT_ERROR)){
                return e.getMessage();
            }
            throw e;
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }

    // ###   Package-private methods:   ###
    String getAuth() {
        return authToken;
    }

    void setAuth(String authToken) {
        this.authToken = authToken;
    }

    String getUser() {
        return username;
    }

    void setUser(String username) {
        this.username = username;
    }
}
