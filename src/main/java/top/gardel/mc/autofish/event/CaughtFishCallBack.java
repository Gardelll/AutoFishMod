package top.gardel.mc.autofish.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface CaughtFishCallBack {
    Event<CaughtFishCallBack> EVENT = EventFactory.createArrayBacked(CaughtFishCallBack.class,
            (listeners) -> (player) -> {
                for (CaughtFishCallBack listener : listeners) {
                    ActionResult result = listener.interact(player);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player);
}
