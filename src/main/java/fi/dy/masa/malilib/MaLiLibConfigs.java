package fi.dy.masa.malilib;

import java.nio.file.Files;
import java.nio.file.Path;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public class MaLiLibConfigs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = MaLiLibReference.MOD_ID + ".json";

    public static class Generic
    {
        public static final ConfigHotkey    IGNORED_KEYS            = new ConfigHotkey("ignoredKeys", "", "Any keys set here will be completely ignored");
        public static final ConfigBoolean   ENABLE_CONFIG_SWITCHER  = new ConfigBoolean("enableConfigSwitcher", true, "Enable the \"Config Switcher\" at the top right to\nquickly switch between Registered Config Screens\nNOTE: If the Config Screen isn't registered,\nwe will try to automatically register them.", "Config Switcher");
        public static final ConfigHotkey    OPEN_GUI_CONFIGS        = new ConfigHotkey("openGuiConfigs", "A,C", "Open the in-game malilib config GUI");
        public static final ConfigBoolean   REALMS_COMMON_CONFIG    = new ConfigBoolean("realmsCommonConfig", true, "Whether or not to use a common config file name for all realms servers.\nIf this is disabled, then the server IP and port are used in the generated config file names.\nHowever, apparently the Realms server addresses change regularly, so the config names would change\nall the time and thus the configs wouldn't save properly.\nSo basically leave this enabled if you only play on one Realms server.\nIf you play on multiple Realms... then the configs will get mixed up regardless.\nUnless you play on the different servers on different Minecraft instances\nto keep the configs separated by the Minecraft instance.");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                ENABLE_CONFIG_SWITCHER,
                IGNORED_KEYS,
                OPEN_GUI_CONFIGS,
                REALMS_COMMON_CONFIG
        );
    }

    public static class Debug
    {
        public static final ConfigBoolean INPUT_CANCELLATION_DEBUG  = new ConfigBoolean("inputCancellationDebugging", false, "When enabled, then the cancellation reason/source\nfor inputs (keyboard and mouse) is printed out");
        public static final ConfigBoolean KEYBIND_DEBUG             = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are\nprinted to the game console (and the action bar, if enabled)");
        public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR   = new ConfigBoolean("keybindDebuggingIngame", true, "If enabled, then the messages from 'keybindDebugging'\nare also printed to the in-game action bar");
        public static final ConfigBoolean MOUSE_SCROLL_DEBUG        = new ConfigBoolean("mouseScrollDebug", false, "If enabled, some debug values from mouse scrolling\nare printed to the game console/log");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                INPUT_CANCELLATION_DEBUG,
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR,
                MOUSE_SCROLL_DEBUG
        );
    }

    public static void loadFromFile()
    {
        //File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        Path configFile = FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME);

        //if (configFile.exists() && configFile.isFile() && configFile.canRead())
        if (Files.exists(configFile) && Files.isReadable(configFile))
        {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);

                MaLiLib.logger.debug("loadFromFile(): Successfully loaded config file '{}'.", configFile.toAbsolutePath());
            }
            else
            {
                MaLiLib.logger.error("loadFromFile(): Failed to parse config file as a JSON element '{}'.", configFile.toAbsolutePath());
            }
        }
        else
        {
            MaLiLib.logger.error("loadFromFile(): Failed to load config file '{}'.", configFile.toAbsolutePath());
        }
    }

    public static void saveToFile()
    {
        //File dir = FileUtils.getConfigDirectory();
        Path dir = FileUtils.getConfigDirectoryAsPath();

        //if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        if (!Files.exists(dir))
        {
            FileUtils.createDirectoriesIfMissing(dir);
            MaLiLib.logger.debug("saveToFile(): Creating directory '{}'.", dir.toAbsolutePath());
        }

        if (Files.isDirectory(dir))
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);

            //JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
            Path config = dir.resolve(CONFIG_FILE_NAME);

            if (JsonUtils.writeJsonToFileAsPath(root, config))
            {
                MaLiLib.logger.debug("saveToFile(): Successfully saved config file '{}'.", config.toAbsolutePath());
            }
            else
            {
                MaLiLib.logger.error("saveToFile(): Failed to save config file '{}'.", config.toAbsolutePath());
            }
        }
        else
        {
            MaLiLib.logger.error("saveToFile(): Config Folder '{}' does not exist!", dir.toAbsolutePath());
        }
    }

    @Override
    public void onConfigsChanged()
    {
        saveToFile();
        loadFromFile();
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
