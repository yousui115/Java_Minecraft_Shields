package yousui115.shields.recipe;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import yousui115.shields.item.ItemSShield;


public class RecipeShield implements IRecipe
{
    private final ItemStack material;
    private final ItemStack key;
    private final ItemStack shield;
    private final ItemStack result;

    private ItemStack[] recipe;

    public RecipeShield(ItemStack materialIn, ItemStack keyIn, @Nonnull ItemStack shieldIn, @Nonnull ItemStack resultIn)
    {
        material = materialIn;
        key = keyIn;
        shield = shieldIn;
        result = resultIn;

        ItemStack[] temp = {material, key, material,
                            material, shield, material,
                            null, material, null};
        recipe = temp;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        boolean match = true;

        for (int idx = 0; idx < inv.getSizeInventory(); idx++)
        {
            ItemStack stack1 = inv.getStackInSlot(idx);
            ItemStack stack2 = recipe[idx];

            //▼盾部分の比較
            if (ItemStack.areItemsEqual(stack1, stack2))
            {
                continue;
            }
            else if (stack1 != null && stack2 != null)
            {
                if (stack1.getItem() == Item.getItemFromBlock(Blocks.PLANKS) &&
                    stack2.getItem() == Item.getItemFromBlock(Blocks.PLANKS))
                {
                    continue;
                }
                else if (stack1.getItem() instanceof ItemSShield &&
                         stack2.getItem() instanceof ItemSShield)
                {
                    ItemSShield.EnumShieldState state1 = ItemSShield.getShieldState(stack1);
                    ItemSShield.EnumShieldState state2 = ItemSShield.getShieldState(stack2);

                    //■同じ物である。
                    if (state1 == state2) { continue; }
                }
                else if (stack1.getItem() == Items.SHIELD &&
                         stack2.getItem() == Items.SHIELD)
                {
                    //■耐久値は無視する。
                    continue;
                }
            }

            match = false;
            break;
        }

        return match;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int idx;
        for (idx = 0; idx < inv.getInventoryStackLimit(); idx++)
        {
            if (inv.getStackInSlot(idx) != null && inv.getStackInSlot(idx).getItem() instanceof ItemShield) { break; }
        }

        ItemStack stack = result.copy();

        ItemSShield.EnumShieldState state = ItemSShield.getShieldState(stack);

        stack.setTagCompound(inv.getStackInSlot(idx).getTagCompound());

        if (state != null) { ItemSShield.setShieldState(stack, state); }

        return stack;
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    /**
     * ■Remaining : 残った，残りの
     */
    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;

    }

}
