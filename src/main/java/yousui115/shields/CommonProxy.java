package yousui115.shields;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import yousui115.shields.event.ShieldHndls;

public class CommonProxy
{
    public void registerModels(){}

    public void registerRenderers(){}

    public void registerEvent()
    {
//        MinecraftForge.EVENT_BUS.register(new FirstLoginGiveItemEvent());

        if (Shields.isInstShield)
        {
            MinecraftForge.EVENT_BUS.register(new ShieldHndls());
        }
    }

    @Nullable
    public EntityPlayer getThePlayer() { return null; }
}
