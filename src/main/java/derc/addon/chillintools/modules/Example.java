package derc.addon.chillintools.modules;

import derc.addon.chillintools.TemplateAddon;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Example extends Module {
    public Example() {
        super(TemplateAddon.CATEGORY, "example", "An example module in a custom category.");
    }
}
