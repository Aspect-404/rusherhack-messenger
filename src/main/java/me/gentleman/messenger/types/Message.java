package me.gentleman.messenger.types;
/**
 * Represents a message sent by a player.
 */
public class Message {
    private String playerName;
    private String message;
    /**
     * Constructs a new Message object with the given player name and message.
     *
     * @param playerName the name of the player sending the message
     * @param message    the content of the message
     */
    public Message(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }
    /**
     * Gets the name of the player who sent the message.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }
    /**
     * Gets the content of the message.
     *
     * @return the message content
     */
    public String getMessage() {
        return message;
    }
}
