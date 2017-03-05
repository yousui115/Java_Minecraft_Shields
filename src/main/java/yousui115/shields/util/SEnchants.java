package yousui115.shields.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.shields.Shields;
import yousui115.shields.enchantment.EnchantmentSMaiden;

public class SEnchants
{
    //■「盾の乙女」
    public static Enchantment ENCH_MAIDEN;

    /**
     * ■
     */
    public static void init()
    {
        createEnch();

        registerEnch();
    }

    /**
     * ■
     */
    public static void createEnch()
    {
        ENCH_MAIDEN = new EnchantmentSMaiden(Enchantment.Rarity.VERY_RARE,
                                             EnumEnchantmentType.BREAKABLE,
                                             new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
    }

    /**
     * ■
     */
    public static void registerEnch()
    {
        GameRegistry.register(ENCH_MAIDEN, new ResourceLocation(Shields.MOD_ID, "maiden"));
    }
}
