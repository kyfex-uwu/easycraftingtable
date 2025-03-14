package com.kyfexuwu.easycraftingtable;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.BiFunction;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Easycraftingtable.MODID)
public class Easycraftingtable {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "easycraftingtable";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Easycraftingtable(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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
                                    this.getCarried().is(holder->isCrafterItem(holder.value()));
                        }
                    };
                }
            });
        }
    }
}
