package yousui115.shields.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemSShield;

@SideOnly(Side.CLIENT)
public class TileEntityShieldRenderer extends TileEntitySpecialRenderer<TileEntityShield>
{
    protected ModelShield shield = new ModelShield();
//    public static final ResourceLocation SSHIELD_BASE_TEXTURE = new ResourceLocation(Soul.MOD_ID, "textures/entity/shield_base_nopattern.png");
//    public static final ResourceLocation SSHIELD_TEXTURE = new ResourceLocation(Soul.MOD_ID, "textures/entity/sshield.png");

    public ResourceLocation SSHIELD_TEXTURE;
    public ItemSShield.EnumShieldState state;

    public TileEntityShieldRenderer(ItemSShield.EnumShieldState stateIn)
    {
        state = stateIn;
        SSHIELD_TEXTURE = new ResourceLocation(Shields.MOD_ID, "textures/entity/shields_" + stateIn.nameMaterial + ".png");
    }

    @Override
    public void renderTileEntityAt(TileEntityShield te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SSHIELD_TEXTURE);

//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220f, 220f);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.shield.render();
        GlStateManager.popMatrix();

    }

}
