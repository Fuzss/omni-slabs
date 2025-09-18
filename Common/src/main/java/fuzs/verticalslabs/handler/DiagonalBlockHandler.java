package fuzs.verticalslabs.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fuzs.puzzleslib.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.api.init.v3.RegistryHelper;
import fuzs.puzzleslib.api.init.v3.tags.TypedTagFactory;
import fuzs.verticalslabs.VerticalSlabs;
import fuzs.verticalslabs.core.CommonAbstractions;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DiagonalBlockHandler {
    public static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();
    public static final TagKey<Block> BLACKLIST = TypedTagFactory.BLOCK.make(VerticalSlabs.MOD_ID, "non_vertical_slabs");

    public static void onBlockAdded(Registry<Block> registry, ResourceLocation id, Block entry, BiConsumer<ResourceLocation, Supplier<Block>> registrar) {
        if (isTarget(entry)) {
            ResourceLocation resourceLocation = VerticalSlabs.id(id.getNamespace() + "/" + id.getPath());
            registrar.accept(resourceLocation, () -> {
                Block block = CommonAbstractions.INSTANCE.getVerticalSlabBlock(entry);
                BLOCK_CONVERSIONS.put(entry, block);
                return block;
            });
        }
    }

    private static boolean isTarget(Block block) {
        return block instanceof SlabBlock && !(block instanceof RotatedSlabBlock);
    }

    public static void onTagsUpdated(RegistryAccess registryAccess, boolean client) {
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                setItemForBlock(blockItem, block);
                setBlockForItem(blockItem, block);
            }
        }
        copyBoundTags();
    }

    private static void setItemForBlock(BlockItem blockItem, Block block) {
        // item id should be fine to use for block items
        if (isTarget(block)) {
            BlockConversionHelper.setItemForBlock(BLOCK_CONVERSIONS.get(block), blockItem);
        }
    }

    private static void setBlockForItem(BlockItem blockItem, Block block) {
        BiMap<Block, Block> conversions = BLOCK_CONVERSIONS;
        Block baseBlock;
        Block diagonalBlock = conversions.get(block);
        if (diagonalBlock != null) {
            baseBlock = block;
        } else {
            baseBlock = conversions.inverse().get(block);
            if (baseBlock != null) {
                diagonalBlock = block;
            } else {
                return;
            }
        }
        if (RegistryHelper.is(BLACKLIST, baseBlock)) {
            BlockConversionHelper.setBlockForItem(blockItem, baseBlock);
        } else {
            BlockConversionHelper.setBlockForItem(blockItem, diagonalBlock);
        }
    }

    private static void copyBoundTags() {
        BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
    }
}
