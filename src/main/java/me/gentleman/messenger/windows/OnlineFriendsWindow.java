package me.gentleman.messenger.windows;

import me.gentleman.messenger.modules.MessengerSettings;
import me.gentleman.messenger.types.Friend;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.feature.window.Window;
import org.rusherhack.client.api.render.graphic.VectorGraphic;
import org.rusherhack.client.api.system.IRelationManager;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.view.ListView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.client.api.utils.objects.PlayerRelation;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class OnlineFriendsWindow extends ResizeableWindow {
	public static OnlineFriendsWindow INSTANCE;
	private final TabbedView tabView;
	public final FriendListView friendsView;
	public final List<Friend> friendItems = new ArrayList<>();
	private boolean selectedFriendLoaded = false;
    public OnlineFriendsWindow() {
		super("Online friends", 100, 325, 150, 100);
		INSTANCE = this;
		try {
			//Uhh???? QUESTION MARK WHY DOESNT THIS WORK
			this.setIcon(new VectorGraphic("rusherhack/messenger/assets/icons/online-friends.svg", 64, 64));
		} catch (Exception ignored) {}
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
        Friend selectedFriend = this.friendsView.getSelectedItem();
		this.friendItems.clear();
		if (Globals.mc.player != null && Globals.mc.level != null) {
			List<String> friendNamesList = new ArrayList<>();
			// Retrieve online friends
			for (PlayerInfo playerInfo : Globals.mc.player.connection.getOnlinePlayers()) {
				if (Globals.mc.level != null && !Globals.mc.player.getName().getString().equals(playerInfo.getProfile().getName()) && RusherHackAPI.getRelationManager().isFriend(playerInfo.getProfile().getName())) {
					friendNamesList.add(playerInfo.getProfile().getName());
				}
			}
			// Retrieve offline friends
			//WHY IS THIS NOT WORKING
			if (MessengerSettings.showOfflineFriends.getValue()){
				IRelationManager relationManager = RusherHackAPI.getRelationManager();
				for (PlayerRelation relation : relationManager.getFriends()) {
					String friendName = relation.username();
					if (!friendNamesList.contains(friendName)) {
						ChatUtils.print(friendName);
						friendNamesList.add(friendName);
					}
				}
			}
			for (String friendName : friendNamesList) {
				this.friendItems.add(new Friend(friendName, this.friendsView));
			}
		}
		this.friendsView.resort();
		if (selectedFriend != null && this.friendItems.contains(selectedFriend)) {
			this.friendsView.setSelectedItem(selectedFriend);
		}
	}
	@Subscribe
	public void onUpdate(EventUpdate event) {
		if (!selectedFriendLoaded && !friendItems.isEmpty()) {
			Friend firstFriend = friendItems.get(0);
			if (firstFriend != null) {
				this.friendsView.setSelectedItem(firstFriend);
				firstFriend.reloadMessageHistory(firstFriend.playerName);
				selectedFriendLoaded = true;
			}
		}
	}
	@Override
	public WindowView getRootView() {
		return this.tabView;
	}
	public static class FriendListView extends ListView<Friend> {
		public FriendListView(String name, Window window, List<Friend> items) {
			super(name, window, items);
			this.addColumn("Username");
		}
	}
}