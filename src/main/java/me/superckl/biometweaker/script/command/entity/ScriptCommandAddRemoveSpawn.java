package me.superckl.biometweaker.script.command.entity;

import java.util.Collection;
import java.util.Iterator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.superckl.api.biometweaker.event.BiomeTweakEvent;
import me.superckl.api.biometweaker.script.AutoRegister;
import me.superckl.api.biometweaker.script.AutoRegister.ParameterOverride;
import me.superckl.api.biometweaker.script.pack.BiomePackage;
import me.superckl.api.superscript.script.command.ScriptCommand;
import me.superckl.api.superscript.util.WarningHelper;
import me.superckl.biometweaker.BiomeTweaker;
import me.superckl.biometweaker.script.object.BiomesScriptObject;
import me.superckl.biometweaker.script.object.TweakerScriptObject;
import me.superckl.biometweaker.util.EntityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.MinecraftForge;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScriptCommandAddRemoveSpawn extends ScriptCommand{

	private final BiomePackage pack;
	private final boolean remove;
	private final EnumCreatureType type;
	private final String entityClass;
	private final int weight, minCount, maxCount;

	@AutoRegister(classes = {BiomesScriptObject.class, TweakerScriptObject.class}, name = "addSpawn")
	@ParameterOverride(exceptionKey = "nonNegInt", parameterIndex = 3)
	@ParameterOverride(exceptionKey = "nonNegInt", parameterIndex = 4)
	@ParameterOverride(exceptionKey = "nonNegInt", parameterIndex = 5)
	public ScriptCommandAddRemoveSpawn(final BiomePackage pack, final String entityClass, final EnumCreatureType type, final int weight, final int minCount, final int maxCount) {
		this(pack, false, type, entityClass, weight, minCount, maxCount);
	}

	@AutoRegister(classes = {BiomesScriptObject.class, TweakerScriptObject.class}, name = "removeSpawn")
	public ScriptCommandAddRemoveSpawn(final BiomePackage pack, final String entityClass, final EnumCreatureType type) {
		this(pack, true, type, entityClass, 0, 0, 0);
	}

	@Override
	public void perform() throws Exception {
		final Class<? extends Entity> clazz2 = EntityHelper.getEntityClass(this.entityClass);
		if(clazz2 == null)
			throw new IllegalArgumentException("Failed to find entity class: "+this.entityClass);
		if(!EntityLiving.class.isAssignableFrom(clazz2))
			throw new IllegalArgumentException("entity class "+this.entityClass+" is not assignable to EntityLiving. It cannot be spawned!");
		final Class<? extends EntityLiving> clazz = WarningHelper.uncheckedCast(clazz2);
		final SpawnListEntry entry = new SpawnListEntry(clazz, this.weight, this.minCount, this.maxCount);
		final Iterator<Biome> it = this.pack.getIterator();
		while(it.hasNext()){
			final Biome biome = it.next();
			if(this.remove && MinecraftForge.EVENT_BUS.post(new BiomeTweakEvent.RemoveSpawn(this, biome, this.type, clazz)))
				continue;
			else if(!this.remove && MinecraftForge.EVENT_BUS.post(new BiomeTweakEvent.AddSpawn(this, biome, entry)))
				continue;
			if(this.remove)
				this.removeEntry(clazz, biome.getSpawnableList(this.type));
			else
				biome.getSpawnableList(this.type).add(new Biome.SpawnListEntry(clazz, this.weight, this.minCount, this.maxCount));
			BiomeTweaker.getInstance().onTweak(Biome.getIdForBiome(biome));
		}
	}

	private void removeEntry(final Class<?> clazz, final Collection<SpawnListEntry> coll){
		final Iterator<SpawnListEntry> it = coll.iterator();
		while(it.hasNext())
			if(it.next().entityClass.getName().equals(clazz.getName()))
				it.remove();
	}

}
