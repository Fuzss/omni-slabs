plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
}

multiloader {
    mixins {
        mixin("HoneycombItemMixin", "WeatheringCopperMixin")
        clientMixin(
            "ClientLevelMixin",
            "MultiPlayerGameModeMixin"
        )
        clientAccessor("BlockBreakingRenderStateAccessor")
    }
}
