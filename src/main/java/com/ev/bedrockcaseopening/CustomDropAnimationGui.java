// File: CustomDropAnimationGui.java
package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.ev.bedrockcaseopening.DungeonDropData.CaseMaterial;
import com.ev.bedrockcaseopening.DungeonDropData.Floor;

import io.netty.util.internal.ThreadLocalRandom;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class CustomDropAnimationGui extends GuiScreen {
//===================================================
//					   VARIABLES
//===================================================
	
	//===================== DEFAULT =====================
	private Minecraft mc;
	
	//===================== SCREENSIZE ==================
    private int scaleFactor;
    private int screenWidth;
    private int screenHeight;
    private int lastScaleFactor = -1;
    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;
    private float lastSpacing = -1f;
    
    //===================== RESOURCES ===================
    private ResourceLocation alphaFadeSidePNG = new ResourceLocation("bedrockcaseopening", "textures/gui/fade_side.png");
    private ResourceLocation audio = new ResourceLocation("gui.button.press");
    private FloatFontRenderer floatFont;
    
    Framebuffer frameBufferLayer1, frameBufferLayer2;
    Framebuffer guiBuffer;
    private ShaderGroup blurShader;
    
    //===================== FRAME TIMER =================
    private long lastFrameTime = 0L;
    private long guiOpenStartTime = -1;
    
    //===================== CAROUSEL LOGICS =============   
    private final List<DungeonDropData.Rule> carouselItems = new ArrayList<>();
    //List of item in the carousel, if everything corrects, the default will always be 50 for every animation.

    private DungeonDropData.Rule rewardItem;
    //The highest tiered item, currently in the Chest.
    
    private Floor floor;
    private CaseMaterial material;
    //floor and case type
    
    private final int itemCount = 50;
    //Amount of item in the carousel. DO NOT CHANGE
    
    private final int rewardSlot = 44;
    //The position of the reward item in the carousel. DO NOT CHANGE 
    
    private float currentScrollSpeed;
    //The current scroll speed of the carousel.

    private float slowTime = MyConfig.slowTime; 
    //Can config ingame. The ammount of time to slow down to speed 0, from the "Slow mark", to the reward position.
    
    private int slowDistance = MyConfig.slowDistance; 
    //Can config ingame. The distance from reward position, to the "Slow mark", which means, configure this to decided when the carousel slows down.
    
    private int lastBoxDistance = 0;
    //Distance between the start of the n box with the n+1 box.
    
    private float offsetX = 0f;
    //Offset of the carousels. Used to make scrolling animation from left to right by increasing this variable overtime.
    
    private final long animationDuration = 300_000_000L; // 300ms
    // Duration of a quick scale animation when opening the chest. 

    private float itemBoxWidth, itemBoxHeight, itemBoxPadding, spacing;
    // Item box size, padding between boxes, and spacing.
    
    private float centerX, centerY;
    // Center point of the screen.
    
    private float stopPoint, slowPoint;
    // Slow,stop point uses  slowTime and slowDistance to calculate speed of carousel.
    
    private float randslow;
    private float randstop;
    // Add unpredicted randomizer.
    
    private boolean hasShownResult = false;
    //When animation finishes, this will turn to true, just for debug.
    
//===================================================
//	   				FUNCTIONS
//===================================================    
    public CustomDropAnimationGui(DungeonDropData.Rule rewardItem, Floor floor, CaseMaterial material) {
    	//First initialize of the CustomAnimationGUI class
    	// - get the reward item into the 44th slot.
    	// - the rest of the item is randomized.
    	// - create small random offset of stop point and slow point for the unpredictable.
    	
    	if(MyConfig.debugMode) System.out.println("Custom Animation UI Initialize");
    	mc = Minecraft.getMinecraft();
    	floatFont = new FloatFontRenderer(mc.fontRendererObj);
    	this.rewardItem = rewardItem;
    	this.floor = floor;
    	this.material = material;
    	rewardRandomizer(rewardItem);
    	
    	mc.getSoundHandler().playSound(PositionedSoundRecord.create(audio, 1.0F));
    	
        randstop = (float)randStopPoint();
        randslow = (float)ThreadLocalRandom.current().nextDouble(-2, 2);
        
        this.lastFrameTime = System.nanoTime();
        this.guiOpenStartTime = System.nanoTime();
    }

    
    private void rewardRandomizer(DungeonDropData.Rule rewardItem) {
    	//Used to randomize all the other item slots except for the reward slots
    	// - Used base weight so that ultra rare item dont show up frequently.
    	// Note : I used my own algorithm to calculate position of rare items, its hard to explain, you can use chatGPT to explain the code if u want to understand.
    	
    	//Create buckets of rarity item, to keep track of items while randomized, so that it doenst duplicate once it already rolled.
    	
        List<List<DungeonDropData.Rule>> rarityBuckets = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            rarityBuckets.add(new ArrayList<>());
        }
    	
        List<DungeonDropData.Rule> allDrops = DungeonDropData.getDrops(material, floor);
        for (DungeonDropData.Rule rule : allDrops) {
            int rarityIndex = rule.rarity - 1; 
            rarityBuckets.get(rarityIndex).add(rule);
        }
    	
    	//Print debug
    	for(int i = 0; i < rarityBuckets.size(); i++) {
    		if(MyConfig.debugMode) System.out.println(rarityBuckets.get(i).size());
    	}
    	
    	// rollsize is basically a weight calculation for all the items that rolled. (weight / rollsize)
    	// Ex: Rarity 0 have weight = 2, roll priority 0 (highest). So that the chance that drop is 2/1000.
    	//
    	//Amount to keep track of the amount of item that has rolled.
    	//checkRepeat used to allowed 1 item only rolled for 3 highest rarity (0, 1, 2)
    	int rollsize = 1000;
    	List<Integer> weight = new ArrayList<>();
    	List<Integer> ammount = new ArrayList<>();
    	List<Boolean> checkRepeat = new ArrayList<>();
    	weight.add(2);
    	weight.add(4);
    	weight.add(10);
    	weight.add(20);
    	weight.add(100);
    	weight.add(100);
    	checkRepeat.add(false);
    	checkRepeat.add(false);
    	checkRepeat.add(false);
    	
    	//Check if reward item is in the checkRepeat list.
    	if(rewardItem.rarity <= 3) {
    		checkRepeat.set(rewardItem.rarity - 1, true);       	
    	}
    	
    	//Rolling algorithm.
    	//Go from highest rarity to lowest rarity
    	// - Before roll check for the checkRepeat list.
    	// - Each rarity got itemCount-1 amount of rolls.
    	int rolls = itemCount - 1; 
    	for(int i = 0; i < 6; i++) {
    		if (rarityBuckets.get(i).isEmpty()) {
    		    ammount.add(0);
    		    continue;
    		}
    		
    		int count = 0;
    		
    		if(i < 3) {
    			if(checkRepeat.get(i)) {
					ammount.add(count);			
					continue;
				}
    		}
    		
    		//Low one will count
    		for(int j = 0; j < rolls; j++) {
    			if (Math.random() < (float)(weight.get(i))/rollsize) {
    				count++;
    				if (i < 3) break;
    				if (count == rarityBuckets.get(i).size()) break; // cap the rolls in case ur too lucky, not repeating the drops
    			}
    		}
    		ammount.add(count);
    		rolls -= count;
    	}
    	
    	
    	// Fill the item into the carousel, using the amount list that has rolled, and extra algotihm to prefix rater item to get close to the reward item slot (extra copium added).
        for (int i = 6; i >= 0; i--) {
        	if (rarityBuckets.get(i).isEmpty()) continue;
        	
            if (i == 6) { // Common case
                for (int j = 0; j < rolls; j++) {
                    DungeonDropData.Rule randomRule = rarityBuckets.get(i)
                            .get(ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size()));
                    carouselItems.add(randomRule);
                }
            } else if (i < 3 && ammount.get(i) > 0) { // RNG case
                int x = ThreadLocalRandom.current().nextInt(0, 90);
                double randomSlot = (Math.exp(0.04605 * x) * (-1) + 100) / 100 * carouselItems.size() - 1;
                DungeonDropData.Rule randomRule = rarityBuckets.get(i)
                        .get(ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size()));
                carouselItems.add((int) randomSlot, randomRule);
                if (MyConfig.debugMode) System.out.println("RNG DROP rarity " + i + " at slot " + randomSlot + " x " + x);
            } else { // Rare case
                for (int j = 0; j < ammount.get(i); j++) {
                    int randomSlot = ThreadLocalRandom.current().nextInt(carouselItems.size() - 1);
                    int itemIndex = ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size());
                    DungeonDropData.Rule rule = rarityBuckets.get(i).get(itemIndex);
                    carouselItems.add(randomSlot, rule);
                    rarityBuckets.get(i).remove(itemIndex);
                }
            }
        }
		carouselItems.add(rewardSlot, rewardItem);
    }
   
    
   int getBoxColor(DropRarity rarity) {
	   //get the color of item boxes connect with their rarity.
        switch (rarity) {
            case PRAYTORNG:
                return getRainbowGlowColor();
            case DIVINE:
                return 0xFF4EEBEB;
            case MYTHIC:
                return 0xFFF953F9;
            case LEGENDARY:
            	return 0xFFEF9E01;
            case EPIC:
            	return 0xFFAA00AA;
            case FISH:
            	return 0xFF4C4BE2;
            case COMMON:
            	return 0xFFE0DFE0;
            default:
                return 0xFF000000;
        }
    }

   
    private int getRainbowGlowColor() {
    	//Custom rgb sequence just for PRAYTORNG rarity
        double time = System.currentTimeMillis() % 10000L / 1000.0;
        float hue = (float) time;
        int rgb = Color.HSBtoRGB(hue, 0.6f, 1.0f);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
    
    
    public static double randStopPoint() {
    	//Randomize the randStop (the position that the carousel stop on the reward slot (0 -> 1)
        double rand = ThreadLocalRandom.current().nextDouble(0, 100); // 0–100

        if (rand < 40) {
            return ThreadLocalRandom.current().nextDouble(0, 0.2);
        } else if (rand < 60) {
            return ThreadLocalRandom.current().nextDouble(0.2, 0.8);
        } else {
            return ThreadLocalRandom.current().nextDouble(0.8, 1.0);
        }
    }
    
    
    private double velocityFromX(double x, float scaleFactor) {
    	//Custom speed algorithm for currentScrollSpeed (east out cubic)
        double X = stopPoint - slowPoint;
        double T = slowTime ;
        
        if (x <= 0) {
            double v0 = X * 3 / T;
            return v0;
        }
        
        if (x >= X) return 0;
        double normX = x / X;
        double t = T * (1 - Math.cbrt(1 - normX)); 
        double nt = t / T;
        
        return X * 3 * Math.pow(1 - nt, 2) / T;
    }

    
    private void updateLayout(ScaledResolution scaled) {
    	//Update layout when the player changes GUI, Screen Resolutions.
        this.itemBoxWidth = screenWidth / 5f;
        this.itemBoxHeight = screenHeight / 4f;
        this.itemBoxPadding = screenWidth / 64f;
        this.spacing = itemBoxWidth + itemBoxPadding;

        if (lastSpacing > 0f) {
            float ratio = spacing / lastSpacing;
            offsetX *= ratio;
        }

        this.lastSpacing = spacing;
        this.lastScaleFactor = scaleFactor;
        this.lastScreenWidth = screenWidth;
        this.lastScreenHeight = screenHeight;
        
        this.centerX = screenWidth / 2;
        this.centerY = screenHeight / 2;
        
        this.stopPoint = 41 * spacing + randstop * itemBoxWidth;
        this.slowPoint = stopPoint - ((slowDistance + randslow) * spacing);
        
        frameBufferLayer1 = new Framebuffer(screenWidth, screenHeight, true);
        frameBufferLayer2 = new Framebuffer(screenWidth, screenHeight, true);
        guiBuffer = new Framebuffer(screenWidth, screenHeight, true);
        
        GL11.glPushMatrix();
        GL11.glScalef(1f / scaleFactor, 1f / scaleFactor, 1f);
        setupStencilLayer1();
        setupStencilLayer2();
        GL11.glPopMatrix();
        
        if (blurShader != null) {
        	blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
        }
        
        // DEBUG Print
        if(MyConfig.debugMode) System.out.println("[DEBUG] Resolution changed → New scaleFactor=" + scaleFactor +
            ", width=" + screenWidth + ", height=" + screenHeight);
        if(MyConfig.debugMode) System.out.println("[DEBUG] Resolution changed → MC.window" + mc.displayWidth +
                " x " + mc.displayHeight);
    }

    
//===================================================
//		        ONE-TIME GUI FUNCTION
//===================================================   
    @Override
    public void initGui() {
    	//Initialize the GUI right after CustomAnimationGUI() called.
    	//Only call once to use the blur shader
        super.initGui();
        if (mc.entityRenderer != null && mc.getFramebuffer() != null) {
            try {
            	blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("bedrockcaseopening", "shaders/post/blur.json"));
                blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
//===================================================
//    			EVERY-FRAME FUNCTION
//===================================================       
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
        GlStateManager.disableAlpha();
        //===========================================================
    	// PRE-RENDER CALCULATION
        //===========================================================
        
        float progress = (System.nanoTime() - guiOpenStartTime) / (float) animationDuration;
        progress = Math.min(progress, 1.0f);
        progress = 1 - (1 - progress) * (1 - progress);
    	
    	// Get accurate Width and Height compare with Game Resolution and GUI Settings in Minecraft,
        ScaledResolution scaled = new ScaledResolution(mc);
        scaleFactor = scaled.getScaleFactor();
        screenWidth = scaled.getScaledWidth() * scaleFactor;
        screenHeight = scaled.getScaledHeight() * scaleFactor;
        
        if (scaleFactor != lastScaleFactor || screenWidth != lastScreenWidth || screenHeight != lastScreenHeight) {
            updateLayout(scaled);
        }
        
        // Calculate DeltaTime ( MUST ) 
        long now = System.nanoTime();
        if (lastFrameTime == 0) lastFrameTime = now;
        float deltaTime = (now - lastFrameTime) / 1_000_000_000f;
        deltaTime = Math.min(deltaTime, 0.05f); 
        lastFrameTime = now;

        // Calculate Velocity by distance at slowPoint 
        if(!hasShownResult) {
	        currentScrollSpeed = (float) velocityFromX(offsetX - slowPoint, scaleFactor);
	        offsetX += currentScrollSpeed * deltaTime;
	        if (currentScrollSpeed == 0) {
	        	hasShownResult = true;
	        }
        }
        
        //If the results are up, play the reward sound, then switch GUI.
    	if (hasShownResult) {
    	    String sound;
    	    switch (rewardItem.rarity) {
    	        case 7: sound = "dig.grass"; break;
    	        case 6: sound = "liquid.splash"; break;
    	        case 5: sound = "random.orb"; break;
    	        case 4: sound = "fireworks.launch"; break;
    	        case 3: sound = "random.levelup"; break;
    	        case 2: sound = "mob.wither.spawn"; break;
    	        case 1: sound = "random.levelup"; break;
    	        default: return;
    	    }
    	    Minecraft.getMinecraft().getSoundHandler().playSound(
    	        PositionedSoundRecord.create(new ResourceLocation("minecraft", sound), 1.0F)
    	    );
    	    
    	    if (rewardItem.rarity == 1) {
    	    	Minecraft.getMinecraft().getSoundHandler().playSound(
	        	        PositionedSoundRecord.create(new ResourceLocation("minecraft", "fireworks.launch"), 1.0F)
	        	    );
    	    	Minecraft.getMinecraft().getSoundHandler().playSound(
	        	        PositionedSoundRecord.create(new ResourceLocation("minecraft", "mob.wither.spawn"), 1.0F)
	        	    );
    	    }
            Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
    	}
        
        //===========================================================
    	// RENDER
        //===========================================================
        GL11.glPushMatrix();
        GL11.glScalef(1f / scaleFactor, 1f / scaleFactor, 1f);
        
        //drawDefaultBackground();
        renderLayer1(progress);
        
        blurShader.loadShaderGroup(partialTicks);
        
        renderLayer2(progress); 
        renderJudgementLine();
        
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

//===================================================
//					RENDER FUNCTIONS
//===================================================       
    public void setupStencilLayer1() {
        frameBufferLayer1.setFramebufferColor(0, 0, 0, 0);  
        frameBufferLayer1.enableStencil();
        frameBufferLayer1.bindFramebuffer(true);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        GL11.glColorMask(false, false ,false, false);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilMask(0xFF);
        
        drawCircleAlphaMask(centerX, centerY, centerY * 2/3, 0xFFFFFFFF, 64);

        GL11.glColorMask(true, true, true, true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
    
    public void renderLayer1(float progress) {
    	frameBufferLayer1.bindFramebuffer(true);
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilMask(0x00);
        
    	float scale = progress;
    	
        drawCarousel(centerX, centerY, scale * 0.8f);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(alphaFadeSidePNG);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 1920, 1080, screenWidth, screenHeight);
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        
        mc.getFramebuffer().bindFramebuffer(true);
        
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        frameBufferLayer1.framebufferRenderExt(screenWidth, screenHeight, false);
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
    
    public void setupStencilLayer2() {
    	frameBufferLayer2.setFramebufferColor(0, 0, 0, 0);  
    	frameBufferLayer2.enableStencil();
    	frameBufferLayer2.bindFramebuffer(true);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        GL11.glColorMask(false, false, false, false);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilMask(0xFF);
        
        drawCircleAlphaMask(centerX, centerY, centerY * 2/3, 0xFFFFFFFF, 64);
        
        GL11.glColorMask(true, true, true, true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
    
    public void renderLayer2(float progress) {
    	frameBufferLayer2.bindFramebuffer(true);

    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilMask(0x00);
        
    	GlStateManager.pushMatrix();
    	float scale = progress;
        
    	GlStateManager.color(1f, 1f, 1f, 1f);
        drawCircleAlphaMask(centerX, centerY, centerY * 2/3 * scale, 0x3F000000, 64);

        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawCarousel(centerX, centerY, 1f * scale);
        GlStateManager.popMatrix();
        
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        
        mc.getFramebuffer().bindFramebuffer(true);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        frameBufferLayer2.framebufferRenderExt(screenWidth, screenHeight, false);
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
    
    
    public void drawCarousel(float centerX, float centerY, float size) {
        for (int i = 0; i < carouselItems.size(); i++) {
            float x = centerX - (offsetX - (i - 3) * spacing) * size;
            float y = centerY - itemBoxHeight / 2 * size;

            if ((x + itemBoxWidth) < 0 || x > screenWidth ||
                (y + itemBoxHeight) < 0 || y > screenHeight) {
                continue;
            }
            
            DropRarity rarity = DropRarity.fromIndex(carouselItems.get(i).rarity);
            if (rarity == null) continue;  
            int boxColor = getBoxColor(rarity);
            
            float y1 = y + ((float)(itemBoxHeight * size * 15)/16);
            float y2 = y + itemBoxHeight * size;
            
            float textX = x + itemBoxWidth  * 1/2;
            float textY = y + itemBoxHeight * 3/4;
            
            GlStateManager.color(1f, 1f, 1f, 1f);
            drawRect((int) x, (int) y, (int) (x + itemBoxWidth * size), (int) (y + itemBoxHeight * size), 0x3F888888);
            
            GlStateManager.color(1f, 1f, 1f, 1f);
            drawRect((int) x, (int) y1, (int) (x + itemBoxWidth * size), (int) y2, boxColor);
            
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1f, 1f, 1f, 1f);
            drawGradientRect((int)x, (int) (y2 -  itemBoxHeight * size) , (int)(x + itemBoxWidth * size), (int)y2, (boxColor & 0x00FFFFFF) | 0x00 << 24, (boxColor & 0x00FFFFFF) | 0xCC << 24);
            GlStateManager.disableBlend();
            
            renderImage(i, x, y, size);  
            
            if(MyConfig.allowText) {
                GL11.glPushMatrix();
    	        GL11.glScalef(MyConfig.textScale, MyConfig.textScale, 1f);
    	        floatFont.drawCenteredString(normalizeString(carouselItems.get(i).item.name()), textX / MyConfig.textScale, textY / MyConfig.textScale, boxColor, true);
    			GL11.glPopMatrix();
            }
        }
    }
    
    public String normalizeString(String input) {
        if (input == null || input.isEmpty()) return "";

        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            String word = part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
            result.append(word).append(" ");
        }

        return result.toString().trim();
    }
    
    public void renderJudgementLine() {
        float lineWidth = screenWidth / 512;
        float lineHeight = screenHeight / 4;
        drawRect((int)(centerX - lineWidth/2),
        		(int)(centerY - lineHeight/3 * 2.5),
        		(int)(centerX + lineWidth),
        		(int)(centerY + lineHeight), 0xFFFFA500
        		);    
        
        int boxDistance = (int)offsetX % (int)spacing;
        if(boxDistance < lastBoxDistance) {
        	mc.getSoundHandler().playSound(PositionedSoundRecord.create(audio, 1.0F));
        }
        lastBoxDistance = boxDistance;
    }
    
    public void renderImage(int slot, float currentX, float currentY, float size) {
        ResourceLocation image = CitManager.getTextureData(carouselItems.get(slot).item.name()).getRl();        
    	int frameCount = CitManager.getTextureData(carouselItems.get(slot).item.name()).getFrames();;
    	int frameHeight = 16;
    	float frameDurationMs = CitManager.getTextureData(carouselItems.get(slot).item.name()).getFrametime() * 50;

    	long time = System.currentTimeMillis() % 10000;
    	int frameIndex = (int)((long)(time / frameDurationMs) % frameCount);
    	float v = frameIndex * frameHeight;

        if (image != null) {        	
        	int imageWidth = 16, imageHeight = 16;
        	float imageSize = 0.8f * itemBoxHeight * size;
        	float imageX = currentX + (itemBoxWidth * size)/2 - imageSize/2;
        	float imageY = currentY + (itemBoxHeight * size)/2 - imageSize/2;
        	
        	try {
        	    mc.getTextureManager().bindTexture(image);
        	} catch (Exception e) {
        		if(MyConfig.debugMode) System.out.println(e);
        	    mc.getTextureManager().bindTexture(TextureMap.LOCATION_MISSING_TEXTURE);
        	}
        	
        	GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        	drawScaledCustomSizeModalRect((int)imageX, (int)imageY, 0, v, imageWidth, imageHeight, (int)imageSize, (int)imageSize, 16f, frameHeight * frameCount);
        }
    }
    
    
    public void drawCircleAlphaMask(float centerX, float centerY, float radius, int color, int segments) {
        if (segments < 3) segments = 32;

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);
        
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(centerX, centerY);
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            float x = (float) (centerX + Math.cos(angle) * radius);
            float y = (float) (centerY + Math.sin(angle) * radius);
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        
        if (mc.entityRenderer != null) {
            mc.entityRenderer.stopUseShader();
        }
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
        	Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

}
