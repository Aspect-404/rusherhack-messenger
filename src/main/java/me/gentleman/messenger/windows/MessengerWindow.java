package me.gentleman.messenger.windows;

import me.gentleman.messenger.modules.MessengerSettings;
import me.gentleman.messenger.types.*;
import me.gentleman.messenger.utils.*;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.content.ComboContent;
import org.rusherhack.client.api.ui.window.content.component.ButtonComponent;
import org.rusherhack.client.api.ui.window.content.component.TextFieldComponent;
import org.rusherhack.client.api.ui.window.view.RichTextView;
import org.rusherhack.client.api.ui.window.view.TabbedView;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.notification.NotificationType;

import java.util.List;

public class MessengerWindow extends ResizeableWindow {
    public static MessengerWindow INSTANCE;
    private final TabbedView rootView;
    private final RichTextView messageView;
    private Friend friend;
    public MessengerWindow() {
        super("Messenger", 150, 100, 300, 300);
        RusherHackAPI.getEventBus().subscribe(this);
        INSTANCE = this;
        if (Globals.mc.player != null || Globals.mc.level != null || OnlineFriendsWindow.INSTANCE != null && friend != null && friend.playerName != null) {
            try {
                this.setIcon(PlayerUtils.fetchPlayerHead(friend.playerName));
            } catch (Exception ignored) {}
        }
        this.setMinWidth(150);
        this.setMinHeight(150);
        this.messageView = new RichTextView("Messages", this);
        final ComboContent inputCombo = new ComboContent(this);
        final TextFieldComponent rawMessage = new TextFieldComponent(this, "enter message", 100);
        final ButtonComponent sendButton = new ButtonComponent(this, "send", () -> {
            final String input = rawMessage.getValue();
            if (input.isEmpty() || Globals.mc.player == null || Globals.mc.level == null || OnlineFriendsWindow.INSTANCE == null) {
                return;
            }
            final Friend selected = OnlineFriendsWindow.INSTANCE.friendsView.getSelectedItem();
            if (selected != null && selected.playerName != null){
                Globals.mc.player.connection.sendCommand("w " + selected.playerName + " " + input);

                String formattedInput = (selected.playerName + ": " + input);
                selected.addMessage(formattedInput, true);
                friend.reloadMessageHistory(selected.playerName);
            }
            rawMessage.setValue("");
        });
        rawMessage.setReturnCallback((str) -> sendButton.onClick());
        inputCombo.addContent(rawMessage, ComboContent.AnchorSide.LEFT);
        inputCombo.addContent(sendButton, ComboContent.AnchorSide.RIGHT);
        this.rootView = new TabbedView(this, List.of(this.messageView, inputCombo));
    }

    @Subscribe
    public void onPacketReceive(EventPacket.Receive event) {
        if (Globals.mc.player == null || Globals.mc.level == null) {
            return;
        }
        if (event.getPacket() instanceof ClientboundPlayerChatPacket chatPacket) {
            Message chatInfo = MessageUtils.constructMessage(chatPacket.body().content());
            messageCheck(chatInfo);
            if (RusherHackAPI.getRelationManager().isFriend(chatInfo.getPlayerName()) && chatInfo.getPlayerName().equals(friend.playerName)){
                friend.reloadMessageHistory(friend.playerName);
            }
        } else if (event.getPacket() instanceof ClientboundSystemChatPacket chatPacket) {
            Message chatInfo = MessageUtils.constructMessage(chatPacket.content().getString());
            messageCheck(chatInfo);
            if (RusherHackAPI.getRelationManager().isFriend(chatInfo.getPlayerName()) && chatInfo.getPlayerName().equals(friend.playerName)){
                friend.reloadMessageHistory(friend.playerName);
            }
        }
    }
    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (OnlineFriendsWindow.INSTANCE != null) {
            Friend selectedItem = OnlineFriendsWindow.INSTANCE.friendsView.getSelectedItem();
            if (selectedItem != null && selectedItem.playerName != null && (friend == null || !friend.playerName.equals(selectedItem.playerName))) {
                friend = selectedItem;
                friend.reloadMessageHistory(selectedItem.playerName);
            }
        }
    }
    public void messageCheck(Message chatInfo) {
        String playerName = chatInfo.getPlayerName();
        String message = chatInfo.getMessage();
        boolean isFriend = RusherHackAPI.getRelationManager().isFriend(playerName);
        if (isFriend) {
            if (Globals.mc.player != null) {
                String yourPlayerName = Globals.mc.player.getName().getString();
                boolean isYourMessage = playerName.equalsIgnoreCase(yourPlayerName);
                String formattedMessage = isYourMessage
                        ? "To: " + playerName + ": " + message
                        : "From: " + playerName + ": " + message;
                if (MessengerSettings.notifications.getValue()) {
                    RusherHackAPI.getNotificationManager().send(NotificationType.INFO, formattedMessage);
                }
                Friend friendItem = findFriendItem(playerName);
                if (friendItem != null) {
                    friendItem.addMessage(message, isYourMessage);
                }
            }
        }
    }
    private Friend findFriendItem(String playerName) {
        for (Friend friendItem : OnlineFriendsWindow.INSTANCE.friendItems) {
            if (friendItem.playerName.equals(playerName)) {
                return friendItem;
            }
        }
        return null;
    }
    @Override
    public WindowView getRootView() {
        return this.rootView;
    }
    public RichTextView getMessageView() {
        return this.messageView;
    }
}
