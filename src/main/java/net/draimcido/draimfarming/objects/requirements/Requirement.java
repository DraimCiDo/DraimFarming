package net.draimcido.draimfarming.objects.requirements;

import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Requirement {

    protected String[] values;
    protected boolean mode;
    protected String msg;

    protected Requirement(@NotNull String[] values, boolean mode, @Nullable String msg) {
        this.values = values;
        this.mode = mode;
        this.msg = msg;
    }

    public void notMetMessage(Player player) {
        if (msg == null) return;
        AdventureUtil.playerMessage(player, this.msg);
    }
}
