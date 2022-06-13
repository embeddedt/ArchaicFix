package org.embeddedt.archaicfix.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.embeddedt.archaicfix.ArchaicFix;

import java.lang.ref.WeakReference;

public class PreventSPDeadlockThread extends Thread {
    private WeakReference<IntegratedServer> oldServer;
    public PreventSPDeadlockThread(IntegratedServer theServer) {
        oldServer = new WeakReference<>(theServer);
        setName("ArchaicFix deadlock detection thread");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000L);
        } catch(InterruptedException e) {
            return;
        }
        IntegratedServer server = oldServer.get();
        if(server == null)
            return;
        if(Minecraft.getMinecraft().thePlayer == null && server.getCurrentPlayerCount() == 0) {
            ArchaicFix.LOGGER.warn("Detected possible deadlock, stopping integrated server");
            Minecraft.getMinecraft().func_152343_a(() -> {
                Minecraft.stopIntegratedServer();
                Minecraft.getMinecraft().loadWorld(null);
                Minecraft.getMinecraft().displayGuiScreen(null);
                return null;
            });
        }
    }
}
