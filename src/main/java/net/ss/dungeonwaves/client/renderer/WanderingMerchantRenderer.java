package net.ss.dungeonwaves.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ss.dungeonwaves.entity.WanderingMerchantEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WanderingMerchantRenderer extends MobRenderer<WanderingMerchantEntity, VillagerModel<WanderingMerchantEntity>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("minecraft","textures/entity/wandering_trader.png");

    public WanderingMerchantRenderer (EntityRendererProvider.Context p_174441_) {
        super(p_174441_, new VillagerModel<>(p_174441_.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, p_174441_.getModelSet(), p_174441_.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<>(this, p_174441_.getItemInHandRenderer()));
    }

    public @NotNull ResourceLocation getTextureLocation (@NotNull WanderingMerchantEntity p_116373_) {
        return VILLAGER_BASE_SKIN;
    }

    protected void scale (@NotNull WanderingMerchantEntity p_116375_, PoseStack p_116376_, float p_116377_) {
        float $$3 = 0.9375F;
        p_116376_.scale($$3, $$3, $$3);
    }
}
