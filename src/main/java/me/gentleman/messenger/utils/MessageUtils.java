package me.gentleman.messenger.utils;

import me.gentleman.messenger.types.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    public static Message constructMessage(String chatPacketContent) {
        for (Pattern pattern : RegexUtils.FROM_PATTERNS) {
            Matcher matcher = pattern.matcher(chatPacketContent);
            if (matcher.find()) {
                String playerName = matcher.group(1);
                String message = matcher.group(2);
                return new Message(playerName, message);
            }
        }
        return new Message(null, null);
    }
}