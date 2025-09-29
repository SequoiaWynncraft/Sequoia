package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Toast notification for temporary messages with flat design
 */
public class AutreToast extends AutreContainer {
    public enum ToastType {
        INFO(AutreRenderer2.Color.getAccent()),
        SUCCESS(new AutreRenderer2.Color(0.298f, 0.686f, 0.314f, 1f)),
        WARNING(new AutreRenderer2.Color(1f, 0.596f, 0f, 1f)),
        ERROR(new AutreRenderer2.Color(0.957f, 0.263f, 0.212f, 1f));
        
        public final AutreRenderer2.Color color;
        
        ToastType(AutreRenderer2.Color color) {
            this.color = color;
        }
    }
    
    protected String message;
    protected ToastType type = ToastType.INFO;
    protected float displayTime = 3000f; // 3 seconds
    protected float currentTime = 0f;
    protected boolean autoHide = true;
    protected Runnable onDismiss;
    
    public AutreToast(float x, float y, float width, float height, String message) {
        super(x, y, width, height);
        this.message = message;
        
        // Toast styling - flat with colored accent
        setBackgroundColor(AutreRenderer2.Color.SURFACE);
        setBorderWidth(3f);
        setBorderColor(type.color);
    }
    
    public AutreToast setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public AutreToast setType(ToastType type) {
        this.type = type;
        setBorderColor(type.color);
        return this;
    }
    
    public AutreToast setDisplayTime(float timeMs) {
        this.displayTime = timeMs;
        return this;
    }
    
    public AutreToast setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
        return this;
    }
    
    public AutreToast setOnDismiss(Runnable onDismiss) {
        this.onDismiss = onDismiss;
        return this;
    }
    
    public void show() {
        setVisible(true);
        currentTime = 0f;
    }
    
    public void dismiss() {
        setVisible(false);
        if (onDismiss != null) {
            onDismiss.run();
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Update timer
        if (autoHide) {
            currentTime += deltaTime;
            if (currentTime >= displayTime) {
                dismiss();
                return;
            }
        }
        
        // Render background and border
        super.renderSelf(context, mouseX, mouseY, deltaTime);
        
        // Render message
        if (message != null && !message.isEmpty()) {
            float textWidth = mc.textRenderer.getWidth(message);
            float textHeight = mc.textRenderer.fontHeight;
            
            float textX = getAbsoluteX() + (width - textWidth) / 2;
            float textY = getAbsoluteY() + (height - textHeight) / 2;
            
            AutreRenderer2.drawText(context, message, textX, textY, AutreRenderer2.Color.TEXT_PRIMARY, false);
        }
        
        // Progress bar if auto-hide is enabled
        if (autoHide && displayTime > 0) {
            float progress = currentTime / displayTime;
            float progressWidth = width * progress;
            
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY() + height - 2f,
                progressWidth, 2f, type.color);
        }
    }
}