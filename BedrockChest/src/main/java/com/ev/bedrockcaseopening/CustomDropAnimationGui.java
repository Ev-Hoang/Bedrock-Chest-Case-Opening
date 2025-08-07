// File: CustomDropAnimationGui.java
package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import io.netty.util.internal.ThreadLocalRandom;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class CustomDropAnimationGui extends GuiScreen {
    private final List<DungeonDropItem> carouselItems = new ArrayList<>();
    List<DungeonDropItem> availableDrops = new ArrayList<>();
    private DungeonDropItem rewardItem;
    
    private Minecraft mc;
    
    private ResourceLocation alphaMaskInPNG; // not use, maybe update in the future for optimization
    private ResourceLocation alphaFadeSidePNG = new ResourceLocation("bedrockcaseopening", "textures/gui/fade_side.png");
    private ResourceLocation audio = new ResourceLocation("gui.button.press");
    
    Framebuffer frameBufferLayer1, frameBufferLayer2;
    Framebuffer guiBuffer;
    private ShaderGroup blurShader;

    private float offsetX = 0f;
    
    private float currentScrollSpeed;
    private float slowTime = 7f; 
    private int slowDistance = 20; // In box count
    
    //private boolean isSlowingDown = false;
    private boolean hasShownResult = false;
    
    private int lastBoxDistance = 0;
    private boolean hasBoxEndCrossJudgementLine = false;
    private boolean hasBoxStartCrossJudgementLine = false;

    private long lastFrameTime = 0L;

    private long guiOpenStartTime = -1;
    private final long animationDuration = 300_000_000L; // 300ms
    
    private final int itemCount = 50;
    private int rewardSlot;

    
    private int scaleFactor;
    private int screenWidth;
    private int screenHeight;
    private int lastScaleFactor = -1;
    private int lastScreenWidth = -1;
    private int lastScreenHeight = -1;

    private float itemBoxWidth, itemBoxHeight, itemBoxPadding, spacing;
    private float lastSpacing = -1f;
    private float centerX, centerY;
    
    private float stopPoint, slowPoint;
    
    private float randslow;
    private float randstop;
    
    //Initialize the GUI
    public CustomDropAnimationGui(DungeonDropItem rewardItem) {
    	System.out.println("Custom Animation UI Initialize");
    	mc = Minecraft.getMinecraft();
    	this.rewardItem = rewardItem;
    	rewardRandomizer(rewardItem);
    	
    	mc.getSoundHandler().playSound(PositionedSoundRecord.create(audio, 1.0F));
    	
        randstop = (float)getWeightedRandom();
        randslow = (float)ThreadLocalRandom.current().nextDouble(-2, 2);
        
        //currentScrollSpeed = baseScrollSpeed;
        this.lastFrameTime = System.nanoTime();
        this.guiOpenStartTime = System.nanoTime();
    }

    private void rewardRandomizer(DungeonDropItem rewardItem) {
    	List<List<DungeonDropItem>> rarityBuckets = new ArrayList<>();

    	// Khởi tạo 7 bucket rỗng tương ứng với rarity 1 đến 7
    	for (int i = 0; i < 7; i++) {
    	    rarityBuckets.add(new ArrayList<>());
    	}

    	// Phân loại item vào từng bucket
    	for (DungeonDropItem item : DungeonDropItem.values()) {
    	    int rarity = item.getRarityIndex(); // Assumes getRarity() ∈ [1, 7]
    	    rarityBuckets.get(rarity - 1).add(item);
    	} 
    	
    	//test
    	for(int i = 0; i < rarityBuckets.size(); i++) {
    		System.out.println(rarityBuckets.get(i).size());
    	}
    	
    	//randomize from rollsize, per each rarity
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
    	
    	if(rewardItem.getRarityIndex() <= 3) {
    		checkRepeat.set(rewardItem.getRarityIndex() - 1, true);       	
    	}
    	
    	int rolls = itemCount - 1; // not roll for RewardItem slot.
    	for(int i = 0; i < 6; i++) {
    		int count = 0;
    		
    		//Check for High Rarity already got on carousel
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
    	
    	
        //Centered the reward in the carousel
    	rewardSlot = itemCount - 6;
		for (DungeonDropItem item : DungeonDropItem.values()) availableDrops.add(item); 	
		for(int i = 6 ; i >=0; i--) {
			if (i == 6) { //Common case (repititive + fill the item slots)
				for(int j = 0; j < rolls; j++) { 
					DungeonDropItem randomItem = rarityBuckets.get(i).get(
	    	            ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size())
	    	        );
	    	        carouselItems.add(randomItem);
				}
			} else if (i < 3 && ammount.get(i)>0) { //RNG case (only 1 per case)
				int x = ThreadLocalRandom.current().nextInt(0,90);
				double randomSlot = (Math.exp(0.04605 * x) * (-1) + 100)/100 * carouselItems.size() - 1;
				int itemInRarityBucket = ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size());
				carouselItems.add((int)randomSlot, rarityBuckets.get(i).get(itemInRarityBucket));
				System.out.println("RNG DROP rarity " + i + " at slot " + randomSlot + " x " + x);
				
			} else { //Rare case (fill in item slots + not repititive
				for(int j = 0; j < ammount.get(i); j++) {
					int randomSlot = ThreadLocalRandom.current().nextInt(carouselItems.size()-1);
					int itemInRarityBucket = ThreadLocalRandom.current().nextInt(rarityBuckets.get(i).size());
					carouselItems.add(randomSlot, rarityBuckets.get(i).get(itemInRarityBucket));
					rarityBuckets.get(i).remove(itemInRarityBucket);
				}
			}		
		}
		carouselItems.add(rewardSlot, rewardItem);

//    	for (int i = 0; i < itemCount; i++) {
//    		System.out.println(i + " " + carouselItems.get(i).name());
//    	}
    }
   
   int getBoxColor(DropRarity rarity) {
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
        double time = System.currentTimeMillis() % 10000L / 1000.0;
        float hue = (float) time;
        int rgb = Color.HSBtoRGB(hue, 0.6f, 1.0f);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static double getWeightedRandom() {
        double rand = ThreadLocalRandom.current().nextDouble(0, 100); // 0–100

        if (rand < 40) {
            // 50% cơ hội: 0.1 – 0.3
            return ThreadLocalRandom.current().nextDouble(0.1, 0.2);
        } else if (rand < 60) {
            // 20% cơ hội: 0.3 – 0.4
            return ThreadLocalRandom.current().nextDouble(0.2, 0.8);
        } else {
            // 30% cơ hội: 0.4 – 1.0
            return ThreadLocalRandom.current().nextDouble(0.8, 1.0);
        }
    }
    
    private double velocityFromX(double x, float scaleFactor) {
        double X = stopPoint - slowPoint;
        double T = slowTime ;

        // Nếu x < 0 thì dùng velocity tại x = 0
        if (x <= 0) {
            double v0 = X * 3 / T;  // tương đương với v tại t = 0
            return v0;
        }

        // Nếu x >= X (đi hết quãng đường) thì dừng
        if (x >= X) return 0;

        // Nội suy theo easing cubic
        double normX = x / X;
        double t = T * (1 - Math.cbrt(1 - normX)); // t(x) theo ease-out
        double nt = t / T;

        return X * 3 * Math.pow(1 - nt, 2) / T; // v(t)
    }

    
    private void updateLayout(ScaledResolution scaled) {
        this.itemBoxWidth = screenWidth / 5f;
        this.itemBoxHeight = screenHeight / 4f;
        this.itemBoxPadding = screenWidth / 64f;
        this.spacing = itemBoxWidth + itemBoxPadding;

        // Điều chỉnh offsetX theo spacing mới
        if (lastSpacing > 0f) {
            float ratio = spacing / lastSpacing;
            offsetX *= ratio;
        }

        // Lưu lại thông tin resolution
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
        
        // DEBUG
        System.out.println("[DEBUG] Resolution changed → New scaleFactor=" + scaleFactor +
            ", width=" + screenWidth + ", height=" + screenHeight);
        System.out.println("[DEBUG] Resolution changed → MC.window" + mc.displayWidth +
                " x " + mc.displayHeight);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (mc.entityRenderer != null && mc.getFramebuffer() != null) {
            try {
            	blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("bedrockcaseopening", "shaders/post/blur.json"));
                blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //this.buttonList.add(new GuiButton(0, screenWidth / 2 - 100, screenHeight / 8 * 7 - 10, 200, 20, "Skip!"));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        //===========================================================
    	// PRE-RENDER CALCULATION
        //===========================================================
        
        float progress = (System.nanoTime() - guiOpenStartTime) / (float) animationDuration;
        progress = Math.min(progress, 1.0f); // Clamp trong [0, 1]

        // Optionally apply easing (ví dụ: easeOutQuad)
        progress = 1 - (1 - progress) * (1 - progress);
    	
    	// Get accurate Width and Height compare with Gaame Resolution and GUI Settings in Minecraft
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
	        if (currentScrollSpeed == 0) {
	        	if (!hasShownResult) {
	        	    String sound;
	        	    switch (rewardItem.getRarity().getIndex()) {
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
	        	    
	        	    if (rewardItem.getRarity().getIndex() == 1) {
	        	    	Minecraft.getMinecraft().getSoundHandler().playSound(
	    	        	        PositionedSoundRecord.create(new ResourceLocation("minecraft", "fireworks.launch"), 1.0F)
	    	        	    );
	        	    	Minecraft.getMinecraft().getSoundHandler().playSound(
	    	        	        PositionedSoundRecord.create(new ResourceLocation("minecraft", "mob.wither.spawn"), 1.0F)
	    	        	    );
	        	    }
		           // Minecraft.getMinecraft().thePlayer.addChatMessage(
		           //     new ChatComponentText("You received: Test" + " position " + offsetX + " target " + stopPoint));
		            Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
		            hasShownResult = true;
	        	}
	        }
        }
        
        float scrollSpeed = currentScrollSpeed; // 4 = Highest GUI Settings (3) + 1 
        offsetX += scrollSpeed * deltaTime;
        
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
        
    	float scale = progress; // Scale từ 0.8 → 1.0      
    	
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
    	float scale = progress; // Scale từ 0.8 → 1.0
        
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
            
            DropRarity rarity = carouselItems.get(i).getRarity();
            if (rarity == null) continue;
            int boxColor = getBoxColor(rarity);
            
            float y1 = y + ((float)(itemBoxHeight * size * 15)/16);
            float y2 = y + itemBoxHeight * size;
            
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
        }
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
        	System.out.print("hi");
        	mc.getSoundHandler().playSound(PositionedSoundRecord.create(audio, 1.0F));
        	//first try btw
        }
        lastBoxDistance = boxDistance;
    }
    
    public void renderImage(int slot, float currentX, float currentY, float size) {
        ResourceLocation image = carouselItems.get(slot).getImage();
    	int frameCount = carouselItems.get(slot).getFrameCount();
    	int frameHeight = 16;
    	float frameDurationMs = carouselItems.get(slot).getFrameTick() * 50;

    	long time = System.currentTimeMillis() % 10000;
    	int frameIndex = (int)((long)(time / frameDurationMs) % frameCount);
    	float v = frameIndex * frameHeight;

        if (image != null) {
        	float imageSize = 0.8f * itemBoxHeight * size;
        	float imageX = currentX + (itemBoxWidth * size)/2 - imageSize/2;
        	float imageY = currentY + (itemBoxHeight * size)/2 - imageSize/2;
        	
        	mc.getTextureManager().bindTexture(image);
        	GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        	drawScaledCustomSizeModalRect((int)imageX, (int)imageY, 0, v, 16, 16, (int)imageSize, (int)imageSize, 16f, frameHeight * frameCount);
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
