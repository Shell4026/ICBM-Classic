package icbm.classic.lib.capability.gps;

import icbm.classic.api.caps.IGPSData;
import icbm.classic.lib.LanguageUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GPSDataHelpers {

    public static boolean handlePlayerInteraction(IGPSData gpsData, PlayerEntity player, BiConsumer<World, Vec3d> setter) {
        return handlePlayerInteraction(gpsData, player, true, setter);
    }

    public static boolean handlePlayerInteraction(IGPSData gpsData, PlayerEntity player, Consumer<Vec3d> setter) {
        return handlePlayerInteraction(gpsData, player, false, (w, v) -> setter.accept(v));
    }

    public static boolean handlePlayerInteraction(IGPSData gpsData, PlayerEntity player, boolean setWorld, BiConsumer<World, Vec3d> setter) {
        if (gpsData == null)
        {
            return false;
        }

        final Vec3d position = gpsData.getPosition();
        final World world = gpsData.getWorld();
        if(position == null) {
            player.sendMessage(new StringTextComponent(LanguageUtility.getLocal("chat.launcher.noTargetInTool")));
        }
        else if(setWorld && world != null) {
            setter.accept(world, position );

            final String x = String.format("%.1f", position .x);
            final String y = String.format("%.1f", position .y);
            final String z = String.format("%.1f", position .z);

            final String name = Optional.of(world.getWorldInfo()).map(WorldInfo::getWorldName).orElse("--");
            final String worldName = String.format("(%s)%s", world.provider.getDimension(), name);

            player.sendMessage(new TranslationTextComponent("info.icbmclassic:gps.set.all", x, y, z, worldName));
        }
        else {
            setter.accept(null, gpsData.getPosition());

            final String x = String.format("%.1f", position .x);
            final String y = String.format("%.1f", position .y);
            final String z = String.format("%.1f", position .z);
            player.sendMessage(new TranslationTextComponent("info.icbmclassic:gps.set.pos", x, y, z));
        }
        return true;
    }
}
