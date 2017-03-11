package yousui115.shields;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yousui115.shields.network.PacketHandler;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SItems;
import yousui115.shields.util.SRecipes;

@Mod(modid = Shields.MOD_ID, name = "Shields" ,version = Shields.VERSION)
public class Shields
{
    public static final String MOD_ID = "shields";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;
    public static final String VERSION = "M1102_F2185_v4";

    public static boolean isInstShield = false;

    @Instance(Shields.MOD_ID)
    public static Shields instance;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy", serverSide = MOD_DOMAIN + ".CommonProxy")
    public static CommonProxy proxy;

    /**
     * ■初期化処理 (前処理)
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //■アイテムの生成と登録
        SItems.init();

        //■エンチャントの生成と登録
        SEnchants.init();

        //■メッセージの初期設定
        PacketHandler.init();
    }

    /**
     * ■初期化処理 (本処理)
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■Shieldを導入しているか否か
        for (ModContainer cont : Loader.instance().getModList())
        {
            if (cont.getModId().equals("shield")) { isInstShield = true; }
        }

        //■キーバインドの登録
        proxy.registerKeyBinding();

        //■レンダラーの登録
        proxy.registerRenderers();

        //■レシピの登録
        SRecipes.init();

        //■イベントフックの登録
        proxy.registerEvent();
    }
}
