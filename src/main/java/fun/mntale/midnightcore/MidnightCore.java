package fun.mntale.midnightcore;

import com.tcoded.folialib.FoliaLib;
import fun.mntale.midnightcore.api.task.PlayerTaskApi;
import fun.mntale.midnightcore.command.*;
import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MidnightCore extends JavaPlugin implements Listener {
    private static MidnightCore instance;
    private FoliaLib foliaLib;

    @Override
    public void onEnable() {
        instance = this;
        foliaLib = new FoliaLib(this);

        getServer().getPluginManager().registerEvents(new PlayerTaskApi(), this);

        BasicCommand taskCommand = new TaskCommand();
        registerCommand("task", taskCommand);

        BasicCommand playerCommand = new PlayerCommand();
        registerCommand("player", playerCommand);
    }

    public static MidnightCore getInstance() {
        return instance;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }
}