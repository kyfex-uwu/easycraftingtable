package com.kyfexuwu.easycraftingtable;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.BiFunction;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Easycraftingtable.MODID)
public class Easycraftingtable {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "easycraftingtable";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Easycraftingtable(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public boolean isCrafterItem(Item item){
        return Config.craftingTableItems.contains(item);
    }

    @SubscribeEvent
    public void onCraftingTableUsed(PlayerInteractEvent.RightClickItem event){
        if(isCrafterItem(event.getItemStack().getItem())){
            event.getEntity().openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.crafting");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
                    return new CraftingMenu(syncId, playerInv, new ContainerLevelAccess() {
                        public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> func) {
                            return Optional.of(func.apply(player.level(), player.blockPosition()));
                        }
                    }){
                        public boolean stillValid(Player player) {
                            return !Config.requireInInv ||
                                    playerInv.contains(stack->isCrafterItem(stack.getItem()))||
                                    this.getCarried().is(holder->isCrafterItem(holder.get()));
                        }
                    };
                }
            });
        }
    }
}
