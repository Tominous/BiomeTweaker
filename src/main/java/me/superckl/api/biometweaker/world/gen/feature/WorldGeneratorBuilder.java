package me.superckl.api.biometweaker.world.gen.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public abstract class WorldGeneratorBuilder<V extends WorldGeneratorWrapper<?>> {

	protected IBlockState mainBlock = Blocks.STONE.getDefaultState();
	protected int count = 10;

	public abstract V build();

	public IBlockState getMainBlock() {
		return this.mainBlock;
	}

	public void setMainBlock(final IBlockState mainBlock) {
		this.mainBlock = mainBlock;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

}
