package top.gardel.mc.autofish;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import top.gardel.mc.autofish.event.CaughtFishCallBack;

import java.util.Timer;
import java.util.TimerTask;

public class AutoFishMod implements ModInitializer {
    private Timer timer;

    @Override
    public void onInitialize() {

        timer = new Timer(false);
        CaughtFishCallBack.EVENT.register((player) ->
        {
            if (player instanceof ClientPlayerEntity) {
                //player.sendMessage(new LiteralText("鱼上钩了"), false);
                ItemStack mainHandItem = player.getMainHandStack();
                ItemStack offHandItem = player.getOffHandStack();
                ItemStack fishingRod = null;
                Hand hand = null;
                if (mainHandItem.getItem() == Items.FISHING_ROD) {
                    fishingRod = mainHandItem;
                    hand = Hand.MAIN_HAND;
                } else if (offHandItem.getItem() == Items.FISHING_ROD) {
                    fishingRod = offHandItem;
                    hand = Hand.OFF_HAND;
                }
                if (fishingRod != null) {
                    assert MinecraftClient.getInstance().interactionManager != null;
                    MinecraftClient.getInstance().interactionManager.interactItem(player, player.world, hand);
                    if (fishingRod.getMaxDamage() - fishingRod.getDamage() > 3) {
                        Hand finalHand = hand;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MinecraftClient.getInstance().interactionManager.interactItem(player, player.world, finalHand);
                            }
                        }, 500);
                    }
                }
            }
            return ActionResult.SUCCESS;
        });
    }
}