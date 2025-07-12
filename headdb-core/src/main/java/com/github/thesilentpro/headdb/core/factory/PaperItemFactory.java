package com.github.thesilentpro.headdb.core.factory;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.HeadDB;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

@ApiStatus.Internal
public class PaperItemFactory implements ItemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaperItemFactory.class);
    private final HeadDB plugin;

    public PaperItemFactory(HeadDB plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack asItem(Head head) {
        ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfileExact(UUID.randomUUID(), null);

        try {
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(URI.create("http://textures.minecraft.net/texture/" + head.getTexture()).toURL());
            profile.setTextures(textures);
        } catch (MalformedURLException ex) {
            LOGGER.error("Failed to set texture for {} (ID:{} | Texture: {})", head.getName(), head.getId(), head.getTexture(), ex);
            return item;
        }

        String cost = String.valueOf(plugin.getCfg().getHeadOrCategoryPrice(head.getId(), head.getCategory().toLowerCase(Locale.ROOT)));
        meta.itemName(plugin.getCfg().getHeadName().replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName())).replaceText(builder -> builder.matchLiteral("{cost}").replacement(cost)));

        List<Component> lore = new ArrayList<>(plugin.getCfg().getHeadsLore());
        lore.replaceAll(component -> component.replaceText(builder -> builder.matchLiteral("{id}").replacement(String.valueOf(head.getId())))
                .replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName()))
                .replaceText(builder -> builder.matchLiteral("{category}").replacement(head.getCategory()))
                .replaceText(builder -> builder.matchLiteral("{tags}").replacement(String.join(",", head.getTags())))
                .replaceText(builder -> builder.matchLiteral("{cost}").replacement(cost)
        ));
        meta.lore(lore);

        meta.setPlayerProfile(profile);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack asItem(OfflinePlayer player) {
        if (player == null || player.getName() == null) {
            return null;
        }

        ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(player.getUniqueId(), player.getName());
        meta.setPlayerProfile(profile);
        meta.itemName(Component.text(player.getName()));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public UUID getIdFromItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = meta.getPlayerProfile();
        return profile != null ? profile.getId() : null;
    }

    @Override
    public Component getNameFromItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        return meta.itemName();
    }

    @Override
    public ItemStack setItemDetails(ItemStack item, Component name, Component... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.itemName(name);
        meta.lore(lore != null ? Arrays.asList(lore) : null);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack newItem(Material material) {
        return material == null ? null : ItemStack.of(material);
    }

    @Override
    public ItemStack newItem(Material material, Component name, Component... lore) {
        ItemStack item = ItemStack.of(material);
        return setItemDetails(item, name, lore);
    }

}