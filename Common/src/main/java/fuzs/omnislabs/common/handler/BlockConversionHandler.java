package fuzs.omnislabs.common.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.common.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.common.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.common.api.event.v1.server.TagsUpdatedCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.function.*;

public class BlockConversionHandler {
    private static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();

    public static RegistryEntryAddedCallback<Block> onRegistryEntryAdded(Predicate<Block> filter, BiFunction<Block, BlockBehaviour.Properties, Block> factory, String modId) {
        return (Registry<Block> registry, Identifier id, Block block, BiConsumer<Identifier, Supplier<Block>> registrar) -> {
            if (filter.test(block)) {
                Identifier identifier = Identifier.fromNamespaceAndPath(modId, id.getNamespace() + "/" + id.getPath());
                registrar.accept(identifier, () -> {
                    BlockBehaviour.Properties properties = BlockConversionHelper.copyBlockProperties(block, identifier);
                    Block newBlock = factory.apply(block, properties);
                    BLOCK_CONVERSIONS.put(block, newBlock);
                    return newBlock;
                });
            }
        };
    }

    public static BiMap<Block, Block> getBlockConversions() {
        return Maps.unmodifiableBiMap(BLOCK_CONVERSIONS);
    }

    public static PlayerInteractEvents.UseBlock onUseBlock(TagKey<Block> unalteredBlocks, BooleanSupplier configOption) {
        return (Player player, Level level, InteractionHand interactionHand, BlockHitResult hitResult) -> {
            if (!configOption.getAsBoolean()) {
                return EventResultHolder.pass();
            }

            // Allows for toggling between original and converted block variants via shift+right-clicking with an empty hand.
            if (player.isSecondaryUseActive() && player.getItemInHand(interactionHand).isEmpty()) {
                BlockPos blockPos = hitResult.getBlockPos();
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.is(unalteredBlocks)) {
                    Block newBlock;
                    Block block = blockState.getBlock();
                    if (BLOCK_CONVERSIONS.containsKey(block)) {
                        newBlock = BLOCK_CONVERSIONS.get(block);
                    } else if (BLOCK_CONVERSIONS.containsValue(block)) {
                        newBlock = BLOCK_CONVERSIONS.inverse().get(block);
                    } else {
                        newBlock = null;
                    }

                    if (newBlock != null) {
                        BlockState newBlockState = newBlock.withPropertiesOf(blockState);
                        newBlockState = Block.updateFromNeighbourShapes(newBlockState, level, blockPos);
                        level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
                        level.neighborChanged(blockPos, newBlock, null);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, newBlockState));
                        level.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, blockPos, 0);
                        return EventResultHolder.interrupt(InteractionResult.SUCCESS);
                    }
                }
            }

            return EventResultHolder.pass();
        };
    }

    public static TagsUpdatedCallback onTagsUpdated(TagKey<Block> unalteredBlocks, Predicate<Block> filter) {
        return (HolderLookup.Provider registries, boolean isClientUpdate) -> {
            for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                if (entry.getValue() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();
                    setItemForBlock(filter, blockItem, block);
                    setBlockForItem(unalteredBlocks, blockItem, block);
                }
            }

            BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
        };
    }

    private static void setItemForBlock(Predicate<Block> filter, BlockItem blockItem, Block block) {
        if (filter.test(block)) {
            BlockConversionHelper.setItemForBlock(BLOCK_CONVERSIONS.get(block), blockItem);
        }
    }

    private static void setBlockForItem(TagKey<Block> tagKey, BlockItem blockItem, Block block) {
        Block oldBlock;
        Block newBlock = BLOCK_CONVERSIONS.get(block);
        if (newBlock != null) {
            oldBlock = block;
        } else {
            oldBlock = BLOCK_CONVERSIONS.inverse().get(block);
            if (oldBlock != null) {
                newBlock = block;
            } else {
                return;
            }
        }

        if (oldBlock.builtInRegistryHolder().is(tagKey)) {
            BlockConversionHelper.setBlockForItem(blockItem, oldBlock);
        } else {
            BlockConversionHelper.setBlockForItem(blockItem, newBlock);
        }
    }
}
