package fun.mntale.midnightcore;

import com.tcoded.folialib.FoliaLib;
import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fun.mntale.midnightcore.command.*;

public final class MidnightCore extends JavaPlugin implements Listener {
    public static MidnightCore instance;
    public FoliaLib foliaLib;

    @Override
    public void onEnable() {
        instance = this;
        foliaLib = new FoliaLib(this);

        BasicCommand PlayerCommand = new PlayerCommand();
        registerCommand("player", PlayerCommand);
    }
}