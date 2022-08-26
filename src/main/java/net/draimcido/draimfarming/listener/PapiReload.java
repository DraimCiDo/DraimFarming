package net.draimcido.draimfarming.listener;

import net.draimcido.draimfarming.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PapiReload implements Listener {

    @EventHandler
    public void onReload(me.clip.placeholderapi.events.ExpansionUnregisterEvent event){
        if (Main.placeholders != null)
            if (event.getExpansion().equals(Main.placeholders))
                Main.placeholders.register();
    }
}
