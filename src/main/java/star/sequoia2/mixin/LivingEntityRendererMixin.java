package star.sequoia2.mixin;

import com.wynntils.services.hades.HadesUser;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.features.impl.PartyHealthDisplay;
import star.sequoia2.utils.render.TextureStorage;

import java.util.List;

import static star.sequoia2.utils.wynn.HadesUtils.cachedHadesUsers;
import static star.sequoia2.utils.wynn.HadesUtils.cachedPartyMembers;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends EntityRenderer<T, S>
        implements FeatureRendererContext<S, M>, FeaturesAccessor {
    @Shadow
    @Final
    protected List<FeatureRenderer<S, M>> features;
    @Unique
    private LivingEntity mainLivingEntityThing;

    @Unique private static final float texW = 256F;
    @Unique private static final float texH = 256F;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    public void updateRenderState(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci){
        mainLivingEntityThing = livingEntity;
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    public void render(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (mainLivingEntityThing != null && features().getIfActive(PartyHealthDisplay.class).map(PartyHealthDisplay::isActive).orElse(false)) {
            renderHearts(mainLivingEntityThing, livingEntityRenderState.bodyYaw, 0, matrixStack, vertexConsumerProvider, i);
        }
    }
    @Unique private void renderHearts(LivingEntity livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light){
        float d = (float) Math.sqrt(this.dispatcher.getSquaredDistanceToCamera(livingEntity));

        float scale = Math.max(0.0015f * d * features().getIfActive(PartyHealthDisplay.class).map(partyHealthDisplay -> partyHealthDisplay.getMin().get()).orElse(1.0f), 0.02f * features().getIfActive(PartyHealthDisplay.class).map(partyHealthDisplay -> partyHealthDisplay.getMax().get()).orElse(1.0f));

        String name;
        try {
            name = ((PlayerEntity) livingEntity).getGameProfile().getName();
            if (!cachedPartyMembers.contains(name)) return;

            HadesUser hadesUser = cachedHadesUsers.get(name);
            if (hadesUser == null) return;

            float hpProgress = (float) hadesUser.getHealth().getProgress();
            float manaProgress = (float) hadesUser.getMana().getProgress();

            matrixStack.push();

            matrixStack.translate(0, 2.8, 0);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.scale(scale, scale, scale);
            Matrix4f model = matrixStack.peek().getPositionMatrix();

            RenderLayer renderLayer;
            renderLayer = RenderLayer.getText(TextureStorage.bars);

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);

            float width = 99;
            float height = 13;

            float x = -width / 2.0F;
            float y = 0F;

            float opacity = Math.max(Math.min(1, (2-d/100)), 0);

            if (hpProgress < 1) {
                drawBar(vertexConsumer, model, x, y, width, height,
                        0, 0, 99, 13, Math.min(hpProgress, 1), 1F, texW, texH, opacity);

                drawBar(vertexConsumer, model, x, y, width, height,
                        0, 16, 99, 13, 0F, Math.min(hpProgress, 1), texW, texH, opacity);
            } else {
                drawBar(vertexConsumer, model, x, y, width, height,
                        0, 16, 99, 13, 0F, Math.min(2 - hpProgress, 1), texW, texH, opacity);

                drawBar(vertexConsumer, model, x, y, width, height,
                        0, 32, 99, 13, Math.min(2 - hpProgress, 1), 1, texW, texH, opacity);
            }

            drawBar(vertexConsumer, model, x, y, width, height,
                    0, 48, 99, 13, Math.max(Math.min(manaProgress, 1F), 0F), 1F, texW, texH, opacity);

            drawBar(vertexConsumer, model, x, y, width, height,
                    0, 64, 99, 13, 0F,  Math.max(Math.min(manaProgress, 1F), 0F), texW, texH, opacity);

            matrixStack.pop();

        } catch (RuntimeException e) {
//            Sequoia2.debug(e.getMessage());
        }
    }

    @Unique
    private void drawBar(VertexConsumer vc, Matrix4f matrix, float x, float y, float width, float height,
                         float texX, float texY, float texW, float texH,
                         float progressStart, float progressEnd, float spriteSheetW, float spriteSheetH, float opacity) {

        float fullU = texW / spriteSheetW;
        float fullV = texH / spriteSheetH;
        float minU = texX / spriteSheetW + fullU * progressEnd * progressStart;
        float minV = texY / spriteSheetH;
        float maxU = minU + fullU * progressEnd;
        float maxV = minV + fullV;

        // Counter-clockwise vertex order
        vc.vertex(matrix, x + width * progressStart,         y,          0.0F).texture(minU, minV).light(15728880).color(1F, 1F, 1F, opacity); // Bottom-left
        vc.vertex(matrix, x + width * progressStart,         y - height, 0.0F).texture(minU, maxV).light(15728880).color(1F, 1F, 1F, opacity); // Top-left
        vc.vertex(matrix, x + width * progressStart + width * progressEnd, y - height, 0.0F).texture(maxU, maxV).light(15728880).color(1F, 1F, 1F, opacity); // Top-right
        vc.vertex(matrix, x + width * progressStart + width * progressEnd, y,          0.0F).texture(maxU, minV).light(15728880).color(1F, 1F, 1F, opacity); // Bottom-right
    }

}