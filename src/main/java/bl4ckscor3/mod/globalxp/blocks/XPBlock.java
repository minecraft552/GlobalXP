package bl4ckscor3.mod.globalxp.blocks;

import bl4ckscor3.mod.globalxp.GlobalXP;
import bl4ckscor3.mod.globalxp.imc.top.ITOPInfoProvider;
import bl4ckscor3.mod.globalxp.network.packets.UpdateXPBlock;
import bl4ckscor3.mod.globalxp.tileentity.TileEntityXPBlock;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;

public class XPBlock extends Block implements ITOPInfoProvider
{
	public XPBlock()
	{
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(12.5F, 2000.0F).sound(SoundType.METAL));

		setRegistryName(GlobalXP.MOD_ID + ":xp_block");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(world.isRemote || !stack.hasTag())
			return;

		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityXPBlock)
		{
			CompoundNBT tag = stack.getTag().getCompound("BlockEntityTag");

			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
			((TileEntityXPBlock)te).read(tag);
			((TileEntityXPBlock)te).markDirty();
			GlobalXP.channel.send(PacketDistributor.NEAR.with(() -> new TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 64, world.getDimension().getType())), new UpdateXPBlock((TileEntityXPBlock)te));
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		TileEntity te = world.getTileEntity(pos);

		if(world.getTileEntity(pos) instanceof TileEntityXPBlock)
			((TileEntityXPBlock)te).setDestroyedByCreativePlayer(player.isCreative());
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityXPBlock)
		{
			ItemStack stack = new ItemStack(asItem());

			if(((TileEntityXPBlock)te).getStoredLevels() != 0)
			{
				CompoundNBT stackTag = new CompoundNBT();

				stackTag.put("BlockEntityTag", ((TileEntityXPBlock)te).write(new CompoundNBT()));
				stack.setTag(stackTag);
				spawnAsEntity(world, pos, stack);
			}
			else if(!((TileEntityXPBlock)te).isDestroyedByCreativePlayer())
				spawnAsEntity(world, pos, stack);
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new TileEntityXPBlock();
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
	{
		TileEntity te = world.getTileEntity(data.getPos());

		if(te instanceof TileEntityXPBlock)
		{
			probeInfo.horizontal().text(I18n.format("info.globalxp.levels", String.format("%.2f", ((TileEntityXPBlock)te).getStoredLevels())));

			if(mode == ProbeMode.EXTENDED)
				probeInfo.horizontal().text(I18n.format("info.globalxp.xp", ((TileEntityXPBlock)te).getStoredXP()));
		}
	}
}
