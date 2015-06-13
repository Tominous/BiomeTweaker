package me.superckl.biometweaker.script;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import me.superckl.biometweaker.script.command.IScriptCommand;
import me.superckl.biometweaker.util.LogHelper;

import com.google.common.collect.Maps;

@Getter
public class ScriptCommandManager {

	public static enum ApplicationStage{
		PRE_INIT, INIT, POST_INIT, FINISHED_LOAD, SERVER_STARTING, SERVER_STARTED;
	}

	@Setter
	private ApplicationStage currentStage = ApplicationStage.FINISHED_LOAD;

	private final Map<ApplicationStage, List<IScriptCommand>> commands = Maps.newEnumMap(ApplicationStage.class);
	private final Set<ApplicationStage> appliedStages = EnumSet.noneOf(ApplicationStage.class);

	public boolean addCommand(final IScriptCommand command){
		if(!this.commands.containsKey(this.currentStage))
			this.commands.put(this.currentStage, new ArrayList<IScriptCommand>());
		if(this.appliedStages.contains(this.currentStage))
			try {
				command.perform();
			} catch (final Exception e) {
				LogHelper.error("Failed to execute script command: "+command);
				e.printStackTrace();
			}
		return this.commands.get(this.currentStage).add(command);
	}

	public void applyCommandsFor(final ApplicationStage stage){
		if(!this.commands.containsKey(stage))
			return;
		this.appliedStages.add(stage);
		final List<IScriptCommand> commands = this.commands.get(stage);
		LogHelper.info("Found "+commands.size()+" tweak"+(commands.size() > 1 ? "s":"")+" to apply for stage "+stage.toString()+". Applying...");
		for(final IScriptCommand command:commands)
			try{
				command.perform();
			}catch(final Exception e){
				LogHelper.error("Failed to execute script command: "+command);
				e.printStackTrace();
			}
	}

	public void reset(){
		this.commands.clear();
		this.currentStage = ApplicationStage.FINISHED_LOAD;
	}

}
