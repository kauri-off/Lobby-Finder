package com.kauri.mixgame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

@Mod(modid = MixGame.MODID, name = MixGame.NAME, version = MixGame.VERSION)
public class MixGame
{
    public static final String MODID = "mixgame";
    public static final String NAME = "MixGame Helper";
    public static final String VERSION = "1.3";

    private static Logger logger;
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private boolean flag = false;
    private KeyBinding key = new KeyBinding(
            "Toggle helper",
            Keyboard.KEY_J,
            "Kauri mods"
    );
    private long clock = System.currentTimeMillis();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(key);
    }
    @SubscribeEvent
    public void onButton(InputEvent.KeyInputEvent event) {
        if (!Keyboard.isKeyDown(key.getKeyCode())) return;

        flag = !flag;
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!flag) return;
        if (System.currentTimeMillis() - clock < 1000) return;

        if (getCurrentPlayers() > 1) {
            clock = System.currentTimeMillis();
            goNext();
        } else if (getCurrentPlayers() == -1) {

        } else {
            flag = false;
        }
    }
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            minecraft.fontRenderer.drawStringWithShadow(flag ? "Finding game..." : "Inactive", 10, 10, Color.WHITE.getRGB());
        }
    }
    private List<String> getScoreboard() {
        if (minecraft.world == null) return null;
        Scoreboard scoreboard = minecraft.world.getScoreboard();
        if (scoreboard == null) return null;
        List<String> sidebarScores = ScoreboardUtils.getSidebarScores(scoreboard);
        return sidebarScores;
    }
    private int getCurrentPlayers() {
        List<String> sc = getScoreboard();
        if (sc == null) return -1;
        if (sc.size() <= 12) {
            return -1;
        }
        String line = sc.get(12);

        int lastindex = 0;
        for (; lastindex < line.length(); lastindex++) {
            char c = line.charAt(lastindex);
            if (c == '§') {
                if (lastindex > 3) {
                    break;
                }
            }
        }
        try {
            return Integer.parseInt(sc.get(12).substring(3, lastindex));
        } catch (Exception e) {
            return -1;
        }
    }
    private void goNext() {
        minecraft.player.inventory.currentItem = 4;
        minecraft.playerController.processRightClick(minecraft.player, minecraft.world, EnumHand.MAIN_HAND);
    }
}
