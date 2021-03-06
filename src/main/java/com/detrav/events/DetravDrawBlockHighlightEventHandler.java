package com.detrav.events;

import com.detrav.utils.DetravConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
//import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * Created by wital_000 on 18.04.2016.
 */
public class DetravDrawBlockHighlightEventHandler {

    public static long modeBlockBreak = 0L;
    public static boolean disableDepthBuffer = false;
    public static float thickness = 4F;
    public static float offset = 0.01F;
    public static float red = 1F;//0.1F;
    public static float green = 1F;
    public static float blue = 1F;//0.1F;
    public static float alpha = 0.1F;

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
        if (drawMoreSelectionBox(e.player, e.target, 0, e.currentItem, e.partialTicks))
            e.setCanceled(true);
    }

    public static boolean drawMoreSelectionBox(EntityPlayer player, MovingObjectPosition mouseHit, int par3, ItemStack par4ItemStack, float par5) {
        int rad = DetravConfig.DIGGING_RADIUS;
        switch ((int) modeBlockBreak) {
            //case 0: Просто рисуем без экспанда
            case 0:
                //drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX , mouseHit.blockY, mouseHit.blockZ , par3, par4ItemStack, par5);
                return false;
            case 1:
                switch (mouseHit.sideHit) {//Рисуеи по моусхиту
                    case 0:
                    case 1://x,z
                        for (int i = -rad; i <= rad; i++)
                            for (int j = -rad; j <= rad; j++)
                                drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX + i, mouseHit.blockY, mouseHit.blockZ + j, par3, par4ItemStack, par5);
                        break;
                    case 2:
                    case 3://x,y
                        for (int i = -rad; i <= rad; i++)
                            for (int j = -rad; j <= rad; j++)
                                drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX + i, mouseHit.blockY + j, mouseHit.blockZ, par3, par4ItemStack, par5);
                        break;
                    case 4:
                    case 5://y,z
                        for (int i = -rad; i <= rad; i++)
                            for (int j = -rad; j <= rad; j++)
                                drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX, mouseHit.blockY + i, mouseHit.blockZ + j, par3, par4ItemStack, par5);
                        break;
                }
                break;
            case 2://x,z
                for (int i = -rad; i <= rad; i++)
                    for (int j = -rad; j <= rad; j++)
                        drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX + i, mouseHit.blockY, mouseHit.blockZ + j, par3, par4ItemStack, par5);
                break;
            case 3:
                float rotationYaw = player.rotationYaw;
                while (rotationYaw > 0) rotationYaw -= 360F;
                while (rotationYaw < -360) rotationYaw += 360F;
                if ((-135 <= rotationYaw && rotationYaw <= -45) || (-315 <= rotationYaw && rotationYaw <= -225)) {
                    //y,z
                    for (int i = -rad; i <= rad; i++)
                        for (int j = -rad; j <= rad; j++)
                            drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX, mouseHit.blockY + i, mouseHit.blockZ + j, par3, par4ItemStack, par5);
                } else if ((-225 <= rotationYaw && rotationYaw <= -135) || -45 <= rotationYaw || rotationYaw <= -315) {
                    //x,y
                    for (int i = -rad; i <= rad; i++)
                        for (int j = -rad; j <= rad; j++)
                            drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX + i, mouseHit.blockY + j, mouseHit.blockZ, par3, par4ItemStack, par5);
                }
                break;
            case 4:
                //x,y,z
                for (int i = -rad; i <= rad; i++)
                    for (int j = -rad; j <= rad; j++)
                        for (int k = -rad; k <= rad; k++)
                            drawSelectionBox(player, mouseHit.typeOfHit, mouseHit.blockX + i, mouseHit.blockY + j, mouseHit.blockZ + k, par3, par4ItemStack, par5);
                break;
        }
        return false;
    }

    public static void drawSelectionBox(EntityPlayer player,MovingObjectPosition.MovingObjectType typeOfHit, int blockX,int blockY, int blockZ, int par3, ItemStack par4ItemStack, float par5) {
        if ((par3 == 0) && (typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)) {
            //float breakProgress = getBlockDamage(player, block);
            if (disableDepthBuffer) {
                GL11.glDisable(2929);
            }
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(thickness);
            GL11.glDisable(3553);
            GL11.glDepthMask(false);
            float f1 = offset;

            Minecraft mc = Minecraft.getMinecraft();
            Block b = mc.theWorld.getBlock(blockX, blockY, blockZ);
            if (b != Blocks.air && b!= null) {
                b.setBlockBoundsBasedOnState(mc.theWorld, blockX, blockY, blockZ);

                double xOffset = player.lastTickPosX + (player.posX - player.lastTickPosX) * par5;
                double yOffset = player.lastTickPosY + (player.posY - player.lastTickPosY) * par5;
                double zOffset = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * par5;

                float xExpand = 0F;
                float yExpand = 0F;
                float zExpand = 0F;



                try {
                    AxisAlignedBB bb = b.getSelectedBoundingBoxFromPool(mc.theWorld, blockX, blockY, blockZ).expand(xExpand + f1, yExpand + f1, zExpand + f1).getOffsetBoundingBox(-xOffset, -yOffset, -zOffset);
                    GL11.glColor4f(red, green, blue, alpha);
                    //drawOutlinedBoundingBox(bb);
                    drawBlock(bb);
                }catch(Exception e) {

                }

            }
            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            if (disableDepthBuffer) {
                GL11.glEnable(2929);
            }
        }
    }

    private static void drawBlock(AxisAlignedBB par1AxisAlignedBB) {
        //Tessellator tessellator = Tessellator.instance;
        if(alpha > 0.0F) {


            renderDown(par1AxisAlignedBB);
            renderUp(par1AxisAlignedBB);
            renderNorth(par1AxisAlignedBB);
            renderSouth(par1AxisAlignedBB);
            renderWest(par1AxisAlignedBB);
            renderEast(par1AxisAlignedBB);
        }

    }


    public static void renderUp(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }

    public static void renderDown(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }

    public static void renderNorth(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }

    public static void renderSouth(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
    }

    public static void renderWest(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }

    public static void renderEast(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }

    private static void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(1);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
    }


    static boolean inited = false;

    public static void register() {
        if (!inited) {
            inited = true;
            if (DetravConfig.DIGGING_HIGHLIGHT) {
                DetravDrawBlockHighlightEventHandler handler = new DetravDrawBlockHighlightEventHandler();
                MinecraftForge.EVENT_BUS.register(handler);
                FMLCommonHandler.instance().bus().register(handler);
            }
        }
    }
}