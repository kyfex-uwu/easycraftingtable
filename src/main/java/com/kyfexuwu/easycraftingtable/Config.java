package com.kyfexuwu.easycraftingtable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Easycraftingtable.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue REQUIRE_IN_INVENTORY = BUILDER.comment(
            "Whether it is required to have a crafting table in your inventory at all times after opening the crafting " +
                    "screen from the item").define("requireInInv",false);
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CRAFTING_TABLE_ITEMS = BUILDER.comment(
            "A list of items that will trigger the crafting table screen, as well as keep the crafting screen open " +
                    "(if requireInInventory is true)")
            .defineListAllowEmpty("craftingTableItems",List.of("minecraft:crafting_table","minecraft:crafter"), Config::validateItemName);

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName &&
                ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(itemName));
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean requireInInv;
    public static Set<Item> craftingTableItems;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        requireInInv=REQUIRE_IN_INVENTORY.get();
        craftingTableItems=CRAFTING_TABLE_ITEMS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
    }
}
