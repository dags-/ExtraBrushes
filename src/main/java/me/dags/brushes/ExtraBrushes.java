package me.dags.brushes;

import com.thevoxelbox.voxelsniper.Brushes;
import me.dags.brushes.brush.MultiPointBrush;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "ExtraBrushes", dependencies = @Dependency(id = "voxelsniper"))
public class ExtraBrushes {

    @Listener
    public void postInit(GamePostInitializationEvent e) {
        Brushes.get().registerSniperBrush(MultiPointBrush.class, "multi", "mpb");
    }
}
