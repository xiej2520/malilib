package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.fabricmc.api.ModInitializer;

public class MaLiLib implements ModInitializer
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                new ModInfo(MaLiLibReference.MOD_ID, MaLiLibReference.MOD_NAME, MaLiLibConfigGui::new));
    }
}
