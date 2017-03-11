package yousui115.shields.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import yousui115.shields.Shields;

public class EnchantmentSMaiden extends Enchantment
{
    public EnchantmentSMaiden(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots)
    {
        super(rarityIn, typeIn, slots);
        this.setName(Shields.MOD_ID + ".maiden");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) { return Integer.MAX_VALUE - 2; }
    @Override
    public int getMaxEnchantability(int enchantmentLevel) { return Integer.MAX_VALUE - 1; }

}
