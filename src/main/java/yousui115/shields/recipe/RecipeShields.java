package yousui115.shields.recipe;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;

public class RecipeShields extends ShapedRecipes
{

    public RecipeShields(int width, int height, ItemStack[] p_i1917_3_, ItemStack output)
    {
        super(width, height, p_i1917_3_, output);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        //■例えば、2行3列のレシピが上2行でも、下2行でも作れるようにしてる。
        for (int i = 0; i <= 3 - this.recipeWidth; ++i)
        {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j)
            {
                if (this.checkMatch2(inv, i, j, true))
                {
                    return true;
                }

                if (this.checkMatch2(inv, i, j, false))
                {
                    return true;
                }
            }
        }

        return false;
    }


    protected boolean checkMatch2(InventoryCrafting invIn, int widthIn, int heightIn, boolean isReverse)
    {
        for (int idW = 0; idW < 3; ++idW)
        {
            for (int idH = 0; idH < 3; ++idH)
            {
                int posW = idW - widthIn;
                int posH = idH - heightIn;
                ItemStack stackRecipe = null;

                if (posW >= 0 && posH >= 0 && posW < this.recipeWidth && posH < this.recipeHeight)
                {
                    if (isReverse)
                    {
                        stackRecipe = this.recipeItems[this.recipeWidth - posW - 1 + posH * this.recipeWidth];
                    }
                    else
                    {
                        stackRecipe = this.recipeItems[posW + posH * this.recipeWidth];
                    }
                }

                ItemStack stackInv = invIn.getStackInRowAndColumn(idW, idH);

                //■比較
                if (stackInv != null || stackRecipe != null)
                {
                    //▼どっちかがnull
                    if (stackInv == null && stackRecipe != null ||
                        stackInv != null && stackRecipe == null)
                    {
                        return false;
                    }

                    //▼アイテムそのものが違う
                    if (stackRecipe.getItem() != stackInv.getItem())
                    {
                        return false;
                    }

                    //▼ブロックではない、かつ、バニラ盾ではない、かつ、メタ値が違う
                    if (stackRecipe.getMetadata() != 32767 &&
                        stackInv.getItem() != Items.SHIELD &&
                        stackRecipe.getMetadata() != stackInv.getMetadata())
                    {
                        return false;
                    }

                    //▼既に合成済み
                    if (stackInv.getItem() instanceof ItemShields &&
                        stackRecipe.getItem() instanceof ItemShields &&
                        this.getRecipeOutput().getItem() != Items.SHIELD)
                    {
                        EnumShieldState stateResult = ItemShields.getShieldState(this.getRecipeOutput());

                        if (stateResult != EnumShieldState.WOOD)
                        {
                            int states = ItemShields.getShieldStates(stackInv);

                            if ((states & stateResult.bit) == stateResult.bit) { return false; }
                        }
                    }

                }
            }
        }

        return true;
    }

    @Override
    @Nullable
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        //■クラフト先
        ItemStack stackResult = this.getRecipeOutput().copy();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            //■インベントリを左上から巡回
            ItemStack stackSlot = inv.getStackInSlot(i);

            if (stackSlot != null &&
                (stackSlot.getItem() == Items.SHIELD || stackSlot.getItem() instanceof ItemShields))
            {
                //■クラフト先の盾の状態
                EnumShieldState state = ItemShields.getShieldState(stackResult);

                //■クラフト元 -> クラフト先 へNBTデータを上書き
                if (stackSlot.hasTagCompound())
                {
                    stackResult.setTagCompound(stackSlot.getTagCompound().copy());
                }

                //■クラフト先の盾の状態を追加
                if (state != null)
                {
                    ItemShields.addShieldStates(stackResult, state);
                    ItemShields.setShieldState(stackResult, state);
                }
            }
        }

        return stackResult;
    }
}
