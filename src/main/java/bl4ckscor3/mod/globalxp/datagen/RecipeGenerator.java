package bl4ckscor3.mod.globalxp.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.globalxp.GlobalXP;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected final void buildRecipes(RecipeOutput recipeOutput) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GlobalXP.XP_BLOCK)
		.pattern("BBB")
		.pattern("BEB")
		.pattern("BBB")
		.define('B', Items.IRON_BARS)
		.define('E', Tags.Items.GEMS_EMERALD)
		.unlockedBy("has_emerald", has(Tags.Items.GEMS_EMERALD))
		.save(recipeOutput);
		//@formatter:on
	}
}
