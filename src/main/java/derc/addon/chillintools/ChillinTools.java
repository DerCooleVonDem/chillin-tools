package derc.addon.chillintools;

import derc.addon.chillintools.modules.BlockAim;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ChillinTools extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(ChillinTools.class);
	public static final Category CATEGORY = new Category("Chillin' Tools");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Chillin' Tools");

		// Required when using @EventHandler
		MeteorClient.EVENT_BUS.registerLambdaFactory("derc.addon.chillintools", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

		// Modules
        Modules.get().add(new BlockAim());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
