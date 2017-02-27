package yousui115.shields;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import yousui115.shields.event.ShieldHndls;
import yousui115.shields.event.ShieldsHndls;

public class CommonProxy
{
    public void stackInputDate(){}
    public boolean isKeyUp() { return false; }
    public boolean isKeyPush() { return false; }
    public void resetStackDate(){}

    public void registerKeyBinding(){}

    public void registerModels(){}

    public void registerRenderers(){}

    public void registerEvent()
    {
//        MinecraftForge.EVENT_BUS.register(new FirstLoginGiveItemEvent());

        if (Shields.isInstShield)
        {
            MinecraftForge.EVENT_BUS.register(new ShieldHndls());
        }

        MinecraftForge.EVENT_BUS.register(new ShieldsHndls());
    }

    @Nullable
    public EntityPlayer getThePlayer() { return null; }
}
