package me.gentleman.messenger.windows;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.client.api.render.graphic.VectorGraphic;
import org.rusherhack.client.api.ui.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.Window;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.ListItemContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.view.ListView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineFriendsWindow extends ResizeableWindow {

	public static OnlineFriendsWindow INSTANCE;

	private final TabbedView tabView;
	public final FriendListView friendsView;

	public final List<FriendItem> friendItems = new ArrayList<>();

	public OnlineFriendsWindow() {
		super("Online friends", 100, 325, 150, 100);
		INSTANCE = this;

		try {
			this.setIcon(new VectorGraphic("rusherhack/graphics/windows/relations_window.svg", 64, 64));
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setMinWidth(100);
		this.setMinHeight(100);
		final ComboContent comboContent = new ComboContent(this);


		this.friendsView = new FriendListView("Online friends", this, this.friendItems);

		ButtonComponent refreshButton = new ButtonComponent(this, "Refresh", this::resyncList);
		comboContent.addContent(refreshButton, ComboContent.AnchorSide.RIGHT);

		this.tabView = new TabbedView(this, List.of(comboContent, this.friendsView));

		RusherHackAPI.getEventBus().subscribe(this);
	}

	public void resyncList() {
		this.friendItems.clear();

		if (Globals.mc.player != null && Globals.mc.level != null) {
			List<String> friendNamesList = new ArrayList<>();

			for (PlayerInfo playerInfo : Globals.mc.player.connection.getOnlinePlayers()) {
				if (Globals.mc.level != null && RusherHackAPI.getRelationManager().isFriend(playerInfo.getProfile().getName())) {
					friendNamesList.add(playerInfo.getProfile().getName());
				}
			}

			for (String friendName : friendNamesList) {
				this.friendItems.add(new FriendItem(friendName, this.friendsView));
			}
		}

		this.friendsView.resort();
	}

	@Subscribe
	public void onPacketReceive(EventPacket.Receive event) {

		if (event.getPacket() instanceof ClientboundLoginPacket) {
			resyncList();
		}
	}

	@Override
	public WindowView getRootView() {
		return this.tabView;
	}

	class FriendItem extends ListItemContent {
		public final String playerName;
		private final String messageHistoryDirectory = "rusherhack/message_history/";
		private final String messageHistoryFile;

		public FriendItem(String playerName, ListView<FriendItem> view) {
			super(OnlineFriendsWindow.this, view);
			this.playerName = playerName;
			this.messageHistoryFile = messageHistoryDirectory + "messages_between_you_and_" + playerName + ".txt";

			File directory = new File(messageHistoryDirectory);
			if (!directory.exists()) {
				if (directory.mkdirs()) {
					System.out.println("Message history directory created successfully");
				} else {
					System.err.println("Failed to create message history directory");
				}
			}

			reloadMessageHistory();
		}

		@Override
		public String getAsString(ListView<?>.Column column) {
			if (column.getName().equalsIgnoreCase("username")) {
				return this.playerName;
			}
			return "null";
		}

		public void addMessage(String message, boolean isYourMessage) {
			String formattedMessage = (isYourMessage ? "To: " : "From: " + playerName + ": ") + message;
			saveMessageToFile(formattedMessage);
			displayMessage(formattedMessage, isYourMessage);
		}

		private void saveMessageToFile(String message) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(messageHistoryFile), true))) {
				writer.write(message + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void displayMessage(String formattedMessage, boolean isYourMessage) {
			int color = isYourMessage ? Color.white.getRGB() : Color.lightGray.getRGB();
			MessengerWindow.INSTANCE.getMessageView().add(formattedMessage, color);
		}

		private void reloadMessageHistory() {
			MessengerWindow.INSTANCE.getMessageView().clear();  // Clear the message view before reloading

			try (BufferedReader reader = new BufferedReader(new FileReader(new File(messageHistoryFile)))) {
				String line;
				while ((line = reader.readLine()) != null) {
					displayMessage(line, line.startsWith("To: "));
				}
			} catch (IOException e) {
				// File doesn't exist or other IO error, ignore
			}
		}
	}


	class FriendListView extends ListView<FriendItem> {

		public FriendListView(String name, Window window, List<FriendItem> items) {
			super(name, window, items);

			this.addColumn("Username");
		}
	}
}
