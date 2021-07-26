package bl4ckscor3.mod.globalxp.compat;

import java.util.List;

import bl4ckscor3.mod.globalxp.GlobalXP;
import bl4ckscor3.mod.globalxp.xpblock.XPBlock;
import bl4ckscor3.mod.globalxp.xpblock.XPBlockTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@WailaPlugin(GlobalXP.MOD_ID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider
{
	public static final ResourceLocation XP_BLOCK = new ResourceLocation(GlobalXP.MOD_ID, "xp_block");
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();

	@Override
	public void register(IRegistrar registrar)
	{
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, XPBlock.class);
		registrar.registerStackProvider(INSTANCE, XPBlock.class);
	}

	@Override
	public void appendBody(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config)
	{
		if(accessor.getTileEntity() instanceof XPBlockTileEntity)
		{
			XPBlockTileEntity te = ((XPBlockTileEntity)accessor.getTileEntity());

			tooltip.add(new TranslatableComponent("info.globalxp.levels", String.format("%.2f", te.getStoredLevels())));

			if(accessor.getPlayer().isCrouching())
				tooltip.add(new TranslatableComponent("info.globalxp.xp", te.getStoredXP()));
		}
	}

	@Override
	public ItemStack getStack(IDataAccessor accessor, IPluginConfig config)
	{
		return new ItemStack(GlobalXP.xp_block);
	}
}
