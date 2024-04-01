package me.gentleman.messenger;

import me.gentleman.messenger.modules.MessengerSettings;
import me.gentleman.messenger.windows.MessengerWindow;
import me.gentleman.messenger.windows.OnlineFriendsWindow;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

public class Main extends Plugin {
	@Override
	public void onLoad() {
		RusherHackAPI.getWindowManager().registerFeature(new MessengerWindow());
		RusherHackAPI.getWindowManager().registerFeature(new OnlineFriendsWindow());
		RusherHackAPI.getModuleManager().registerFeature(new MessengerSettings());
		this.getLogger().info("Messenger plugin loaded!");
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Messenger plugin unloaded!");
	}
}