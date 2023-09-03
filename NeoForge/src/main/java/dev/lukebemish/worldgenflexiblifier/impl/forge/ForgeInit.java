package dev.lukebemish.worldgenflexiblifier.impl.forge;

import dev.lukebemish.worldgenflexiblifier.impl.Constants;
import dev.lukebemish.worldgenflexiblifier.impl.WorldgenFlexiblifier;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class ForgeInit {

    public ForgeInit() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        var featuresRegistrar = DeferredRegister.create(Registries.FEATURE, Constants.MOD_ID);
        featuresRegistrar.register(modBus);
        WorldgenFlexiblifier.registerFeatures(featuresRegistrar::register);
    }
}
