package bl4ckscor3.mod.globalxp.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityXPBlock extends TileEntity
{
	private int storedLevels = 0;
	
	/**
	 * Adds one level to this tile entity
	 */
	public void addLevel()
	{
		addLevel(1);
	}
	
	/**
	 * Adds levels to this tile entity
	 * @param lvl The amount of levels to add
	 */
	public void addLevel(int lvl)
	{
		storedLevels += lvl;
		markDirty();
	}
	
	public void removeLevel()
	{
		if(storedLevels - 1 < 0)
			return;
		
		storedLevels -= 1;
		markDirty();
	}
	
	/**
	 * Gets how many XP are stored in this tile entity
	 * @return The total amount of XP stored in this tile entity
	 */
	public int getStoredLevels()
	{
		return storedLevels;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("stored_levels", storedLevels);
		return super.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		storedLevels = tag.getInteger("stored_levels");
		super.readFromNBT(tag);
	}
}
