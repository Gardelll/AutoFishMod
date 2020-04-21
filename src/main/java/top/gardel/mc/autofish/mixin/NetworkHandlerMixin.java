package top.gardel.mc.autofish.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

// 通过声音判断
// 事实上这个类完全不需要, 因为即使是在服务器上, 客户端的浮漂实体仍会计算是否有物品上钩

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerMixin {
    private FishingBobberEntity myFishingBobber = null;

    @Inject(at = @At("RETURN"),
            method = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo info, double d, double e, double f, Entity entity40, EntityType<?> entityType) {
        if (packet.getEntityTypeId() != EntityType.FISHING_BOBBER) return;

        //System.out.println(packet.toString());

        if (!(((Object)this) instanceof ClientPlayNetworkHandler)) return;
        try {
            ClientPlayNetworkHandler thiz = (ClientPlayNetworkHandler) (Object) this;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.isInSingleplayer()) return;
            if (client.player != null) {
                if (client.player.getUuid().equals(Objects.requireNonNull(((FishingBobberEntity) entity40).getOwner()).getUuid())) {
                    myFishingBobber = (FishingBobberEntity) entity40;
                }
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Inject(at = @At("RETURN"), method = "onPlaySound", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo info) {
        if (myFishingBobber == null || packet.getSound() != SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) return;

        if (!myFishingBobber.isAlive()) {
            myFishingBobber = null;
            return;
        }

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        Vec3d pos = myFishingBobber.getPos();

        // 声音的位置与鱼钩的位置在3格之内
        if (pos.distanceTo(new Vec3d(x, y, z)) < 3) {
            System.out.println("检测到鱼上钩");
            //CaughtFishCallBack.EVENT.invoker().interact(myFishingBobber.getOwner());
        }
    }
}
