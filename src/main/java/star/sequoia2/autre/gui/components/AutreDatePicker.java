package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.time.LocalDate;

import static star.sequoia2.client.SeqClient.mc;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.function.Consumer;

/**
 * Date picker with calendar popup - flat design
 */
public class AutreDatePicker extends AutreComponent {
    protected LocalDate selectedDate = LocalDate.now();
    protected LocalDate displayedMonth = LocalDate.now().withDayOfMonth(1);
    protected boolean isOpen = false;
    protected String dateFormat = "yyyy-MM-dd";
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    
    // Calendar dimensions
    protected float calendarWidth = 200f;
    protected float calendarHeight = 180f;
    protected float cellSize = 24f;
    protected float headerHeight = 30f;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color calendarBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color headerBg = AutreRenderer2.Color.BACKGROUND;
    protected AutreRenderer2.Color selectedColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color todayColor = AutreRenderer2.Color.getAccent().withAlpha(0.3f);
    protected AutreRenderer2.Color hoverColor = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color otherMonthColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    
    protected Consumer<LocalDate> onDateChange;
    
    public AutreDatePicker(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreDatePicker setDate(LocalDate date) {
        this.selectedDate = date != null ? date : LocalDate.now();
        this.displayedMonth = this.selectedDate.withDayOfMonth(1);
        if (onDateChange != null) {
            onDateChange.accept(this.selectedDate);
        }
        return this;
    }
    
    public LocalDate getDate() {
        return selectedDate;
    }
    
    public AutreDatePicker setDateFormat(String format) {
        this.dateFormat = format;
        this.formatter = DateTimeFormatter.ofPattern(format);
        return this;
    }
    
    public AutreDatePicker setOnDateChange(Consumer<LocalDate> callback) {
        this.onDateChange = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check click on main input field
        if (event.x >= absX && event.x <= absX + width &&
            event.y >= absY && event.y <= absY + height) {
            isOpen = !isOpen;
            return;
        }
        
        // Handle calendar interactions when open
        if (isOpen) {
            float calendarX = absX;
            float calendarY = absY + height + 2f;
            
            if (event.x >= calendarX && event.x <= calendarX + calendarWidth &&
                event.y >= calendarY && event.y <= calendarY + calendarHeight) {
                
                handleCalendarClick(event.x - calendarX, event.y - calendarY);
                return;
            }
        }
        
        // Click outside - close calendar
        if (isOpen) {
            isOpen = false;
        }
    }
    
    private void handleCalendarClick(float relativeX, float relativeY) {
        // Header area (navigation)
        if (relativeY <= headerHeight) {
            float buttonWidth = 30f;
            
            // Previous month button
            if (relativeX <= buttonWidth) {
                displayedMonth = displayedMonth.minusMonths(1);
            }
            // Next month button
            else if (relativeX >= calendarWidth - buttonWidth) {
                displayedMonth = displayedMonth.plusMonths(1);
            }
            return;
        }
        
        // Calendar grid area
        float gridStartY = headerHeight + 20f; // Account for day labels
        if (relativeY >= gridStartY) {
            int col = (int) (relativeX / cellSize);
            int row = (int) ((relativeY - gridStartY) / cellSize);
            
            if (col >= 0 && col < 7 && row >= 0 && row < 6) {
                LocalDate clickedDate = getDateForCell(row, col);
                if (clickedDate != null) {
                    selectedDate = clickedDate;
                    displayedMonth = selectedDate.withDayOfMonth(1);
                    isOpen = false;
                    
                    if (onDateChange != null) {
                        onDateChange.accept(selectedDate);
                    }
                }
            }
        }
    }
    
    private LocalDate getDateForCell(int row, int col) {
        LocalDate firstOfMonth = displayedMonth;
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        
        int dayNumber = row * 7 + col - firstDayOfWeek + 1;
        
        if (dayNumber < 1) {
            // Previous month
            LocalDate prevMonth = firstOfMonth.minusMonths(1);
            int daysInPrevMonth = YearMonth.from(prevMonth).lengthOfMonth();
            return prevMonth.withDayOfMonth(daysInPrevMonth + dayNumber);
        } else if (dayNumber > YearMonth.from(firstOfMonth).lengthOfMonth()) {
            // Next month
            return firstOfMonth.plusMonths(1).withDayOfMonth(dayNumber - YearMonth.from(firstOfMonth).lengthOfMonth());
        } else {
            // Current month
            return firstOfMonth.withDayOfMonth(dayNumber);
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Main input field
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, backgroundColor);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, borderColor);
        
        // Date text
        String dateText = selectedDate.format(formatter);
        float textX = absX + 6f;
        float textY = absY + (height - mc.textRenderer.fontHeight) / 2f;
        
        AutreRenderer2.drawText(context, dateText, textX, textY, textColor, false);
        
        // Calendar icon
        String calendarIcon = "📅";
        float iconX = absX + width - 20f;
        AutreRenderer2.drawText(context, calendarIcon, iconX, textY, textColor, false);
        
        // Calendar popup
        if (isOpen) {
            renderCalendar(context, absX, absY + height + 2f, mouseX, mouseY);
        }
    }
    
