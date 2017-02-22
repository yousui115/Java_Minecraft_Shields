package yousui115.shields.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import yousui115.shields.CommonProxy;
import yousui115.shields.util.SItems;

public class ClientProxy extends CommonProxy
{
    /**
     * ■ModelBakery に Item と ResourceLocation の関連を登録
     */
    @Override
    public void registerModels()
    {
        SItems.registerModels();
    }

    /**
     * ■
     */
    @Override
    public void registerRenderers()
    {
        SItems.registerRenderers();
    }

    /**
     * ■イベントの登録
     */
    @Override
    public void registerEvent()
    {
        super.registerEvent();
    }

    /**
     * ■プレイヤーの取得
     */
    @Nullable
    @Override
    public EntityPlayer getThePlayer() { return Minecraft.getMinecraft().thePlayer; }
}
