package com.digitscodecompendium.terralib.client;

import com.digitscodecompendium.terralib.TerraLib;
import com.digitscodecompendium.terralib.client.gui.SharedHudPanel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

/** Client entry point for TerraLib's shared GUI layers. */
@Mod(value = TerraLib.MOD_ID, dist = Dist.CLIENT)
public final class TerraLibClient {
    private static final ResourceLocation SHARED_HUD =
            ResourceLocation.fromNamespaceAndPath(TerraLib.MOD_ID, "shared_hud_panel");

    public TerraLibClient(IEventBus modBus) {
        modBus.addListener(TerraLibClient::registerGuiLayers);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(SHARED_HUD, SharedHudPanel::render);
    }
}
