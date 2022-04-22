package derc.addon.chillintools;

import derc.addon.chillintools.commands.ExampleCommand;
import derc.addon.chillintools.modules.AnotherExample;
import derc.addon.chillintools.modules.Example;
import derc.addon.chillintools.modules.hud.HudExample;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class TemplateAddon extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(TemplateAddon.class);
	public static final Category CATEGORY = new Category("Example");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon Template");

		// Required when using @EventHandler
		MeteorClient.EVENT_BUS.registerLambdaFactory("meteordevelopment.addons.template", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

		// Modules
		Modules.get().add(new Example());
		Modules.get().add(new AnotherExample());

		// Commands
		Commands.get().add(new ExampleCommand());

		// HUD
        HUD.get().elements.add(new HudExample());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
