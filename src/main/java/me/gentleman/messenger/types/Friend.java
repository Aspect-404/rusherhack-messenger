package me.gentleman.messenger.types;

import me.gentleman.messenger.utils.LogUtils;
import me.gentleman.messenger.windows.MessengerWindow;
import org.rusherhack.client.api.ui.window.content.ListItemContent;
import org.rusherhack.client.api.ui.window.view.ListView;

import java.awt.*;
import java.io.*;

public class Friend extends ListItemContent {
    public final String playerName;
    private final String msgHistoryPath = "rusherhack/message_history/";
    public String messageHistoryFile = "";

    public Friend(String playerName, ListView<Friend> view) {
        super(MessengerWindow.INSTANCE, view);
        this.playerName = playerName;
        File directory = new File(msgHistoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            LogUtils.error("Failed to create directory: " + msgHistoryPath);
        }
    }

    private void loadMessages(String filename) {
        if (!new File(filename).exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                displayMessage(line, line.startsWith("To: "));
            }
        } catch (IOException ignored) {}
    }

    public void addMessage(String message, boolean isYourMessage) {
        this.messageHistoryFile = msgHistoryPath + "/" + playerName + ".txt";
        String formattedMessage = (isYourMessage ? "To: " : "From: " + playerName + ": ") + message;
        saveMessageToFile(formattedMessage);
        displayMessage(formattedMessage, isYourMessage);
    }

    private void saveMessageToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messageHistoryFile, true))) {
            writer.write(message + "\n");
        } catch (IOException ignored) {}
    }

    private void displayMessage(String formattedMessage, boolean isYourMessage) {
        int color = isYourMessage ? Color.white.getRGB() : Color.lightGray.getRGB();
        MessengerWindow.INSTANCE.getMessageView().add(formattedMessage, color);
    }

    public void reloadMessageHistory(String filename) {
        MessengerWindow.INSTANCE.getMessageView().clear();
        loadMessages(msgHistoryPath + filename + ".txt");
    }

    @Override
    public String getAsString(ListView<?>.Column column) {
        return null;
    }
}