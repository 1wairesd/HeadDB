package com.github.thesilentpro.headdb.core.menu.gui;

import com.github.thesilentpro.grim.button.SimpleButton;
import com.github.thesilentpro.grim.gui.PaginatedGUI;
import com.github.thesilentpro.grim.page.Page;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.menu.LocalHeadsMenu;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.headdb.core.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LocalHeadsGUI extends PaginatedGUI {

    public LocalHeadsGUI(HeadDB plugin, String key, Component title, List<ItemStack> items) {
        super(new NamespacedKey(plugin, "gui_" + key));

        // Chunk items list
        for (List<ItemStack> itemsChunk : Utils.chunk(items, plugin.getCfg().getHeadsMenuRows() * 9)) {
            LocalHeadsMenu headsMenu = new LocalHeadsMenu(plugin, this, title, itemsChunk);

            if (plugin.getCfg().isHeadsMenuDividerEnabled()) {
                ItemStack dividerItem = Compatibility.newItem(
                        plugin.getCfg().getHeadsMenuDividerMaterial(),
                        MiniMessage.miniMessage().deserialize(plugin.getCfg().getHeadsMenuDividerName())
                );
                int startSlot = (plugin.getCfg().getDividerRow() - 1) * 9;
                for (int i = startSlot; i < startSlot + 9; i++) {
                    headsMenu.setButton(i, new SimpleButton(dividerItem));
                }
            }

            // Info item
            if (plugin.getCfg().isShowInfoItem()) {
                Component[] infoLore = new Component[]{
                        Component.text("❓ Didn't spot the perfect head in our collection?")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text("🎯 We're always adding more — and you can help!")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text(""),
                        Component.text("📥 Submit your favorite or original heads")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text("✨ Directly through our community Discord!")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text(""),
                        Component.text("🔗 Discord > https://discord.gg/RJsVvVd")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW)
                };

                ItemStack infoItem = plugin.getHeadApi()
                        .findByTexture("16439d2e306b225516aa9a6d007a7e75edd2d5015d113b42f44be62a517e574f")
                        .join()
                        .map(head -> Compatibility.setItemDetails(
                                head.getItem(),
                                Component.text("Can't find the head you're looking for?").color(NamedTextColor.RED),
                                infoLore
                        ))
                        .orElseGet(() -> Compatibility.newItem(
                                Material.WRITABLE_BOOK,
                                Component.text("Can't find the head you're looking for?").color(NamedTextColor.RED),
                                infoLore
                        ));

                headsMenu.setButton(53, new SimpleButton(infoItem, ctx -> {
                    Compatibility.sendMessage(
                            ctx.event().getWhoClicked(),
                            Component.text("Click to join: https://discord.gg/RJsVvVd").color(NamedTextColor.AQUA)
                    );
                }));
            }

            addPage(headsMenu);
        }

        // ── MENU CONTROLS ────────────────────────────────────────────────────────────

        // Back button
        ItemStack backItem = plugin.getHeadApi()
                .findByTexture(plugin.getCfg().getBackTexture())
                .join()
                .map(head -> Compatibility.setItemDetails(
                        head.getItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.back.name").orElseGet(() -> Component.text("◀ Back")),
                        plugin.getLocalization().getConsoleMessage("menu.controls.back.lore").orElseGet(() -> Component.text("Takes you to the previous page: ${{BACK}}"))
                ))
                .orElseGet(() -> Compatibility.newItem(
                        plugin.getCfg().getBackItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.back.name").orElseGet(() -> Component.text("◀ Back")),
                        plugin.getLocalization().getConsoleMessage("menu.controls.back.lore").orElseGet(() -> Component.text("Takes you to the previous page: ${{BACK}}"))
                ));

        // Page‐info button
        ItemStack pageInfoItem = plugin.getHeadApi()
                .findByTexture(plugin.getCfg().getInfoTexture())
                .join()
                .map(head -> Compatibility.setItemDetails(
                        head.getItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.info.name").orElseGet(() ->
                                Component.text("ℹ Page ")
                                        .append(Component.text("${{CURRENT}}").color(NamedTextColor.GREEN))
                                        .append(Component.text("/").color(NamedTextColor.GRAY))
                                        .append(Component.text("${{MAX}}").color(NamedTextColor.RED))
                        ),
                        plugin.getLocalization().getConsoleMessage("menu.controls.info.lore").orElseGet(() ->
                                Component.text("Click here to go to the main menu.")
                        )
                ))
                .orElseGet(() -> Compatibility.newItem(
                        plugin.getCfg().getInfoItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.info.name").orElseGet(() ->
                                Component.text("ℹ Page ")
                                        .append(Component.text("${{CURRENT}}").color(NamedTextColor.GREEN))
                                        .append(Component.text("/").color(NamedTextColor.GRAY))
                                        .append(Component.text("${{MAX}}").color(NamedTextColor.RED))
                        ),
                        plugin.getLocalization().getConsoleMessage("menu.controls.info.lore").orElseGet(() ->
                                Component.text("Click here to go to the main menu.")
                        )
                ));

        // Next button
        ItemStack nextItem = plugin.getHeadApi()
                .findByTexture(plugin.getCfg().getNextTexture())
                .join()
                .map(head -> Compatibility.setItemDetails(
                        head.getItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.next.name").orElseGet(() -> Component.text("Next ▶")),
                        plugin.getLocalization().getConsoleMessage("menu.controls.next.lore").orElseGet(() -> Component.text("Takes you to the next page: ${{NEXT}}"))
                ))
                .orElseGet(() -> Compatibility.newItem(
                        plugin.getCfg().getNextItem(),
                        plugin.getLocalization().getConsoleMessage("menu.controls.next.name").orElseGet(() -> Component.text("Next ▶")),
                        plugin.getLocalization().getConsoleMessage("menu.controls.next.lore").orElseGet(() -> Component.text("Takes you to the next page: ${{NEXT}}"))
                ));


        setControls(
                new SimpleButton(backItem, ctx -> {
                    Compatibility.playSound((Player) ctx.event().getWhoClicked(), plugin.getSoundConfig().get("control.back"));
                }),
                new SimpleButton(pageInfoItem, ctx -> {
                    plugin.getMenuManager().getMainMenu().open((Player) ctx.event().getWhoClicked());
                    Compatibility.playSound((Player) ctx.event().getWhoClicked(), plugin.getSoundConfig().get("control.info"));
                }),
                new SimpleButton(nextItem, ctx -> {
                    Compatibility.playSound((Player) ctx.event().getWhoClicked(), plugin.getSoundConfig().get("control.next"));
                }),
                true
        );

        getPages().values().forEach(Page::reRender);
    }

}