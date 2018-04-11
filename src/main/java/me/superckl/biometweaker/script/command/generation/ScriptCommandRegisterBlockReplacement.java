package me.superckl.biometweaker.script.command.generation;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;
import me.superckl.api.biometweaker.block.BlockStateBuilder;
import me.superckl.api.biometweaker.event.BiomeTweakEvent;
import me.superckl.api.biometweaker.script.AutoRegister;
import me.superckl.api.biometweaker.script.pack.BiomePackage;
import me.superckl.api.superscript.script.command.ScriptCommand;
import me.superckl.biometweaker.BiomeTweaker;
import me.superckl.biometweaker.common.world.TweakWorldManager;
import me.superckl.biometweaker.common.world.gen.BlockReplacementManager;
import me.superckl.biometweaker.common.world.gen.ReplacementConstraints;
import me.superckl.biometweaker.script.object.BiomesScriptObject;
import me.superckl.biometweaker.script.object.TweakerScriptObject;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;

@AutoRegister(classes = {BiomesScriptObject.class, TweakerScriptObject.class}, name = "registerGenBlockRep")
@RequiredArgsConstructor
public class ScriptCommandRegisterBlockReplacement extends ScriptCommand{

	private final BiomePackage pack;
	private final int weight;
	private final BlockStateBuilder<?> toReplace;
	private final ReplacementConstraints replaceWith;

	public ScriptCommandRegisterBlockReplacement(final BiomePackage pack, final BlockStateBuilder<?> block1, final ReplacementConstraints block2) {
		this(pack, 1, block1, block2);
	}

	@Override
	public void perform() throws Exception {
		if(this.replaceWith == null || !this.replaceWith.hasBlock())
			throw new IllegalStateException("Cannot register block replacement with no block specified!");
		final Iterator<Biome> it = this.pack.getIterator();
		while(it.hasNext()){
			final Biome gen = it.next();
			if(MinecraftForge.EVENT_BUS.post(new BiomeTweakEvent.RegisterGenBlockReplacement(this, this.weight, gen, this.toReplace, this.replaceWith)))
				continue;
			if(TweakWorldManager.getCurrentWorld() == null)
				BlockReplacementManager.registerGlobalBlockReplacement(Biome.getIdForBiome(gen), this.weight, this.toReplace.build(), this.replaceWith);
			else
				BlockReplacementManager.getManagerForWorld(TweakWorldManager.getCurrentWorld()).registerBlockReplacement(Biome.getIdForBiome(gen),
						this.weight, this.toReplace.build(), this.replaceWith);
			BiomeTweaker.getInstance().onTweak(Biome.getIdForBiome(gen));
		}
	}

}
