package yousui115.shields.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import yousui115.shields.CommonProxy;
import yousui115.shields.util.SItems;

public class ClientProxy extends CommonProxy
{
    private static final KeyBinding changeShieldKey = new KeyBinding("key.shields.change", Keyboard.KEY_R, "Shields Key Config");
    private int keyPressed = 0;

    /**
     * ■「1tickに1回だけ」呼び出す事。厳守。
     */
    @Override
    public void stackInputDate() { keyPressed = (keyPressed << 1) | (changeShieldKey.isKeyDown() ? 1 : 0); }
    @Override
    public boolean isKeyUp() { return (keyPressed & 0x3) == 0x2; }
    @Override
    public boolean isKeyPush() { return (keyPressed & 0x3) == 0x1; }
    @Override
    public void resetStackDate() { keyPressed = 0; }

    /**
     * ■きーばいんど
     */
    @Override
    public void registerKeyBinding()
    {
        ClientRegistry.registerKeyBinding(changeShieldKey);
    }

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
