package com.example.examplemod;

import com.example.examplemod.commands.BlockCommand;
import com.example.examplemod.commands.ModGuiCommand;
import com.example.examplemod.gui.CustomConfigGui;
import com.example.examplemod.helper.KeyHelper;
import com.example.examplemod.shader.backup.backup2.BlurRenderer;
import com.example.examplemod.shader.trash.BlurHandler;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    public static BlurRenderer blurRenderer;
    public static int DOWNSCALE = 2;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
        blurRenderer = new BlurRenderer();
        blurRenderer.initShaders();

        MinecraftForge.EVENT_BUS.register(blurRenderer);
        MinecraftForge.EVENT_BUS.register(new BlurHandler());
        MinecraftForge.EVENT_BUS.register(new KeyHelper());
        MinecraftForge.EVENT_BUS.register(new CustomConfigGui(null));

        ClientCommandHandler.instance.registerCommand(new BlockCommand());
        ClientCommandHandler.instance.registerCommand(new ModGuiCommand());
    }
}
