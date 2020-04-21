package top.gardel.mc.autofish.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.gardel.mc.autofish.event.CaughtFishCallBack;

@Mixin(FishingBobberEntity.class)
public class FishMixin {

	@Shadow
	private static TrackedData<Boolean> CAUGHT_FISH;

	@Shadow
	private boolean caughtFish;

	@Inject(at = @At("RETURN"), method = "onTrackedDataSet", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
		if(CAUGHT_FISH.equals(data)) {
			FishingBobberEntity thiz = (FishingBobberEntity) (Object) this;
			if (thiz.getDataTracker().get(CAUGHT_FISH)) {
				if (thiz.getOwner() instanceof ClientPlayerEntity) {
					ActionResult result = CaughtFishCallBack.EVENT.invoker().interact(thiz.getOwner());
					if (result == ActionResult.FAIL) {
						caughtFish = false;
					}
				}
			}
		}
	}
}
