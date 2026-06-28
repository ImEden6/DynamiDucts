package com.mervyn.dynamiducts.datagen;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.init.DDBlocks;
import com.mervyn.dynamiducts.init.DDItems;
import com.mervyn.dynamiducts.init.DDTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class DDRecipeProvider extends RecipeProvider {

  public DDRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
    super(registries, output);
  }

  private ShapedRecipeBuilder ddShaped(RecipeCategory category, ItemLike result, int count) {
    return ShapedRecipeBuilder.shaped(
        this.registries.lookupOrThrow(Registries.ITEM), category, result, count);
  }

  private ShapelessRecipeBuilder ddShapeless(RecipeCategory category, ItemLike result, int count) {
    return ShapelessRecipeBuilder.shapeless(
        this.registries.lookupOrThrow(Registries.ITEM), category, result, count);
  }

  @Override
  protected void buildRecipes() {
    buildMetalRecipes(this.output);
    buildEnergyDuctRecipes(this.output);
    buildFluidDuctRecipes(this.output);
    buildItemDuctRecipes(this.output);
    buildStructuralRecipes(this.output);
    buildTransportRecipes(this.output);
    buildAttachmentRecipes(this.output);
  }

  public static class Runner extends RecipeProvider.Runner {
    public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
      super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(
        HolderLookup.Provider registries, RecipeOutput output) {
      return new DDRecipeProvider(registries, output);
    }

    @Override
    public String getName() {
      return "Recipes";
    }
  }

  private void buildMetalRecipes(RecipeOutput output) {
    ingotNugget(output, DDItems.LEAD_INGOT, DDItems.LEAD_NUGGET, "lead");
    ingotNugget(output, DDItems.TIN_INGOT, DDItems.TIN_NUGGET, "tin");
    ingotNugget(output, DDItems.SILVER_INGOT, DDItems.SILVER_NUGGET, "silver");
    ingotNugget(output, DDItems.INVAR_INGOT, DDItems.INVAR_NUGGET, "invar");
    ingotNugget(output, DDItems.ELECTRUM_INGOT, DDItems.ELECTRUM_NUGGET, "electrum");
    ingotNugget(output, DDItems.BRONZE_INGOT, DDItems.BRONZE_NUGGET, "bronze");
    ingotNugget(output, DDItems.SIGNALUM_INGOT, DDItems.SIGNALUM_NUGGET, "signalum");
    ingotNugget(output, DDItems.ENDERIUM_INGOT, DDItems.ENDERIUM_NUGGET, "enderium");
    ingotNugget(output, DDItems.LUMIUM_INGOT, DDItems.LUMIUM_NUGGET, "lumium");

    ddShapeless(RecipeCategory.MISC, DDItems.LEAD_INGOT, 2)
        .requires(Items.IRON_INGOT)
        .requires(Items.CHARCOAL)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output, id("lead_ingot_from_iron"));

    ddShapeless(RecipeCategory.MISC, DDItems.TIN_INGOT, 2)
        .requires(Items.IRON_INGOT)
        .requires(Items.QUARTZ)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output, id("tin_ingot_from_iron"));

    ddShapeless(RecipeCategory.MISC, DDItems.SILVER_INGOT, 2)
        .requires(Items.IRON_INGOT)
        .requires(Items.GLOWSTONE_DUST)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output, id("silver_ingot_from_iron"));

    ddShapeless(RecipeCategory.MISC, DDItems.INVAR_INGOT, 3)
        .requires(Items.IRON_INGOT)
        .requires(Items.IRON_INGOT)
        .requires(Items.REDSTONE)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output, id("invar_ingot_from_iron"));

    ddShapeless(RecipeCategory.MISC, DDItems.ELECTRUM_INGOT, 2)
        .requires(Items.GOLD_INGOT)
        .requires(DDTags.INGOTS_SILVER)
        .unlockedBy("has_gold", has(Items.GOLD_INGOT))
        .save(output, id("electrum_ingot_from_gold"));

    ddShapeless(RecipeCategory.MISC, DDItems.BRONZE_INGOT, 4)
        .requires(Items.COPPER_INGOT)
        .requires(Items.COPPER_INGOT)
        .requires(Items.COPPER_INGOT)
        .requires(DDTags.INGOTS_TIN)
        .unlockedBy("has_copper", has(Items.COPPER_INGOT))
        .save(output, id("bronze_ingot_from_copper"));

    ddShaped(RecipeCategory.MISC, DDItems.SIGNALUM_INGOT, 4)
        .pattern("RCR")
        .pattern("CSC")
        .pattern("RCR")
        .define('R', Items.REDSTONE)
        .define('C', Items.COPPER_INGOT)
        .define('S', DDTags.INGOTS_SILVER)
        .unlockedBy("has_copper", has(Items.COPPER_INGOT))
        .save(output, id("signalum_ingot_craft"));

    ddShaped(RecipeCategory.MISC, DDItems.ENDERIUM_INGOT, 4)
        .pattern("ETE")
        .pattern("TST")
        .pattern("ETE")
        .define('E', Items.ENDER_PEARL)
        .define('T', DDTags.INGOTS_TIN)
        .define('S', DDTags.INGOTS_SILVER)
        .unlockedBy("has_ender", has(Items.ENDER_PEARL))
        .save(output, id("enderium_ingot_craft"));

    ddShaped(RecipeCategory.MISC, DDItems.LUMIUM_INGOT, 4)
        .pattern("GTG")
        .pattern("TST")
        .pattern("GTG")
        .define('G', Items.GLOWSTONE_DUST)
        .define('T', DDTags.INGOTS_TIN)
        .define('S', DDTags.INGOTS_SILVER)
        .unlockedBy("has_glowstone", has(Items.GLOWSTONE_DUST))
        .save(output, id("lumium_ingot_craft"));
  }

  private void buildEnergyDuctRecipes(RecipeOutput output) {
    ddShaped(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_BASIC, 6)
        .pattern("RRR")
        .pattern("IGI")
        .pattern("RRR")
        .define('R', Items.REDSTONE)
        .define('I', DDTags.INGOTS_LEAD)
        .define('G', Items.GLASS)
        .unlockedBy("has_lead", has(DDTags.INGOTS_LEAD))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_HARDENED, 1)
        .requires(DDBlocks.ENERGY_DUCT_BASIC)
        .requires(DDTags.NUGGETS_INVAR)
        .requires(DDTags.NUGGETS_INVAR)
        .requires(DDTags.NUGGETS_INVAR)
        .requires(Items.REDSTONE)
        .unlockedBy("has_basic", has(DDBlocks.ENERGY_DUCT_BASIC))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY, 6)
        .pattern("IGI")
        .define('I', DDTags.INGOTS_ELECTRUM)
        .define('G', DDTags.HARDENED_GLASS)
        .unlockedBy("has_electrum", has(DDTags.INGOTS_ELECTRUM))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY, 1)
        .requires(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(Items.REDSTONE)
        .unlockedBy("has_reinforced", has(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_RESONANT_EMPTY, 1)
        .requires(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)
        .requires(DDTags.NUGGETS_ENDERIUM)
        .requires(DDTags.NUGGETS_ENDERIUM)
        .requires(DDTags.NUGGETS_ENDERIUM)
        .requires(Items.REDSTONE)
        .unlockedBy("has_signalum", has(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, 6)
        .pattern("IGI")
        .pattern("GEG")
        .pattern("IGI")
        .define('I', DDTags.INGOTS_ELECTRUM)
        .define('G', DDTags.HARDENED_GLASS)
        .define('E', DDBlocks.ENERGY_DUCT_RESONANT_EMPTY)
        .unlockedBy("has_resonant", has(DDBlocks.ENERGY_DUCT_RESONANT_EMPTY))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_REINFORCED, 1)
        .requires(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY)
        .requires(Items.REDSTONE_BLOCK)
        .unlockedBy("has_empty", has(DDBlocks.ENERGY_DUCT_REINFORCED_EMPTY))
        .save(output, id("energy_duct_reinforced_from_empty"));

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_SIGNALUM, 1)
        .requires(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY)
        .requires(Items.REDSTONE_BLOCK)
        .unlockedBy("has_empty", has(DDBlocks.ENERGY_DUCT_SIGNALUM_EMPTY))
        .save(output, id("energy_duct_signalum_from_empty"));

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_RESONANT, 1)
        .requires(DDBlocks.ENERGY_DUCT_RESONANT_EMPTY)
        .requires(Items.REDSTONE_BLOCK)
        .unlockedBy("has_empty", has(DDBlocks.ENERGY_DUCT_RESONANT_EMPTY))
        .save(output, id("energy_duct_resonant_from_empty"));

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR, 1)
        .requires(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY)
        .requires(Items.BLUE_ICE)
        .unlockedBy("has_empty", has(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY))
        .save(output, id("energy_duct_superconductor_from_empty"));
  }

  private void buildFluidDuctRecipes(RecipeOutput output) {
    ddShaped(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_BASIC, 6)
        .pattern("IGI")
        .define('I', Items.COPPER_INGOT)
        .define('G', Items.GLASS)
        .unlockedBy("has_copper", has(Items.COPPER_INGOT))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_BASIC_OPAQUE, 6)
        .pattern("ILI")
        .define('I', Items.COPPER_INGOT)
        .define('L', DDTags.INGOTS_LEAD)
        .unlockedBy("has_copper", has(Items.COPPER_INGOT))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_HARDENED, 6)
        .pattern("IGI")
        .define('I', DDTags.INGOTS_INVAR)
        .define('G', DDTags.HARDENED_GLASS)
        .unlockedBy("has_invar", has(DDTags.INGOTS_INVAR))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_HARDENED_OPAQUE, 6)
        .pattern("ILI")
        .define('I', DDTags.INGOTS_INVAR)
        .define('L', DDTags.INGOTS_LEAD)
        .unlockedBy("has_invar", has(DDTags.INGOTS_INVAR))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_ENERGY, 1)
        .requires(DDBlocks.FLUID_DUCT_HARDENED)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .unlockedBy("has_hardened", has(DDBlocks.FLUID_DUCT_HARDENED))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.FLUID_DUCT_SUPER, 6)
        .pattern("IGI")
        .pattern("GEG")
        .pattern("IGI")
        .define('I', DDTags.INGOTS_BRONZE)
        .define('G', DDTags.HARDENED_GLASS)
        .define('E', DDBlocks.FLUID_DUCT_HARDENED)
        .unlockedBy("has_hardened", has(DDBlocks.FLUID_DUCT_HARDENED))
        .save(output);

    opaqueConversion(output, DDBlocks.FLUID_DUCT_BASIC, DDBlocks.FLUID_DUCT_BASIC_OPAQUE);
    opaqueConversion(output, DDBlocks.FLUID_DUCT_HARDENED, DDBlocks.FLUID_DUCT_HARDENED_OPAQUE);
    opaqueConversion(output, DDBlocks.FLUID_DUCT_ENERGY, DDBlocks.FLUID_DUCT_ENERGY_OPAQUE);
    opaqueConversion(output, DDBlocks.FLUID_DUCT_SUPER, DDBlocks.FLUID_DUCT_SUPER_OPAQUE);
  }

  private void buildItemDuctRecipes(RecipeOutput output) {
    ddShaped(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_BASIC, 6)
        .pattern("IGI")
        .define('I', DDTags.INGOTS_TIN)
        .define('G', DDTags.HARDENED_GLASS)
        .unlockedBy("has_tin", has(DDTags.INGOTS_TIN))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_BASIC_OPAQUE, 6)
        .pattern("ILI")
        .define('I', DDTags.INGOTS_TIN)
        .define('L', DDTags.INGOTS_LEAD)
        .unlockedBy("has_tin", has(DDTags.INGOTS_TIN))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_DENSE, 1)
        .requires(DDBlocks.ITEM_DUCT_BASIC)
        .requires(DDTags.NUGGETS_LEAD)
        .requires(DDTags.NUGGETS_LEAD)
        .requires(DDTags.NUGGETS_LEAD)
        .unlockedBy("has_basic", has(DDBlocks.ITEM_DUCT_BASIC))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_VACUUM, 1)
        .requires(DDBlocks.ITEM_DUCT_BASIC)
        .requires(DDTags.NUGGETS_SILVER)
        .requires(DDTags.NUGGETS_SILVER)
        .requires(DDTags.NUGGETS_SILVER)
        .unlockedBy("has_basic", has(DDBlocks.ITEM_DUCT_BASIC))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_FAST, 1)
        .requires(DDBlocks.ITEM_DUCT_BASIC)
        .requires(Items.GLOWSTONE)
        .unlockedBy("has_basic", has(DDBlocks.ITEM_DUCT_BASIC))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_ENERGY, 1)
        .requires(DDBlocks.ITEM_DUCT_BASIC)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_SIGNALUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .requires(DDTags.NUGGETS_ELECTRUM)
        .unlockedBy("has_basic", has(DDBlocks.ITEM_DUCT_BASIC))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.ITEM_DUCT_ENERGY_FAST, 1)
        .requires(DDBlocks.ITEM_DUCT_ENERGY)
        .requires(Items.GLOWSTONE)
        .unlockedBy("has_energy", has(DDBlocks.ITEM_DUCT_ENERGY))
        .save(output);

    opaqueConversion(output, DDBlocks.ITEM_DUCT_BASIC, DDBlocks.ITEM_DUCT_BASIC_OPAQUE);
    opaqueConversion(output, DDBlocks.ITEM_DUCT_DENSE, DDBlocks.ITEM_DUCT_DENSE_OPAQUE);
    opaqueConversion(output, DDBlocks.ITEM_DUCT_VACUUM, DDBlocks.ITEM_DUCT_VACUUM_OPAQUE);
    opaqueConversion(output, DDBlocks.ITEM_DUCT_FAST, DDBlocks.ITEM_DUCT_FAST_OPAQUE);
    opaqueConversion(output, DDBlocks.ITEM_DUCT_ENERGY, DDBlocks.ITEM_DUCT_ENERGY_OPAQUE);
    opaqueConversion(output, DDBlocks.ITEM_DUCT_ENERGY_FAST, DDBlocks.ITEM_DUCT_ENERGY_FAST_OPAQUE);
  }

  private void buildStructuralRecipes(RecipeOutput output) {
    ddShaped(RecipeCategory.REDSTONE, DDBlocks.STRUCTURAL_DUCT, 6)
        .pattern("iIi")
        .define('i', Items.IRON_NUGGET)
        .define('I', DDTags.INGOTS_LEAD)
        .unlockedBy("has_lead", has(DDTags.INGOTS_LEAD))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.LUX_DUCT, 1)
        .requires(DDBlocks.STRUCTURAL_DUCT)
        .requires(DDTags.INGOTS_LUMIUM)
        .unlockedBy("has_lumium", has(DDTags.INGOTS_LUMIUM))
        .save(output);
  }

  private void buildTransportRecipes(RecipeOutput output) {
    ddShaped(RecipeCategory.REDSTONE, DDBlocks.TRANSPORT_DUCT_FRAME, 4)
        .pattern("IGI")
        .pattern("G G")
        .pattern("IGI")
        .define('I', DDTags.INGOTS_BRONZE)
        .define('G', DDTags.HARDENED_GLASS)
        .unlockedBy("has_bronze", has(DDTags.INGOTS_BRONZE))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.TRANSPORT_DUCT_BASIC, 1)
        .requires(DDBlocks.TRANSPORT_DUCT_FRAME)
        .requires(Items.PHANTOM_MEMBRANE)
        .unlockedBy("has_frame", has(DDBlocks.TRANSPORT_DUCT_FRAME))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, DDBlocks.TRANSPORT_DUCT_LONG_RANGE, 8)
        .pattern("IGI")
        .pattern("G G")
        .pattern("IGI")
        .define('I', DDTags.INGOTS_LEAD)
        .define('G', DDTags.HARDENED_GLASS)
        .unlockedBy("has_lead", has(DDTags.INGOTS_LEAD))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, DDBlocks.TRANSPORT_DUCT_LINKING, 1)
        .requires(DDBlocks.TRANSPORT_DUCT_BASIC)
        .requires(Items.ENDER_EYE)
        .unlockedBy("has_transport", has(DDBlocks.TRANSPORT_DUCT_BASIC))
        .save(output);
  }

  private void buildAttachmentRecipes(RecipeOutput output) {
    buildTieredAttachment(
        output,
        DDItems.SERVO_BASIC,
        DDItems.SERVO_HARDENED,
        DDItems.SERVO_REINFORCED,
        DDItems.SERVO_SIGNALUM,
        DDItems.SERVO_RESONANT,
        Items.REDSTONE);

    buildTieredAttachment(
        output,
        DDItems.FILTER_BASIC,
        DDItems.FILTER_HARDENED,
        DDItems.FILTER_REINFORCED,
        DDItems.FILTER_SIGNALUM,
        DDItems.FILTER_RESONANT,
        Items.PAPER);

    buildTieredAttachment(
        output,
        DDItems.RETRIEVER_BASIC,
        DDItems.RETRIEVER_HARDENED,
        DDItems.RETRIEVER_REINFORCED,
        DDItems.RETRIEVER_SIGNALUM,
        DDItems.RETRIEVER_RESONANT,
        Items.ENDER_EYE);

    ddShaped(RecipeCategory.REDSTONE, DDItems.RELAY, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', DDTags.NUGGETS_SIGNALUM)
        .define('G', Items.QUARTZ)
        .define('I', DDTags.INGOTS_LEAD)
        .define('R', Items.REDSTONE)
        .unlockedBy("has_lead", has(DDTags.INGOTS_LEAD))
        .save(output);

    ddShaped(RecipeCategory.TOOLS, DDItems.WRENCH, 1)
        .pattern("I I")
        .pattern(" R ")
        .pattern(" I ")
        .define('I', Items.IRON_INGOT)
        .define('R', Items.IRON_NUGGET)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output);
  }

  private void buildTieredAttachment(
      RecipeOutput output,
      ItemLike basic,
      ItemLike hardened,
      ItemLike reinforced,
      ItemLike signalum,
      ItemLike resonant,
      ItemLike middleItem) {
    ddShaped(RecipeCategory.REDSTONE, basic, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', Items.IRON_NUGGET)
        .define('G', Items.GLASS)
        .define('I', Items.IRON_INGOT)
        .define('R', middleItem)
        .unlockedBy("has_iron", has(Items.IRON_INGOT))
        .save(output);

    ddShaped(RecipeCategory.REDSTONE, hardened, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', Items.IRON_NUGGET)
        .define('G', Items.GLASS)
        .define('I', DDTags.INGOTS_INVAR)
        .define('R', middleItem)
        .unlockedBy("has_invar", has(DDTags.INGOTS_INVAR))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, hardened, 1)
        .requires(basic)
        .requires(DDTags.INGOTS_INVAR)
        .unlockedBy("has_basic", has(basic))
        .save(output, key(hardened) + "_upgrade");

    ddShaped(RecipeCategory.REDSTONE, reinforced, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', Items.IRON_NUGGET)
        .define('G', Items.GLASS)
        .define('I', DDTags.INGOTS_ELECTRUM)
        .define('R', middleItem)
        .unlockedBy("has_electrum", has(DDTags.INGOTS_ELECTRUM))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, reinforced, 1)
        .requires(hardened)
        .requires(DDTags.INGOTS_ELECTRUM)
        .unlockedBy("has_hardened", has(hardened))
        .save(output, key(reinforced) + "_upgrade");

    ddShaped(RecipeCategory.REDSTONE, signalum, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', Items.IRON_NUGGET)
        .define('G', Items.GLASS)
        .define('I', DDTags.INGOTS_SIGNALUM)
        .define('R', middleItem)
        .unlockedBy("has_signalum", has(DDTags.INGOTS_SIGNALUM))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, signalum, 1)
        .requires(reinforced)
        .requires(DDTags.INGOTS_SIGNALUM)
        .unlockedBy("has_reinforced", has(reinforced))
        .save(output, key(signalum) + "_upgrade");

    ddShaped(RecipeCategory.REDSTONE, resonant, 2)
        .pattern("iGi")
        .pattern("IRI")
        .define('i', Items.IRON_NUGGET)
        .define('G', Items.GLASS)
        .define('I', DDTags.INGOTS_ENDERIUM)
        .define('R', middleItem)
        .unlockedBy("has_enderium", has(DDTags.INGOTS_ENDERIUM))
        .save(output);

    ddShapeless(RecipeCategory.REDSTONE, resonant, 1)
        .requires(signalum)
        .requires(DDTags.INGOTS_ENDERIUM)
        .unlockedBy("has_signalum", has(signalum))
        .save(output, key(resonant) + "_upgrade");
  }

  private void opaqueConversion(RecipeOutput output, ItemLike transparent, ItemLike opaque) {
    ddShapeless(RecipeCategory.REDSTONE, opaque, 1)
        .requires(transparent)
        .requires(DDTags.INGOTS_LEAD)
        .unlockedBy("has_transparent", has(transparent))
        .save(output, key(opaque) + "_from_transparent");
    ddShapeless(RecipeCategory.REDSTONE, transparent, 1)
        .requires(opaque)
        .requires(DDTags.HARDENED_GLASS)
        .unlockedBy("has_opaque", has(opaque))
        .save(output, key(transparent) + "_from_opaque");
  }

  private void ingotNugget(RecipeOutput output, ItemLike ingot, ItemLike nugget, String name) {
    ddShaped(RecipeCategory.MISC, ingot, 1)
        .pattern("NNN")
        .pattern("NNN")
        .pattern("NNN")
        .define('N', nugget)
        .unlockedBy("has_nugget", has(nugget))
        .save(output, id(name + "_ingot_from_nuggets"));
    ddShapeless(RecipeCategory.MISC, nugget, 9)
        .requires(ingot)
        .unlockedBy("has_ingot", has(ingot))
        .save(output, id(name + "_nugget_from_ingot"));
  }

  private String key(ItemLike item) {
    return DynamiDucts.MODID
        + ":"
        + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
  }

  private String id(String name) {
    return DynamiDucts.MODID + ":" + name;
  }
}
