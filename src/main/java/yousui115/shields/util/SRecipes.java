package yousui115.shields.util;

import static net.minecraftforge.oredict.RecipeSorter.Category.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import yousui115.shields.Shields;
import yousui115.shields.item.ItemSShield;
import yousui115.shields.item.ItemSShield.EnumShieldState;
import yousui115.shields.recipe.RecipeShield;


public class SRecipes
{
    public static void init()
    {
        //■
        RecipeSorter.register(Shields.MOD_ID + ":shield",    RecipeShield.class,    SHAPED, "after:minecraft:shaped");

        //■登録
        register();
    }

    private static void register()
    {
        //■木の盾
        ItemStack stack = new ItemStack(SItems.SHIELD);
        ItemSShield.setShieldState(stack, EnumShieldState.WOOD);
        GameRegistry.addRecipe(stack.copy(),
                        new Object[]
                        {
                            "#w#",
                            "###",
                            " # ",
                            '#', Blocks.PLANKS,
                            'w', Blocks.LOG
                        }
                    );
        GameRegistry.addRecipe(stack.copy(),
                        new Object[]
                        {
                            "#w#",
                            "###",
                            " # ",
                            '#', Blocks.PLANKS,
                            'w', Blocks.LOG2
                        }
                    );

        //■木の盾 -> バニラの盾
        GameRegistry.addRecipe(new RecipeShield(new ItemStack(Item.getItemFromBlock(Blocks.PLANKS)),
                                                new ItemStack(Items.IRON_INGOT),
                                                stack.copy(),
                                                new ItemStack(Items.SHIELD)));

        //■バニラの盾 -> ダイヤモンドの盾
        ItemSShield.setShieldState(stack, EnumShieldState.DIAMOND);
        GameRegistry.addRecipe(new RecipeShield(new ItemStack(Items.DIAMOND),
                                                new ItemStack(Items.ENDER_PEARL),
                                                new ItemStack(Items.SHIELD),
                                                stack.copy()));

        //■バニラの盾 -> 黒曜石の盾
        ItemSShield.setShieldState(stack, EnumShieldState.OBSIDIAN);
        GameRegistry.addRecipe(new RecipeShield(new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN)),
                                                new ItemStack(Items.ENDER_PEARL),
                                                new ItemStack(Items.SHIELD),
                                                stack.copy()));
    }
}
