package yousui115.shields.util;

import static net.minecraftforge.oredict.RecipeSorter.Category.*;

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
        //TODO もうちょっと綺麗にかけるだろう！

        //■ベース
        ItemStack[] astackShields = new ItemStack[EnumShieldState.values().length];
        for (EnumShieldState state : EnumShieldState.values())
        {
            ItemStack stackShields = new ItemStack(SItems.SHIELD);

            ItemShields.setShieldState(stackShields, state);
            ItemShields.setShieldStates(stackShields, state);

            astackShields[state.ordinal()] = stackShields;
        }

        //■木の盾
        SRecipes.addRecipe(astackShields[EnumShieldState.WOOD.ordinal()].copy(), Blocks.PLANKS, Blocks.LOG, Blocks.PLANKS);

        SRecipes.addRecipe(astackShields[EnumShieldState.WOOD.ordinal()].copy(), Blocks.PLANKS, Blocks.LOG2, Blocks.PLANKS);

        //■木の盾 -> バニラの盾
        SRecipes.addRecipe(new ItemStack(Items.SHIELD), Blocks.PLANKS, Items.IRON_INGOT, astackShields[EnumShieldState.WOOD.ordinal()].copy());

        //■上位盾
        Object[][] material = {{Items.STICK, Items.STICK},
                               {Blocks.MAGMA, Items.QUARTZ},
                               {Blocks.ICE, Blocks.PACKED_ICE},
                               {Items.DIAMOND, Items.ENDER_PEARL},
                               {Blocks.OBSIDIAN, Items.ENDER_PEARL}};

        for (EnumShieldState stateResult : EnumShieldState.values())
        {
            if (stateResult == EnumShieldState.WOOD) { continue; }

            //■バニラの盾 -> 追加盾
            SRecipes.addRecipe(astackShields[stateResult.ordinal()].copy(), material[stateResult.ordinal()][0], material[stateResult.ordinal()][1], Items.SHIELD);

            //■追加盾 -> 追加盾
            for (EnumShieldState state : EnumShieldState.values())
            {
                if (state != EnumShieldState.WOOD && state != stateResult)
                {
                    SRecipes.addRecipe(astackShields[stateResult.ordinal()].copy(), material[stateResult.ordinal()][0], material[stateResult.ordinal()][1], astackShields[state.ordinal()].copy());
                }
            }

        }

//        SRecipes.addRecipe(astackShields[EnumShieldState.FLAME.ordinal()].copy(), Blocks.MAGMA, Items.QUARTZ, Items.SHIELD);
//
//        for (EnumShieldState state : EnumShieldState.values())
//        {
//            if (state != EnumShieldState.WOOD && state != EnumShieldState.FLAME)
//            {
//                SRecipes.addRecipe(astackShields[EnumShieldState.FLAME.ordinal()].copy(), Blocks.MAGMA, Items.QUARTZ, astackShields[state.ordinal()].copy());
//            }
//        }
//
//        //■氷の盾
//        SRecipes.addRecipe(astackShields[EnumShieldState.ICE.ordinal()].copy(), Blocks.ICE, Blocks.PACKED_ICE, Items.SHIELD);
//
//        for (EnumShieldState state : EnumShieldState.values())
//        {
//            if (state != EnumShieldState.WOOD && state != EnumShieldState.ICE)
//            {
//                SRecipes.addRecipe(astackShields[EnumShieldState.ICE.ordinal()].copy(), Blocks.ICE, Blocks.PACKED_ICE, astackShields[state.ordinal()].copy());
//            }
//        }
//
//        //■ダイヤモンドの盾
//        SRecipes.addRecipe(astackShields[EnumShieldState.DIAMOND.ordinal()].copy(), Items.DIAMOND, Items.ENDER_PEARL, Items.SHIELD);
//
//        for (EnumShieldState state : EnumShieldState.values())
//        {
//            if (state != EnumShieldState.WOOD && state != EnumShieldState.DIAMOND)
//            {
//                SRecipes.addRecipe(astackShields[EnumShieldState.DIAMOND.ordinal()].copy(), Items.DIAMOND, Items.ENDER_PEARL, astackShields[state.ordinal()].copy());
//            }
//        }
//
//        //■黒曜石の盾
//        SRecipes.addRecipe(astackShields[EnumShieldState.OBSIDIAN.ordinal()].copy(), Blocks.OBSIDIAN, Items.ENDER_PEARL, Items.SHIELD);
//
//        for (EnumShieldState state : EnumShieldState.values())
//        {
//            if (state != EnumShieldState.WOOD && state != EnumShieldState.OBSIDIAN)
//            {
//                SRecipes.addRecipe(astackShields[EnumShieldState.OBSIDIAN.ordinal()].copy(), Blocks.OBSIDIAN, Items.ENDER_PEARL, astackShields[state.ordinal()].copy());
//            }
//        }
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
