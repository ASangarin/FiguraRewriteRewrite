package org.moon.figura.avatars;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.providers.LocalAvatarLoader;
import org.moon.figura.backend.NetworkManager;
import org.moon.figura.config.Config;
import org.moon.figura.gui.FiguraToast;
import org.moon.figura.gui.widgets.lists.AvatarList;
import org.moon.figura.utils.FiguraText;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all the avatars that are currently loaded in memory, and also
 * handles getting the avatars of entities. If an entity does not have a loaded avatar,
 * the AvatarManager will fetch the avatar and cache it.
 */
public class AvatarManager {

    private static final Map<UUID, Avatar> LOADED_AVATARS = new ConcurrentHashMap<>();
    private static final Set<UUID> FETCHED_AVATARS = new HashSet<>();

    public static boolean localUploaded = true; //init as true :3
    public static boolean panic = false;

    // -- avatar events -- //

    public static void tickLoadedAvatars() {
        if (panic)
            return;

        //unload avatars from disconnected players
        //needs to actually be an event, otherwise some things like skulls will try to download the avatar every tick
        /*
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Set<UUID> toRemove = new HashSet<>();
        for (UUID id : LOADED_AVATARS.keySet()) {
            if (connection != null && connection.getPlayerInfo(id) == null)
                toRemove.add(id);
        }

        for (UUID id : toRemove)
            clearAvatar(id);
        */

        //tick the avatar
        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.tick();
    }

    public static void onWorldRender(float tickDelta) {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.worldRenderEvent(tickDelta);
    }

    public static void afterWorldRender(float tickDelta) {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.postWorldRenderEvent(tickDelta);
    }

    public static void handleCustomEvent(String id) {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.handleCustomEvent(id);
    }

    public static void applyAnimations() {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.applyAnimations();
    }

    public static void clearAnimations() {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.clearAnimations();
    }

    public static void pauseAnimations() {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.pauseAnimations();
    }

    public static void resumeAnimations() {
        if (panic)
            return;

        for (Avatar avatar : LOADED_AVATARS.values())
            avatar.resumeAnimations();
    }

    // -- avatar getters -- //

    //player will also attempt to load from network, if possible
    public static Avatar getAvatarForPlayer(UUID player) {
        if (panic || Minecraft.getInstance().level == null)
            return null;

        if (!LOADED_AVATARS.containsKey(player))
            fetchBackend(player);

        return LOADED_AVATARS.get(player);
    }

    //tries to get data from an entity
    public static Avatar getAvatar(Entity entity) {
        if (panic || Minecraft.getInstance().level == null)
            return null;

        UUID uuid = entity.getUUID();

        //load from player (fetch backend) if is a player
        if (entity instanceof Player)
            return getAvatarForPlayer(uuid);

        //TODO
        //otherwise, returns the avatar from the entity pool (cem)
        return null;
    }

    // -- avatar management -- //

    //removes an loaded avatar
    public static void clearAvatar(UUID id) {
        Avatar avatar = LOADED_AVATARS.remove(id);
        FETCHED_AVATARS.remove(id);

        if (avatar != null)
            avatar.clean();

        Badges.clear(id);
        NetworkManager.clearRequestsFor(id);
        NetworkManager.unsubscribe(id);

        FiguraMod.LOGGER.debug("Cleared avatar for " + id);
    }

    //clears ALL loaded avatars, including local
    public static void clearAllAvatars() {
        for (UUID id : LOADED_AVATARS.keySet())
            clearAvatar(id);

        localUploaded = true;
        FiguraMod.LOGGER.info("Cleared all avatars");
    }

    //reloads an avatar
    public static void reloadAvatar(UUID id) {
        if (!localUploaded && FiguraMod.isLocal(id))
            loadLocalAvatar(LocalAvatarLoader.getLastLoadedPath());
        else
            clearAvatar(id);
    }

    //load the local player avatar
    public static void loadLocalAvatar(Path path) {
        UUID id = FiguraMod.getLocalPlayerUUID();

        //clear
        Avatar av = LOADED_AVATARS.remove(id);
        if (av != null)
            av.clean();

        Badges.clear(id);
        NetworkManager.clearRequestsFor(id);
        NetworkManager.unsubscribe(id);

        //load
        try {
            Avatar avatar = new Avatar(id);
            LOADED_AVATARS.put(id, avatar);
            avatar.load(LocalAvatarLoader.loadAvatar(path));
            FiguraMod.LOGGER.debug("Loaded local avatar from " + path);
        } catch (Exception e) {
            FiguraMod.LOGGER.error("Failed to load avatar from " + path, e);
            FiguraToast.sendToast(new FiguraText("toast.load_error"), new FiguraText("toast.load_error.2"), FiguraToast.ToastType.ERROR);
        }

        //mark as not uploaded
        localUploaded = false;
    }

    //set an user's avatar
    public static void setAvatar(UUID id, CompoundTag nbt) {
        //remove local watch keys
        if (FiguraMod.isLocal(id)) {
            LocalAvatarLoader.resetWatchKeys();
            AvatarList.selectedEntry = null;
            localUploaded = true;
        }

        try {
            Avatar avatar = new Avatar(id);
            LOADED_AVATARS.put(id, avatar);
            avatar.load(nbt);
            FiguraMod.LOGGER.debug("Set avatar for " + id);
        } catch (Exception e) {
            FiguraMod.LOGGER.error("Failed to set avatar for " + id, e);
        }
    }

    //get avatar from the backend
    private static void fetchBackend(UUID id) {
        //already fetched :p
        if (FETCHED_AVATARS.contains(id))
            return;

        FiguraMod.LOGGER.debug("Getting avatar for " + id);

        LOADED_AVATARS.put(id, new Avatar(id));
        FETCHED_AVATARS.add(id);

        //egg
        if (FiguraMod.CHEESE_DAY && Config.EASTER_EGGS.asBool() && LocalAvatarLoader.cheese != null) {
            setAvatar(id, LocalAvatarLoader.cheese);
            return;
        }

        NetworkManager.getAvatar(id);
    }
}
