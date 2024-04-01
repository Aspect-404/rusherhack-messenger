package me.gentleman.messenger.utils;

import org.rusherhack.client.api.render.graphic.TextureGraphic;
import org.rusherhack.client.api.utils.ChatUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static org.rusherhack.client.api.Globals.mc;

public class PlayerUtils {
    public static boolean checkOnlineStatus(UUID uuid) {
        if (mc.player == null) return false;
        return mc.player.connection.getPlayerInfo(uuid) != null;
    }
    public static TextureGraphic fetchPlayerHead(String username) throws IOException {
        URL imageUrl = new URL("https://mc-heads.net/avatar/" + username);
        ChatUtils.print(imageUrl.toString());
        InputStream inputStream = imageUrl.openStream();
        return new TextureGraphic(inputStream, 64, 64);
    }
}
