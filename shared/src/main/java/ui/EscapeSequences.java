package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String SET_TEXT_COLOR_VERY_LIGHT_GREY = SET_TEXT_COLOR + "252m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "245m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "240m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_NAVY = SET_BG_COLOR + "17m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";
    public static final String WHITE_PIECE = SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD;
    public static final String BLACK_PIECE = SET_TEXT_COLOR_BLACK + SET_TEXT_BOLD;
    public static final String RESET_PIECE = RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;

    public static final String WHITE_KING = WHITE_PIECE + " K " + RESET_PIECE;
    public static final String WHITE_QUEEN = WHITE_PIECE + " Q " + RESET_PIECE;
    public static final String WHITE_BISHOP = WHITE_PIECE + " B " + RESET_PIECE;
    public static final String WHITE_KNIGHT = WHITE_PIECE + " N " + RESET_PIECE;
    public static final String WHITE_ROOK = WHITE_PIECE + " R " + RESET_PIECE;
    public static final String WHITE_PAWN = WHITE_PIECE + " P " + RESET_PIECE;
    public static final String BLACK_KING = BLACK_PIECE + " K " + RESET_PIECE;
    public static final String BLACK_QUEEN = BLACK_PIECE + " Q " + RESET_PIECE;
    public static final String BLACK_BISHOP = BLACK_PIECE + " B " + RESET_PIECE;
    public static final String BLACK_KNIGHT = BLACK_PIECE + " N " + RESET_PIECE;
    public static final String BLACK_ROOK = BLACK_PIECE + " R " + RESET_PIECE;
    public static final String BLACK_PAWN = BLACK_PIECE + " P " + RESET_PIECE;
    public static final String EMPTY = "   ";

    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }
}
