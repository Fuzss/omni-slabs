package fuzs.verticalslabs.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import net.minecraft.core.HolderLookup;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DiagonalBlockHandler.BLACKLIST);
    }
}
