package bl4ckscor3.mod.globalxp;

import bl4ckscor3.mod.globalxp.blocks.XPBlock;
import bl4ckscor3.mod.globalxp.imc.top.GetTheOneProbe;
import bl4ckscor3.mod.globalxp.itemblocks.ItemBlockXPBlock;
import bl4ckscor3.mod.globalxp.network.packets.RequestXPBlockUpdate;
import bl4ckscor3.mod.globalxp.network.packets.UpdateXPBlock;
import bl4ckscor3.mod.globalxp.renderer.TileEntityXPBlockRenderer;
import bl4ckscor3.mod.globalxp.tileentity.TileEntityXPBlock;
import bl4ckscor3.mod.globalxp.util.XPUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
import openmods.utils.EnchantmentUtils;

@Mod(GlobalXP.MOD_ID)
@EventBusSubscriber(bus=Bus.MOD)
public class GlobalXP
{
	public static final String MOD_ID = "globalxp";
	public static final String PROTOCOL_VERSION = "1.0"; //for channel
	public static Block xp_block;
	@ObjectHolder(MOD_ID + ":xp_block")
	public static TileEntityType<TileEntityXPBlock> teTypeXpBlock;
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public GlobalXP()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CONFIG_SPEC);
		MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event)
	{
		int index = 0;

		channel.registerMessage(index++, RequestXPBlockUpdate.class, RequestXPBlockUpdate::encode, RequestXPBlockUpdate::decode, RequestXPBlockUpdate::onMessage);
		channel.registerMessage(index++, UpdateXPBlock.class, UpdateXPBlock::encode, UpdateXPBlock::decode, UpdateXPBlock::onMessage);
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityXPBlock.class, new TileEntityXPBlockRenderer());
	}

	@SubscribeEvent
	public static void onInterModEnqueue(InterModEnqueueEvent event)
	{
		if(ModList.get().isLoaded("theoneprobe"))
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
	}

	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(xp_block = new XPBlock());
	}

	@SubscribeEvent
	public static void onRegisterTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityXPBlock::new, xp_block).build(null).setRegistryName(new ResourceLocation(xp_block.getRegistryName().toString())));
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemBlockXPBlock(xp_block).setRegistryName(xp_block.getRegistryName()));
	}

	public void onRightClickBlock(RightClickBlock event)
	{
		if(!(event.getWorld().getBlockState(event.getPos()).getBlock() instanceof XPBlock) || event.getHand() != Hand.MAIN_HAND)
			return;

		if(!event.getWorld().isRemote)
		{
			PlayerEntity player = event.getEntityPlayer();

			if(player.isSneaking()) //sneaking = add all player xp to the block
			{
				int playerXP = EnchantmentUtils.getPlayerXP(player);

				((TileEntityXPBlock)event.getWorld().getTileEntity(event.getPos())).addXP(playerXP);
				EnchantmentUtils.addPlayerXP(player, -playerXP); // set player xp to 0
			}
			else //not sneaking = remove exactly enough xp from the block to get player to the next level
			{
				TileEntityXPBlock te = ((TileEntityXPBlock)event.getWorld().getTileEntity(event.getPos()));
				int neededXP = XPUtils.getXPToNextLevel(EnchantmentUtils.getPlayerXP(player));
				int availableXP = te.removeXP(neededXP);

				EnchantmentUtils.addPlayerXP(player, availableXP);
			}
		}
	}
}
