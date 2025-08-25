package com.example.examplemod;

import com.example.examplemod.API.SubscribeEventUtil;
import com.example.examplemod.commands.BlockCommand;
import com.example.examplemod.commands.ModGuiCommand;
import com.example.examplemod.gui.CustomConfigGui;
import com.example.examplemod.helper.DisplayUtils;
import com.example.examplemod.helper.KeyHelper;
import com.example.examplemod.shader.gaussianBlur.BlurRenderer;
import com.example.examplemod.shader.roundedRect.RoundedRectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    public static BlurRenderer blurRenderer;
    public static CustomConfigGui customConfigGui;
    public static int DOWNSCALE = 2;

    public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    @EventHandler
    public void pre(FMLPreInitializationEvent event)
    {
        DisplayUtils.update();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
        this.blurRenderer = new BlurRenderer();
        this.blurRenderer.initShaders();
        this.customConfigGui = new CustomConfigGui(null);

        MinecraftForge.EVENT_BUS.register(this.blurRenderer);
        MinecraftForge.EVENT_BUS.register(new KeyHelper());
        MinecraftForge.EVENT_BUS.register(this.customConfigGui);
        MinecraftForge.EVENT_BUS.register(new test());
        MinecraftForge.EVENT_BUS.register(new SubscribeEventUtil());

        ClientCommandHandler.instance.registerCommand(new BlockCommand());
        ClientCommandHandler.instance.registerCommand(new ModGuiCommand());
    }

    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
        this.customConfigGui.finalizeWidgetYPercent();
        //this.customConfigGui.updateWidgetYDisplay();
    }
}
