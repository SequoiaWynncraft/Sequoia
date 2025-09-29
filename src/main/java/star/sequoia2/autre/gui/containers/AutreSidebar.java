package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Resizable sidebar panel with flat design
 */
public class AutreSidebar extends AutreContainer {
    protected boolean isCollapsed = false;
    protected boolean isResizable = true;
    protected Side side = Side.LEFT;
    protected float minWidth = 150f;
    protected float maxWidth = 400f;
    protected float collapsedWidth = 32f;
    protected float resizeHandleWidth = 4f;
    
    // Animation
    protected float currentWidth;
    protected float targetWidth;
    protected float animationSpeed = 8f;
    
    // Interaction state
    protected boolean isResizing = false;
    protected float resizeStartX = 0f;
    protected float resizeStartWidth = 0f;
    
    // Colors for flat design
    protected AutreRenderer2.Color sidebarBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color resizeHandleColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color resizeHandleHoverColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color collapseButtonBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color collapseButtonHover = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    
    // Header
    protected String title = "";
    protected boolean showHeader = true;
    protected float headerHeight = 30f;
    
    public enum Side {
        LEFT, RIGHT
    }
    
    public AutreSidebar(float x, float y, float width, float height, Side side) {
        super(x, y, width, height);
        this.side = side;
        this.currentWidth = width;
        this.targetWidth = width;
        
        addEventListener(MouseClickEvent.class, this::handleClick);
        addEventListener(MouseHoverEvent.class, this::handleHover);
    }
    
    public AutreSidebar setTitle(String title) {
        this.title = title != null ? title : "";
        return this;
    }
    
