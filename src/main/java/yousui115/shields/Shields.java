package yousui115.shields;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.network.PacketHandler;
import yousui115.shields.util.SEnchants;
import yousui115.shields.util.SItems;
import yousui115.shields.util.SRecipes;

@Mod(modid = Shields.MOD_ID, name = "Shields" ,version = Shields.VERSION)
@EventBusSubscriber
public class Shields
{
    public static final String MOD_ID = "shields";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;
    public static final String VERSION = "M1122_F2611_v1";

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
//        //■アイテムの生成と登録
//        SItems.init();

//        //■エンチャントの生成と登録
//        SEnchants.init();

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
//        SRecipes.init();

        //■イベントフックの登録
        proxy.registerEvent();
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event)
    {
        //■アイテムの生成と登録
        SItems.init();

        event.getRegistry().registerAll(SItems.SHIELD);
    }

    @SubscribeEvent
    protected static void registerEnch(RegistryEvent.Register<Enchantment> event)
    {
        SEnchants.init();

        event.getRegistry().registerAll(SEnchants.ENCH_MAIDEN);
    }

    @SubscribeEvent
    protected static void registerRecipe(RegistryEvent.Register<IRecipe> event)
    {
        //■ベース
        HashMap<EnumShieldState, ItemStack> mapShields = Maps.newHashMapWithExpectedSize(EnumShieldState.values().length);
        for (EnumShieldState state : EnumShieldState.values())
        {
            ItemStack stackShields = new ItemStack(SItems.SHIELD);

            ItemShields.setShieldState(stackShields, state);
            ItemShields.setShieldStates(stackShields, state);

            mapShields.put(state, stackShields);
        }

        //■登録
        // ▼木の盾(LOGが2種類あるから・・・）
        event.getRegistry().register(SRecipes.createSRecipe("shields." +  EnumShieldState.WOOD.nameMaterial,
                                                                "shields.log1",
                                                                mapShields.get(EnumShieldState.WOOD).copy(),
                                                                Blocks.LOG, Blocks.PLANKS, Blocks.PLANKS));
        event.getRegistry().register(SRecipes.createSRecipe("shields." +  EnumShieldState.WOOD.nameMaterial,
                                                                "shields.log2",
                                                                mapShields.get(EnumShieldState.WOOD).copy(),
                                                                Blocks.LOG2, Blocks.PLANKS, Blocks.PLANKS));

        // ▼木の盾 -> バニラの盾
        event.getRegistry().register(SRecipes.createSRecipe("shields.vanilla",
                                                                "shields.vanilla",
                                                                new ItemStack(Items.SHIELD),
                                                                Items.IRON_INGOT, Blocks.PLANKS, mapShields.get(EnumShieldState.WOOD).copy()));

        //■上位盾
        for (EnumShieldState stateResult : EnumShieldState.values())
        {
            //■盾生成の為の「0:マテリアル」「1:キー」を取得
            Object[] obj = stateResult.getObjectMaterial();
            if (obj[0] == null && obj[1] == null) { continue; }

            //■グループ名
            String strBase = "shields.";
            String strGroup = strBase + stateResult.nameMaterial;
            String strName   = strGroup + ".vanilla";

            //■バニラの盾 -> 追加盾 のクラフトレシピ
            event.getRegistry().register(SRecipes.createSRecipe(strGroup,
                                                                strName,
                                                                mapShields.get(stateResult).copy(),
                                                                obj[1], obj[0], Items.SHIELD));

            //■追加盾 -> 追加盾 のクラフトレシピ
            for (EnumShieldState state : EnumShieldState.values())
            {
                //■木の盾と同種盾 以外
                if (state != EnumShieldState.WOOD && state != stateResult)
                {
                    //■レシピ名
                    strName = strGroup + "." + state.nameMaterial;

                    event.getRegistry().register(SRecipes.createSRecipe(strGroup, strName,
                                                                        mapShields.get(stateResult).copy(),
                                                                        obj[1], obj[0], mapShields.get(state).copy()));
                }
            }
        }
    }
}
