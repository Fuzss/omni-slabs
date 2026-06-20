package fuzs.omnislabs.common.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.common.api.event.v1.AddBlockEntityTypeBlocksCallback;
import fuzs.puzzleslib.common.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.common.api.event.v1.entity.player.PlayerInteractEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.*;

public class BlockConversionHandler {
    private static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();
    private static final Map<BlockState, BlockState> BLOCK_STATE_CONVERSIONS_CACHE = new MapMaker().weakKeys()
            .weakValues()
            .makeMap();

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

    public static AddBlockEntityTypeBlocksCallback onAddBlockEntityTypeBlocks(Holder.Reference<? extends BlockEntityType<?>> blockEntityType) {
        return (BiConsumer<BlockEntityType<?>, Block> consumer) -> {
            for (Map.Entry<Block, Block> entry : BLOCK_CONVERSIONS.entrySet()) {
                consumer.accept(blockEntityType.value(), entry.getValue());
            }
        };
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

    public static Consumer<RegistryAccess> onClientTagsUpdated(TagKey<Block> unalteredBlocks, Predicate<Block> filter) {
        return (RegistryAccess registries) -> {
            onTagsUpdated(unalteredBlocks, filter);
        };
    }

    public static BiConsumer<ReloadableServerResources, RegistryAccess> onServerResourcesLoad(TagKey<Block> unalteredBlocks, Predicate<Block> filter) {
        return (ReloadableServerResources serverResources, RegistryAccess registries) -> {
            onTagsUpdated(unalteredBlocks, filter);
        };
    }

    private static void onTagsUpdated(TagKey<Block> unalteredBlocks, Predicate<Block> filter) {
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                setItemForBlock(filter, blockItem, block);
                setBlockForItem(unalteredBlocks, blockItem, block);
            }
        }

        BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
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

    @Nullable
    public static BlockState convertToVanillaBlock(@Nullable BlockState blockState) {
        // mod replacement block coming in, we need to forward the original
        return applyBlockConversion(blockState, true);
    }

    @Nullable
    public static BlockState convertFromVanillaBlock(@Nullable BlockState blockState) {
        // original block coming in, we need to convert back to our replacement
        return applyBlockConversion(blockState, false);
    }

    @Nullable
    private static BlockState applyBlockConversion(@Nullable BlockState blockState, boolean inverse) {
        if (blockState != null) {
            return BLOCK_STATE_CONVERSIONS_CACHE.computeIfAbsent(blockState, applyBlockConversion(inverse));
        } else {
            return null;
        }
    }

    private static UnaryOperator<BlockState> applyBlockConversion(boolean inverse) {
        return (BlockState blockState) -> {
            BiMap<Block, Block> blockConversions = inverse ? BLOCK_CONVERSIONS.inverse() : BLOCK_CONVERSIONS;
            if (blockState != null && blockConversions.containsKey(blockState.getBlock())) {
                Block block = blockConversions.get(blockState.getBlock());
                return copyAllProperties(blockState, block.defaultBlockState());
            } else {
                return blockState;
            }
        };
    }

    private static <T extends Comparable<T>, V extends T> BlockState copyAllProperties(BlockState oldBlockState, BlockState newBlockState) {
        for (Property.Value<?> value : oldBlockState.getValues().toList()) {
            newBlockState = newBlockState.trySetValue((Property<T>) value.property(), (V) value.value());
        }

        return newBlockState;
    }
}
