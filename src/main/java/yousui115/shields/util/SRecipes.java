package yousui115.shields.util;

import static net.minecraftforge.oredict.RecipeSorter.Category.*;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemShields;
import yousui115.shields.item.ItemShields.EnumShieldState;
import yousui115.shields.recipe.RecipeShields;

import com.google.common.collect.Maps;


public class SRecipes
{
    public static void init()
    {
        //■
        RecipeSorter.register(Shields.MOD_ID + ":shields",    RecipeShields.class,    SHAPED, "after:minecraft:shaped");

        //■登録
        register();
    }

    private static void register()
    {
        //TODO もうちょっと綺麗にかけないものか

        //■ベース
        HashMap<EnumShieldState, ItemStack> mapShields = Maps.newHashMapWithExpectedSize(EnumShieldState.values().length);
        for (EnumShieldState state : EnumShieldState.values())
        {
            ItemStack stackShields = new ItemStack(SItems.SHIELD);

            ItemShields.setShieldState(stackShields, state);
            ItemShields.setShieldStates(stackShields, state);

            mapShields.put(state, stackShields);
        }

        //■木の盾
        SRecipes.addRecipe(mapShields.get(EnumShieldState.WOOD).copy(), Blocks.PLANKS, Blocks.LOG, Blocks.PLANKS);
        SRecipes.addRecipe(mapShields.get(EnumShieldState.WOOD).copy(), Blocks.PLANKS, Blocks.LOG2, Blocks.PLANKS);

        //■木の盾 -> バニラの盾
        SRecipes.addRecipe(new ItemStack(Items.SHIELD), Blocks.PLANKS, Items.IRON_INGOT, mapShields.get(EnumShieldState.WOOD).copy());

        //■上位盾
        for (EnumShieldState stateResult : EnumShieldState.values())
        {
            //■盾生成の為の「マテリアル」「キー」を取得
            Object[] obj = stateResult.getObjectMaterial();
            if (obj[0] == null && obj[1] == null) { continue; }

            //■バニラの盾 -> 追加盾 のクラフトレシピ
            SRecipes.addRecipe(mapShields.get(stateResult).copy(), obj[0], obj[1], Items.SHIELD);

            //■追加盾 -> 追加盾 のクラフトレシピ
            for (EnumShieldState state : EnumShieldState.values())
            {
                if (state != EnumShieldState.WOOD && state != stateResult)
                {
                    SRecipes.addRecipe(mapShields.get(stateResult).copy(), obj[0], obj[1], mapShields.get(state).copy());
                }
            }
        }
    }


    private static void addRecipe(ItemStack resultIn, Object ...materialIn)
    {
        if (materialIn.length != 3) { return; }

        GameRegistry.addRecipe(createRecipe(resultIn,
                new Object[]
                {
                    "#w#",
                    "#S#",
                    " # ",
                    '#', materialIn[0],
                    'w', materialIn[1],
                    'S', materialIn[2],
                }
            ));

    }

    private static ShapedRecipes createRecipe(ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (String s2 : astring)
            {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[l] = null;
            }
        }

        ShapedRecipes shapedrecipes = new RecipeShields(j, k, aitemstack, stack);
        return shapedrecipes;
    }

}
