package yousui115.shields.recipe;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.util.SUtils;

public class RecipeShields extends ShapedOreRecipe
{
    /**
     * ■　コンストラクタ群
     */
    public RecipeShields(ResourceLocation group, ItemStack result, Object... recipe)
    {
        super(group, result, recipe);
    }

    /**
     * ■　マッチン、グー！
     */
//    @Override
//    public boolean matches(InventoryCrafting inv, World worldIn)
//    {
//        //■例えば、2行3列のレシピが上2行でも、下2行でも作れるようにしてる。
//        for (int i = 0; i <= 3 - this.width; ++i)
//        {
//            for (int j = 0; j <= 3 - this.height; ++j)
//            {
//                if (this.checkMatch2(inv, i, j, true))
//                {
//                    return true;
//                }
//
//                if (this.checkMatch2(inv, i, j, false))
//                {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }


    @Override
    protected boolean checkMatch(InventoryCrafting invIn, int widthIn, int heightIn, boolean isReverse)
    {
        //■インベントリの左上からクルクル回す。
        for (int idW = 0; idW < invIn.getWidth(); ++idW)
        {
            for (int idH = 0; idH < invIn.getWidth(); ++idH)
            {
                int posW = idW - widthIn;
                int posH = idH - heightIn;
//                ItemStack stackRecipe = ItemStack.EMPTY;
                Ingredient target = Ingredient.EMPTY;

                if (posW >= 0 && posH >= 0 && posW < this.width && posH < this.height)
                {
                    if (isReverse)
                    {
//                        stackRecipe = this.recipeItems[this.recipeWidth - posW - 1 + posH * this.recipeWidth];
                        target = this.input.get(width - posW - 1 + posH * width);

                    }
                    else
                    {
//                        stackRecipe = this.recipeItems[posW + posH * this.recipeWidth];
                        target = this.input.get(posW + posH * width);
                    }
                }

                //■クラフトテーブルに置かれたアイテム
                ItemStack stackInv = invIn.getStackInRowAndColumn(idW, idH);

                //■調査対象：盾
                if (stackInv.getItem() instanceof ItemShield)
                {
                    //■上位盾の合成状態の確認
                    if (stackInv.getItem() instanceof ItemShields &&        //テーブルの盾が追加盾
//                        stackRecipe.getItem() instanceof ItemShields &&
                        this.getRecipeOutput().getItem() != Items.SHIELD)   //出力する盾がバニラ盾では無い。
                    {
                        //■出力盾のstateを取得
                        EnumShieldState stateResult = ItemShields.getShieldState(this.getRecipeOutput());

                        //■木の盾ではない。
                        if (stateResult != EnumShieldState.WOOD)
                        {
                            //■合成状態一覧の取得
                            int states = ItemShields.getShieldStates(stackInv);

                            //■合成済みなら、クラフト不可
                            if ((states & stateResult.bit) == stateResult.bit) { return false; }
                        }
                    }

                    stackInv = stackInv.copy();
                    stackInv.setItemDamage(0);
                }

                //■アイテムが一致しているか否か
                if (!target.apply(stackInv))
                {
                    return false;
                }




//                //■比較
//                if (!stackInv.isEmpty() || !stackRecipe.isEmpty())
//                {
//                    if (stackInv.isEmpty() != stackRecipe.isEmpty())
//                    {
//                        return false;
//                    }
//
//                    if (stackRecipe.getItem() != stackInv.getItem())
//                    {
//                        return false;
//                    }
//
//                    if (stackRecipe.getMetadata() != 32767 &&
//                        stackInv.getItem() != Items.SHIELD &&
//                        stackRecipe.getMetadata() != stackInv.getMetadata())
//                    {
//                        return false;
//                    }
//
//                    //▼既に合成済み
//                    if (stackInv.getItem() instanceof ItemShields &&
//                        stackRecipe.getItem() instanceof ItemShields &&
//                        this.getRecipeOutput().getItem() != Items.SHIELD)
//                    {
//                        EnumShieldState stateResult = ItemShields.getShieldState(this.getRecipeOutput());
//
//                        if (stateResult != EnumShieldState.WOOD)
//                        {
//                            int states = ItemShields.getShieldStates(stackInv);
//
//                            if ((states & stateResult.bit) == stateResult.bit) { return false; }
//                        }
//                    }
//                }

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

            if (!SUtils.isEmptyStack(stackSlot) &&
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