    public AutreSidebar setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
        return this;
    }
    
    public AutreSidebar setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
        this.targetWidth = collapsed ? collapsedWidth : width;
        return this;
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    public AutreSidebar setResizable(boolean resizable) {
        this.isResizable = resizable;
        return this;
    }
    
    public AutreSidebar setMinMaxWidth(float minWidth, float maxWidth) {
        this.minWidth = Math.max(collapsedWidth, minWidth);
        this.maxWidth = Math.max(this.minWidth, maxWidth);
        return this;
    }
    
    public float getCurrentWidth() {
        return currentWidth;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check collapse button (in header)
        if (showHeader && event.pressed) {
            float buttonSize = 20f;
            float buttonX = side == Side.LEFT ? absX + currentWidth - buttonSize - 6f : absX + 6f;
            float buttonY = absY + (headerHeight - buttonSize) / 2f;
            
            if (event.x >= buttonX && event.x <= buttonX + buttonSize &&
                event.y >= buttonY && event.y <= buttonY + buttonSize) {
                setCollapsed(!isCollapsed);
                return;
            }
        }
        
        // Check resize handle
        if (isResizable && !isCollapsed) {
            float handleX = side == Side.LEFT ? absX + currentWidth - resizeHandleWidth : absX;
            
            if (event.x >= handleX && event.x <= handleX + resizeHandleWidth &&
                event.y >= absY && event.y <= absY + height) {
                
                if (event.pressed) {
                    isResizing = true;
                    resizeStartX = event.x;
                    resizeStartWidth = currentWidth;
                } else {
                    isResizing = false;
                }
            }
        }
        
        if (!event.pressed) {
            isResizing = false;
        }
    }
    
    private void handleHover(MouseHoverEvent event) {
        if (!enabled || !visible || !isResizing) return;
        
        // Handle resize dragging
        if (isResizing) {
            float deltaX = event.x - resizeStartX;
            if (side == Side.RIGHT) {
                deltaX = -deltaX; // Reverse for right sidebar
            }
            
            float newWidth = Math.max(minWidth, Math.min(maxWidth, resizeStartWidth + deltaX));
            this.width = newWidth;
            this.currentWidth = newWidth;
            this.targetWidth = newWidth;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Animate width
        if (Math.abs(currentWidth - targetWidth) > 0.5f) {
            float diff = targetWidth - currentWidth;
            currentWidth += diff * animationSpeed * deltaTime / 1000f;
        } else {
            currentWidth = targetWidth;
        }
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Sidebar background
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, currentWidth, height, sidebarBg);
        
        // Border
        if (side == Side.LEFT) {
            AutreRenderer2.strokeRect(context.getMatrices(),
                absX + currentWidth - 1f, absY, 1f, height, 1f, borderColor);
        } else {
            AutreRenderer2.strokeRect(context.getMatrices(),
                absX, absY, 1f, height, 1f, borderColor);
        }
        
        // Header
        if (showHeader) {
            renderHeader(context, absX, absY, mouseX, mouseY);
        }
        
        // Resize handle
        if (isResizable && !isCollapsed) {
            renderResizeHandle(context, absX, absY, mouseX, mouseY);
        }
    }
    
    private void renderHeader(DrawContext context, float x, float y, float mouseX, float mouseY) {
        // Header background
        AutreRenderer2.fillRect(context.getMatrices(),
            x, y, currentWidth, headerHeight, sidebarBg.darker(0.05f));
        
        // Header border
        AutreRenderer2.strokeRect(context.getMatrices(),
            x, y + headerHeight - 1f, currentWidth, 1f, 1f, borderColor);
        
        // Collapse button
        float buttonSize = 20f;
        float buttonX = side == Side.LEFT ? x + currentWidth - buttonSize - 6f : x + 6f;
        float buttonY = y + (headerHeight - buttonSize) / 2f;
        
        boolean isButtonHovered = mouseX >= buttonX && mouseX <= buttonX + buttonSize &&
                                mouseY >= buttonY && mouseY <= buttonY + buttonSize;
        
        // Button background
        AutreRenderer2.Color buttonBgColor = isButtonHovered ? collapseButtonHover : collapseButtonBg;
        AutreRenderer2.fillRect(context.getMatrices(),
            buttonX, buttonY, buttonSize, buttonSize, buttonBgColor);
        
        // Button icon
        String collapseIcon;
        if (isCollapsed) {
            collapseIcon = side == Side.LEFT ? "◀" : "▶";
        } else {
            collapseIcon = side == Side.LEFT ? "▶" : "◀";
        }
        
        float iconX = buttonX + (buttonSize - mc.textRenderer.getWidth(collapseIcon)) / 2f;
        float iconY = buttonY + (buttonSize - mc.textRenderer.fontHeight) / 2f;
        
        AutreRenderer2.drawText(context, collapseIcon, iconX, iconY, AutreRenderer2.Color.TEXT_PRIMARY, false);
        
        // Title (when expanded and not empty)
        if (!isCollapsed && !title.isEmpty() && currentWidth > collapsedWidth + 40f) {
            float titleX = side == Side.LEFT ? x + 8f : x + buttonSize + 14f;
            float titleY = y + (headerHeight - mc.textRenderer.fontHeight) / 2f;
            
            // Clip title text
            float availableWidth = currentWidth - buttonSize - 20f;
            String clippedTitle = clipTextToWidth(title, availableWidth);
            
            AutreRenderer2.drawText(context, clippedTitle, titleX, titleY, AutreRenderer2.Color.TEXT_PRIMARY, false);
        }
    }
    
    private void renderResizeHandle(DrawContext context, float x, float y, float mouseX, float mouseY) {
        float handleX = side == Side.LEFT ? x + currentWidth - resizeHandleWidth : x;
        
        boolean isHandleHovered = mouseX >= handleX && mouseX <= handleX + resizeHandleWidth &&
                                mouseY >= y && mouseY <= y + height;
        
        AutreRenderer2.Color handleColor = isHandleHovered || isResizing ? resizeHandleHoverColor : resizeHandleColor;
        
        AutreRenderer2.fillRect(context.getMatrices(),
            handleX, y, resizeHandleWidth, height, handleColor);
    }
    
    private String clipTextToWidth(String text, float maxWidth) {
        if (mc.textRenderer.getWidth(text) <= maxWidth) {
            return text;
        }
        
        // Binary search for maximum length that fits
        int left = 0, right = text.length();
        while (left < right) {
            int mid = (left + right + 1) / 2;
            String candidate = text.substring(0, mid) + "...";
            if (mc.textRenderer.getWidth(candidate) <= maxWidth) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        
        return left > 0 ? text.substring(0, left) + "..." : "...";
    }
    
    @Override
    public float getContentX() {
        return x + padding;
    }
    
    @Override
    public float getContentY() {
        return y + (showHeader ? headerHeight : 0) + padding;
    }
    
    @Override
    public float getContentWidth() {
        return currentWidth - (padding * 2) - (isResizable ? resizeHandleWidth : 0);
    }
    
    @Override
    public float getContentHeight() {
        return height - (showHeader ? headerHeight : 0) - (padding * 2);
    }
}