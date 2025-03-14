package com.kyfexuwu.easycraftingtable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Easycraftingtable.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REQUIRE_IN_INVENTORY = BUILDER.comment(
            "Whether it is required to have a crafting table in your inventory at all times after opening the crafting " +
                    "screen from the item").define("requireInInv",false);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CRAFTING_TABLE_ITEMS = BUILDER.comment(
                    "A list of items that will trigger the crafting table screen, as well as keep the crafting screen open " +
                            "(if requireInInventory is true)")
            .defineListAllowEmpty("craftingTableItems",List.of("minecraft:crafting_table","minecraft:crafter"), Config::validateItemName);

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName &&
                BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean requireInInv;
    public static Set<Item> craftingTableItems;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        requireInInv=REQUIRE_IN_INVENTORY.get();
        craftingTableItems=CRAFTING_TABLE_ITEMS.get().stream().map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
    }
}
