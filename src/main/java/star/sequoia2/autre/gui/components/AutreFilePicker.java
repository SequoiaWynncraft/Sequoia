package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.io.File;

import static star.sequoia2.client.SeqClient.mc;
import java.util.*;
import java.util.function.Consumer;

/**
 * File picker with directory navigation and flat design
 */
public class AutreFilePicker extends AutreComponent {
    protected File selectedFile = null;
    protected File currentDirectory = new File(System.getProperty("user.home"));
    protected boolean isOpen = false;
    protected List<File> currentFiles = new ArrayList<>();
    protected String fileFilter = "*"; // "*" for all files, or extension like "*.txt"
    protected boolean directoryMode = false; // true to select directories instead of files
    
    // Browser dimensions
    protected float browserWidth = 300f;
    protected float browserHeight = 200f;
    protected float itemHeight = 18f;
    protected float headerHeight = 25f;
    protected float scrollOffset = 0f;
    protected int selectedIndex = -1;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color placeholderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    protected AutreRenderer2.Color browserBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color headerBg = AutreRenderer2.Color.BACKGROUND;
    protected AutreRenderer2.Color selectedBg = AutreRenderer2.Color.getAccent().withAlpha(0.2f);
    protected AutreRenderer2.Color hoverBg = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color folderColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color fileColor = AutreRenderer2.Color.TEXT_PRIMARY;
    
    protected Consumer<File> onFileSelected;
    
