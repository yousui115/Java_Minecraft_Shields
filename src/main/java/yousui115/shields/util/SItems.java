package yousui115.shields.util;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.shields.Shields;
import yousui115.shields.client.TileEntityShield;
import yousui115.shields.client.TileEntityShieldRenderer;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;

public class SItems
{
    public static Item SHIELD;
    private static ResourceLocation RL_SHIELD;
    private static ResourceLocation RL_SHIELD_B;

    /**
     * ■
     */
    public static void init()
    {
        //■生成
        create();

        //■登録
        registerItems();

        //■モデルの登録
        Shields.proxy.registerModels();
    }

    /**
     * ■
     */
    private static void create()
    {
        //■盾
        SHIELD = (new ItemShields())
                        .setUnlocalizedName("shields")
                        .setCreativeTab(CreativeTabs.COMBAT)
                        .setHasSubtypes(false)
                        .setNoRepair();
        RL_SHIELD   = new ResourceLocation(Shields.MOD_ID, "shields");
        RL_SHIELD_B = new ResourceLocation(Shields.MOD_ID, "shields_blocking");
    }

    /**
     * ■
     */
    private static void registerItems()
    {
        //■盾
        GameRegistry.register(SHIELD, RL_SHIELD);
    }

    /**
     * ■
     */
    @SideOnly(Side.CLIENT)
    public static void registerModels()
    {
        //■
        ModelBakery.registerItemVariants(SItems.SHIELD, SItems.RL_SHIELD, SItems.RL_SHIELD_B);
        ModelLoader.setCustomMeshDefinition(SItems.SHIELD, createMeshDefinition(SItems.RL_SHIELD));
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers()
    {
        //■
        for (EnumShieldState state : EnumShieldState.class.getEnumConstants())
        {
            //■Item, subID <---> TileEntity.class
            ForgeHooksClient.registerTESRItemStack(SItems.SHIELD,
                                                   state.ordinal(),
                                                   TileEntityShield.getClass(state));
            //■TileEntity.class <---> Renderer
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(TileEntityShield.getClass(state),
                                                                          new TileEntityShieldRenderer(state));
        }

    }

    /**
     * ■
     */
    @SideOnly(Side.CLIENT)
    public static ItemMeshDefinition createMeshDefinition(final ResourceLocation rlIn)
    {
        return  new ItemMeshDefinition()
                    {
                        public ModelResourceLocation getModelLocation(ItemStack stackIn)
                        {
//                            int lvl = 0;
//                            if (stackIn.getItem() instanceof ItemEX)
//                            {
//                                ItemEX itemEx = (ItemEX)stackIn.getItem();
//                                lvl = itemEx.getEXInfoFromExp(stackIn).level * 2 - 2;
//                            }
//
//                            ResourceLocation rl = rlIn[lvl];
//                            return new ModelResourceLocation(rl, "inventory");
                            return new ModelResourceLocation(rlIn, "inventory");
                        }
                    };
    }
}