    private void renderCalendar(DrawContext context, float calendarX, float calendarY, float mouseX, float mouseY) {
        // Calendar background
        AutreRenderer2.fillRect(context.getMatrices(),
            calendarX, calendarY, calendarWidth, calendarHeight, calendarBg);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            calendarX, calendarY, calendarWidth, calendarHeight, 1f, borderColor);
        
        // Header with month/year and navigation
        AutreRenderer2.fillRect(context.getMatrices(),
            calendarX, calendarY, calendarWidth, headerHeight, headerBg);
        
        String monthYear = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        float headerTextX = calendarX + (calendarWidth - mc.textRenderer.getWidth(monthYear)) / 2f;
        float headerTextY = calendarY + (headerHeight - mc.textRenderer.fontHeight) / 2f;
        
        AutreRenderer2.drawText(context, monthYear, headerTextX, headerTextY, textColor, false);
        
        // Navigation arrows
        AutreRenderer2.drawText(context, "<", calendarX + 8f, headerTextY, textColor, false);
        AutreRenderer2.drawText(context, ">", calendarX + calendarWidth - 16f, headerTextY, textColor, false);
        
        // Day labels
        String[] dayLabels = {"S", "M", "T", "W", "T", "F", "S"};
        float dayLabelY = calendarY + headerHeight + 5f;
        
        for (int i = 0; i < 7; i++) {
            float dayLabelX = calendarX + i * cellSize + (cellSize - mc.textRenderer.getWidth(dayLabels[i])) / 2f;
            AutreRenderer2.drawText(context, dayLabels[i], dayLabelX, dayLabelY, otherMonthColor, false);
        }
        
        // Calendar grid
        float gridStartY = calendarY + headerHeight + 20f;
        LocalDate today = LocalDate.now();
        
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                float cellX = calendarX + col * cellSize;
                float cellY = gridStartY + row * cellSize;
                
                LocalDate cellDate = getDateForCell(row, col);
                if (cellDate == null) continue;
                
                boolean isCurrentMonth = cellDate.getMonth() == displayedMonth.getMonth() &&
                                       cellDate.getYear() == displayedMonth.getYear();
                boolean isSelected = cellDate.equals(selectedDate);
                boolean isToday = cellDate.equals(today);
                boolean isHovered = mouseX >= cellX && mouseX <= cellX + cellSize &&
                                  mouseY >= cellY && mouseY <= cellY + cellSize;
                
                // Cell background
                if (isSelected) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        cellX, cellY, cellSize, cellSize, selectedColor);
                } else if (isToday) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        cellX, cellY, cellSize, cellSize, todayColor);
                } else if (isHovered && isCurrentMonth) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        cellX, cellY, cellSize, cellSize, hoverColor);
                }
                
                // Day number
                String dayText = String.valueOf(cellDate.getDayOfMonth());
                AutreRenderer2.Color dayColor = isCurrentMonth ? textColor : otherMonthColor;
                if (isSelected) {
                    dayColor = AutreRenderer2.Color.WHITE;
                }
                
                float dayTextX = cellX + (cellSize - mc.textRenderer.getWidth(dayText)) / 2f;
                float dayTextY = cellY + (cellSize - mc.textRenderer.fontHeight) / 2f;
                
                AutreRenderer2.drawText(context, dayText, dayTextX, dayTextY, dayColor, false);
            }
        }
    }
}