    public AutreFilePicker(float x, float y, float width, float height) {
        super(x, y, width, height);
        refreshDirectory();
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreFilePicker setSelectedFile(File file) {
        this.selectedFile = file;
        if (file != null && file.getParentFile() != null) {
            setCurrentDirectory(file.getParentFile());
        }
        if (onFileSelected != null) {
            onFileSelected.accept(this.selectedFile);
        }
        return this;
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }
    
    public AutreFilePicker setCurrentDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            this.currentDirectory = directory;
            refreshDirectory();
            scrollOffset = 0f;
            selectedIndex = -1;
        }
        return this;
    }
    
    public AutreFilePicker setFileFilter(String filter) {
        this.fileFilter = filter != null ? filter : "*";
        refreshDirectory();
        return this;
    }
    
    public AutreFilePicker setDirectoryMode(boolean directoryMode) {
        this.directoryMode = directoryMode;
        refreshDirectory();
        return this;
    }
    
    public AutreFilePicker setOnFileSelected(Consumer<File> callback) {
        this.onFileSelected = callback;
        return this;
    }
    
    private void refreshDirectory() {
        currentFiles.clear();
        
        if (currentDirectory == null || !currentDirectory.exists()) {
            return;
        }
        
        // Add parent directory entry (if not root)
        if (currentDirectory.getParentFile() != null) {
            currentFiles.add(new File(currentDirectory, ".."));
        }
        
        // Add directory contents
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            Arrays.sort(files, (a, b) -> {
                // Directories first, then files
                if (a.isDirectory() != b.isDirectory()) {
                    return a.isDirectory() ? -1 : 1;
                }
                return a.getName().compareToIgnoreCase(b.getName());
            });
            
            for (File file : files) {
                if (directoryMode) {
                    // In directory mode, only show directories
                    if (file.isDirectory()) {
                        currentFiles.add(file);
                    }
                } else {
                    // Show all directories and matching files
                    if (file.isDirectory() || matchesFilter(file)) {
                        currentFiles.add(file);
                    }
                }
            }
        }
    }
    
    private boolean matchesFilter(File file) {
        if (fileFilter.equals("*")) return true;
        
        if (fileFilter.startsWith("*.")) {
            String extension = fileFilter.substring(1); // Remove *
            return file.getName().toLowerCase().endsWith(extension.toLowerCase());
        }
        
        return file.getName().toLowerCase().contains(fileFilter.toLowerCase());
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check click on main file input field
        if (event.x >= absX && event.x <= absX + width &&
            event.y >= absY && event.y <= absY + height) {
            isOpen = !isOpen;
            return;
        }
        
        // Handle browser interactions when open
        if (isOpen) {
            float browserX = absX;
            float browserY = absY + height + 2f;
            
            if (event.x >= browserX && event.x <= browserX + browserWidth &&
                event.y >= browserY && event.y <= browserY + browserHeight) {
                
                handleBrowserClick(event.x - browserX, event.y - browserY);
                return;
            }
        }
        
        // Click outside - close browser
        if (isOpen) {
            isOpen = false;
        }
    }
    
    private void handleBrowserClick(float relativeX, float relativeY) {
        // Header area (current path and navigation)
        if (relativeY <= headerHeight) {
            // Handle "Up" button or path navigation
            float upButtonWidth = 30f;
            if (relativeX <= upButtonWidth && currentDirectory.getParentFile() != null) {
                setCurrentDirectory(currentDirectory.getParentFile());
            }
            return;
        }
        
        // File list area
        float listStartY = headerHeight;
        float listHeight = browserHeight - headerHeight;
        
        if (relativeY >= listStartY && relativeY < browserHeight) {
            int clickedIndex = (int) ((relativeY - listStartY + scrollOffset) / itemHeight);
            
            if (clickedIndex >= 0 && clickedIndex < currentFiles.size()) {
                File clickedFile = currentFiles.get(clickedIndex);
                
                if (clickedFile.getName().equals("..")) {
                    // Navigate to parent directory
                    setCurrentDirectory(currentDirectory.getParentFile());
                } else if (clickedFile.isDirectory()) {
                    if (directoryMode) {
                        // Select directory
                        selectedFile = clickedFile;
                        selectedIndex = clickedIndex;
                        if (onFileSelected != null) {
                            onFileSelected.accept(selectedFile);
                        }
                    } else {
                        // Navigate into directory
                        setCurrentDirectory(clickedFile);
                    }
                } else {
                    // Select file
                    selectedFile = clickedFile;
                    selectedIndex = clickedIndex;
                    isOpen = false;
                    
                    if (onFileSelected != null) {
                        onFileSelected.accept(selectedFile);
                    }
                }
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Main file input field
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, backgroundColor);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, borderColor);
        
        // File path text
        String displayText = selectedFile != null ? selectedFile.getName() : 
                            (directoryMode ? "Select folder..." : "Select file...");
        AutreRenderer2.Color currentTextColor = selectedFile != null ? textColor : placeholderColor;
        
        float textX = absX + 6f;
        float textY = absY + (height - mc.textRenderer.fontHeight) / 2f;
        
        // Clip text if too long
        float availableWidth = width - 30f;
        String clippedText = clipTextToWidth(displayText, availableWidth);
        
        AutreRenderer2.drawText(context, clippedText, textX, textY, currentTextColor, false);
        
        // Folder/file icon
        String icon = directoryMode ? "📁" : "📄";
        float iconX = absX + width - 20f;
        AutreRenderer2.drawText(context, icon, iconX, textY, textColor, false);
        
        // File browser popup
        if (isOpen) {
            renderFileBrowser(context, absX, absY + height + 2f, mouseX, mouseY);
        }
    }
    
    private void renderFileBrowser(DrawContext context, float browserX, float browserY, float mouseX, float mouseY) {
        // Browser background
        AutreRenderer2.fillRect(context.getMatrices(),
            browserX, browserY, browserWidth, browserHeight, browserBg);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            browserX, browserY, browserWidth, browserHeight, 1f, borderColor);
        
        // Header with current path and up button
        AutreRenderer2.fillRect(context.getMatrices(),
            browserX, browserY, browserWidth, headerHeight, headerBg);
        
        // Up button
        if (currentDirectory.getParentFile() != null) {
            AutreRenderer2.drawText(context, "↑", browserX + 8f,
                browserY + (headerHeight - mc.textRenderer.fontHeight) / 2f, textColor, false);
        }
        
        // Current path
        String pathText = currentDirectory.getName();
        if (pathText.isEmpty()) pathText = currentDirectory.getAbsolutePath();
        
        float pathX = browserX + 25f;
        float pathY = browserY + (headerHeight - mc.textRenderer.fontHeight) / 2f;
        
        // Clip path text
        float availablePathWidth = browserWidth - 30f;
        String clippedPath = clipTextToWidth(pathText, availablePathWidth);
        
        AutreRenderer2.drawText(context, clippedPath, pathX, pathY, textColor, false);
        
        // File list
        float listStartY = browserY + headerHeight;
        float listHeight = browserHeight - headerHeight;
        int visibleItems = (int) (listHeight / itemHeight);
        
        for (int i = 0; i < Math.min(visibleItems, currentFiles.size()); i++) {
            int fileIndex = i + (int) (scrollOffset / itemHeight);
            if (fileIndex >= currentFiles.size()) break;
            
            File file = currentFiles.get(fileIndex);
            float itemY = listStartY + i * itemHeight;
            
            boolean isHovered = mouseX >= browserX && mouseX <= browserX + browserWidth &&
                              mouseY >= itemY && mouseY <= itemY + itemHeight;
            boolean isSelected = fileIndex == selectedIndex;
            
            // Item background
            if (isSelected) {
                AutreRenderer2.fillRect(context.getMatrices(),
                    browserX, itemY, browserWidth, itemHeight, selectedBg);
            } else if (isHovered) {
                AutreRenderer2.fillRect(context.getMatrices(),
                    browserX, itemY, browserWidth, itemHeight, hoverBg);
            }
            
            // File icon and name
            String fileName = file.getName();
            AutreRenderer2.Color nameColor = fileColor;
            String icon = "📄";
            
            if (file.getName().equals("..")) {
                icon = "↖️";
                fileName = "..";
            } else if (file.isDirectory()) {
                icon = "📁";
                nameColor = folderColor;
            }
            
            float iconX = browserX + 4f;
            float nameX = iconX + 16f;
            float itemTextY = itemY + (itemHeight - mc.textRenderer.fontHeight) / 2f;
            
            AutreRenderer2.drawText(context, icon, iconX, itemTextY, nameColor, false);
            
            // Clip filename
            float availableNameWidth = browserWidth - 24f;
            String clippedName = clipTextToWidth(fileName, availableNameWidth);
            
            AutreRenderer2.drawText(context, clippedName, nameX, itemTextY, nameColor, false);
        }
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
}