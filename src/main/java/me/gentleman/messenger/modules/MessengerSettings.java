package me.gentleman.messenger.modules;

import org.rusherhack.client.api.feature.module.Module;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.core.setting.BooleanSetting;
/**
 * Module for managing messenger settings.
 */
public class MessengerSettings extends Module {
    /**
     * Setting for enabling in-game notifications.
     */
    public static BooleanSetting notifications = new BooleanSetting("in-game notifications", true);
    /**
     * Setting for displaying offline friends.
     */
    public static BooleanSetting showOfflineFriends = new BooleanSetting("Show offline friends", false);
    /**
     * Constructs a new MessengerSettings module.
     */
    public MessengerSettings() {
        super("Messenger settings", "settings for the messenger plugin", ModuleCategory.CLIENT);
        this.registerSettings(notifications, showOfflineFriends);
    }
}