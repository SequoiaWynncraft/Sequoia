/**
 * Emoji picker functionality for Sequoia2 chat
 * Provides search, selection, and category navigation
 */

(function() {
    // Track state
    let isEmojiPickerVisible = false;
    let currentCategory = 'frequently-used';
    let selectedEmoji = null;
    let recentlyUsedEmojis = [];
    const MAX_RECENT_EMOJIS = 16;
    
    // DOM Elements references (will be initialized when document is ready)
    let emojiPickerElement;
    let emojiSearchInput;
    let emojiPreview;
    let emojiPreviewName;
    let emojiPreviewCharacter;
    let emojiCategoryNav;
    let emojiResultsContainer;
    
    // Initialize the emoji picker
    function initEmojiPicker() {
        // Get DOM references
        emojiPickerElement = document.getElementById('emoji-picker');
        emojiSearchInput = document.getElementById('emoji-search');
        emojiPreview = document.getElementById('emoji-preview');
        emojiPreviewName = document.getElementById('emoji-preview-name');
        emojiPreviewCharacter = document.getElementById('emoji-preview-emoji');
        emojiCategoryNav = document.getElementById('emoji-category-nav');
        emojiResultsContainer = document.getElementById('emoji-results');
        
        // Tab elements
        const tabButtons = document.querySelectorAll('.emoji-picker-tab');
        const emojiContent = document.querySelector('.emoji-picker-content');
        const stickersContent = document.querySelector('.stickers-content');
        const gifsContent = document.querySelector('.gifs-content');
        
        // Setup tab switching
        tabButtons.forEach(tab => {
            tab.addEventListener('click', () => {
                const tabType = tab.getAttribute('data-tab');
                
                // Update active tab button
                tabButtons.forEach(btn => btn.classList.remove('active'));
                tab.classList.add('active');
                
                // Show/hide content based on tab
                emojiContent.style.display = tabType === 'emoji' ? 'flex' : 'none';
                stickersContent.style.display = tabType === 'stickers' ? 'flex' : 'none';
                gifsContent.style.display = tabType === 'gifs' ? 'flex' : 'none';
                
                // If switching to emoji tab, ensure emoji grid is displayed correctly
                if (tabType === 'emoji') {
                    loadEmojiCategory(currentCategory);
                }
            });
        });
        
        // Set up event listeners
        emojiSearchInput.addEventListener('input', handleEmojiSearch);
        
        // Set up category navigation
        setupCategoryNavigation();
        
        // Load initial emojis
        loadEmojiCategory(currentCategory);
        
        // Load any saved recently used emojis from localStorage
        loadRecentlyUsedEmojis();
    }
    
    // Setup category navigation
    function setupCategoryNavigation() {
        // Create category buttons
        const categories = [
            { id: 'frequently-used', icon: '🕒', name: 'Recent' },
            { id: 'smileys', icon: '😀', name: 'Smileys' },
            { id: 'people', icon: '👋', name: 'People' },
            { id: 'nature', icon: '🐶', name: 'Animals' },
            { id: 'food', icon: '🍎', name: 'Food' },
            { id: 'activities', icon: '⚽', name: 'Activities' },
            { id: 'travel', icon: '🚗', name: 'Travel' },
            { id: 'objects', icon: '💡', name: 'Objects' },
            { id: 'symbols', icon: '❤️', name: 'Symbols' },
            { id: 'flags', icon: '🏁', name: 'Flags' }
        ];
        
        // Clear existing buttons
        emojiCategoryNav.innerHTML = '';
        
        // Add category buttons
        categories.forEach(category => {
            const categoryButton = document.createElement('button');
            categoryButton.className = 'emoji-category-btn';
            categoryButton.title = category.name;
            categoryButton.setAttribute('data-category', category.id);
            
            // Safely use twemoji if it's available
            if (typeof twemoji !== 'undefined') {
                categoryButton.innerHTML = twemoji.parse(category.icon);
            } else {
                categoryButton.innerHTML = category.icon;
                console.warn('Twemoji not loaded, using plain emoji');
            }
            
            if (category.id === currentCategory) {
                categoryButton.classList.add('active');
            }
            
            categoryButton.addEventListener('click', () => {
                // Remove active class from all buttons
                document.querySelectorAll('.emoji-category-btn').forEach(btn => {
                    btn.classList.remove('active');
                });
                
                // Add active class to clicked button
                categoryButton.classList.add('active');
                
                // Load emojis for this category
                currentCategory = category.id;
                loadEmojiCategory(currentCategory);
                
                // Clear search input
                emojiSearchInput.value = '';
            });
            
            emojiCategoryNav.appendChild(categoryButton);
        });
    }
    
    // Load emojis for a specific category
    function loadEmojiCategory(categoryId) {
        // Use our emoji data
        const emojisToDisplay = emojiData[categoryId] || [];
        
        // Special case for "frequently-used" - use our stored recents if available
        if (categoryId === 'frequently-used' && recentlyUsedEmojis.length > 0) {
            renderEmojiResults(recentlyUsedEmojis);
        } else {
            renderEmojiResults(emojisToDisplay);
        }
    }
    
    // Handle emoji search
    function handleEmojiSearch(event) {
        const query = event.target.value.trim();
        
        if (!query) {
            // If search is cleared, show current category
            loadEmojiCategory(currentCategory);
            return;
        }
        
        // Search emojis
        const results = searchEmojis(query);
        renderEmojiResults(results);
    }
    
    // Render emoji search/category results
    function renderEmojiResults(emojis) {
        // Clear existing results
        emojiResultsContainer.innerHTML = '';
        
        if (emojis.length === 0) {
            const noResults = document.createElement('div');
            noResults.className = 'emoji-no-results';
            noResults.textContent = 'No emojis found';
            emojiResultsContainer.appendChild(noResults);
            return;
        }
        
        // Create and append emoji buttons
        emojis.forEach(emoji => {
            const emojiButton = document.createElement('button');
            emojiButton.className = 'emoji-result-item';
            emojiButton.setAttribute('data-emoji', emoji.emoji);
            emojiButton.setAttribute('data-name', emoji.name);
            emojiButton.setAttribute('data-shortcode', emoji.shortcode);
            emojiButton.title = emoji.name;
            
            // Set inner HTML with Twemoji parsing
            emojiButton.innerHTML = typeof twemoji !== 'undefined' ? twemoji.parse(emoji.emoji) : emoji.emoji;
            
            // Add event listeners
            emojiButton.addEventListener('click', () => {
                selectEmoji(emoji);
            });
            
            emojiButton.addEventListener('mouseover', () => {
                showEmojiPreview(emoji);
            });
            
            emojiResultsContainer.appendChild(emojiButton);
        });
    }
    
    // Show emoji preview - Discord-like animation
    function showEmojiPreview(emoji) {
        // Always show the preview area
        emojiPreview.style.display = 'flex';
        
        // Update content
        emojiPreviewName.textContent = emoji.name;
        
        // Use twemoji if available
        emojiPreviewCharacter.innerHTML = typeof twemoji !== 'undefined' ? 
            twemoji.parse(emoji.emoji) : emoji.emoji;
        
        // Add a subtle animation like Discord
        emojiPreview.style.transition = 'opacity 0.1s ease-out';
        emojiPreview.style.opacity = '0.9';
        void emojiPreview.offsetWidth; // Force reflow
        emojiPreview.style.opacity = '1';
    }
    
    // Hide emoji preview
    function hideEmojiPreview() {
        emojiPreview.style.opacity = '0';
        setTimeout(() => {
            emojiPreview.style.display = 'none';
        }, 100);
    }
    
    // Select an emoji (for insertion)
    function selectEmoji(emoji) {
        // Add to recently used
        addToRecentlyUsed(emoji);
        
        // Hide the picker
        toggleEmojiPicker(false);
        
        // Send the emoji to the input field or message
        insertEmojiIntoMessage(emoji.emoji);
    }
    
    // Add emoji to recently used
    function addToRecentlyUsed(emoji) {
        // Remove emoji if it already exists in the recently used list
        recentlyUsedEmojis = recentlyUsedEmojis.filter(item => item.emoji !== emoji.emoji);
        
        // Add emoji to the beginning of the list
        recentlyUsedEmojis.unshift(emoji);
        
        // Limit to MAX_RECENT_EMOJIS
        if (recentlyUsedEmojis.length > MAX_RECENT_EMOJIS) {
            recentlyUsedEmojis = recentlyUsedEmojis.slice(0, MAX_RECENT_EMOJIS);
        }
        
        // Save to localStorage
        saveRecentlyUsedEmojis();
    }
    
    // Save recently used emojis to localStorage
    function saveRecentlyUsedEmojis() {
        try {
            localStorage.setItem('sequoia2_recent_emojis', JSON.stringify(recentlyUsedEmojis));
        } catch (e) {
            console.error('Failed to save recent emojis:', e);
        }
    }
    
    // Load recently used emojis from localStorage
    function loadRecentlyUsedEmojis() {
        try {
            const saved = localStorage.getItem('sequoia2_recent_emojis');
            if (saved) {
                recentlyUsedEmojis = JSON.parse(saved);
            }
        } catch (e) {
            console.error('Failed to load recent emojis:', e);
            recentlyUsedEmojis = [];
        }
    }
    
    // Toggle the emoji picker visibility with Discord-like animation
    function toggleEmojiPicker(show) {
        isEmojiPickerVisible = show !== undefined ? show : !isEmojiPickerVisible;
        
        if (isEmojiPickerVisible) {
            // First set to flex but with opacity 0
            emojiPickerElement.style.display = 'flex';
            emojiPickerElement.style.opacity = '0';
            emojiPickerElement.style.transform = 'translateY(10px) scale(0.98)';
            emojiPickerElement.style.transition = 'opacity 0.15s ease-out, transform 0.15s ease-out';
            
            // Position the picker
            positionEmojiPicker();
            
            // Force a reflow to ensure the transition works
            void emojiPickerElement.offsetWidth;
            
            // Then fade it in
            emojiPickerElement.style.opacity = '1';
            emojiPickerElement.style.transform = 'translateY(0) scale(1)';
            
            // Focus the search input
            emojiSearchInput.focus();
            
            // Change picker mode to normal (not reaction)
            emojiPickerElement.setAttribute('data-mode', 'normal');
        } else {
            // Fade out
            emojiPickerElement.style.opacity = '0';
            emojiPickerElement.style.transform = 'translateY(10px) scale(0.98)';
            
            // Hide after animation completes
            setTimeout(() => {
                emojiPickerElement.style.display = 'none';
                hideEmojiPreview();
            }, 150); // Match the transition duration
        }
    }
    
    // Position the emoji picker relative to the current message input - Discord style
    function positionEmojiPicker() {
        // Get the current mode to determine which button triggered the picker
        const mode = emojiPickerElement.getAttribute('data-mode') || 'normal';
        let triggerButton;
        
        if (mode === 'reaction') {
            // This is for reactions, find the reaction button
            triggerButton = document.querySelector('.message-action-emoji');
        } else {
            // This is for normal emoji insertion, use the emoji button in the chat input
            triggerButton = document.getElementById('emojiBtn');
        }
        
        if (triggerButton && emojiPickerElement) {
            const rect = triggerButton.getBoundingClientRect();
            const pickerHeight = 420; // Updated height
            const pickerWidth = 400;  // Updated width to match Discord
            
            // Calculate vertical position - Discord typically places above
            let topPosition;
            
            // For reactions, Discord places it above the message
            if (mode === 'reaction') {
                // Try to position above first (preferred for reactions)
                if (rect.top > pickerHeight + 10) { 
                    topPosition = rect.top - pickerHeight - 8; // Position above with small gap
                } else {
                    // Not enough space above, position below
                    topPosition = rect.bottom + 8;
                }
            } else {
                // For normal emoji insertion, Discord places above the input field
                if (rect.top > pickerHeight + 10) {
                    topPosition = rect.top - pickerHeight - 8; // Position above with small gap
                } else {
                    // Not enough space above, position below
                    topPosition = rect.bottom + 8;
                }
            }
            
            emojiPickerElement.style.top = `${topPosition}px`;
            emojiPickerElement.style.bottom = 'auto'; // Ensure bottom is not set
            
            // Calculate horizontal position
            // For normal mode, Discord aligns the right edge of the picker with the emoji button
            let leftPosition;
            
            if (mode === 'normal') {
                leftPosition = rect.right - pickerWidth; // Align right edge with button
            } else {
                // For reaction mode, Discord centers it on the reaction button
                leftPosition = rect.left - (pickerWidth / 2) + (rect.width / 2);
            }
            
            // Make sure it doesn't go off the left edge
            if (leftPosition < 12) {
                leftPosition = 12;
            }
            
            // Make sure it doesn't go off the right edge
            if (leftPosition + pickerWidth > window.innerWidth - 12) {
                leftPosition = window.innerWidth - pickerWidth - 12;
            }
            
            // Apply with transition for smooth animation
            emojiPickerElement.style.transition = 'opacity 0.15s ease-out';
            emojiPickerElement.style.left = `${leftPosition}px`;
            emojiPickerElement.style.right = 'auto'; // Clear right if it was set
        } else {
            // Fallback positioning if button not found (center in screen)
            const pickerHeight = 420;
            const pickerWidth = 400;
            
            emojiPickerElement.style.top = `${Math.max(12, (window.innerHeight - pickerHeight) / 2)}px`;
            emojiPickerElement.style.left = `${Math.max(12, (window.innerWidth - pickerWidth) / 2)}px`;
            emojiPickerElement.style.bottom = 'auto';
            emojiPickerElement.style.right = 'auto';
        }
    }
    
    // Insert emoji into message input
    function insertEmojiIntoMessage(emojiChar) {
        // First try to get the chatInput element directly
        let messageInput = document.getElementById('chatInput');
        
        // If that doesn't work, try to get it through the chat app
        if (!messageInput && window.chatApp) {
            messageInput = window.chatApp.chatInput;
        }
        
        // If we still don't have the input element, log error and return
        if (!messageInput) {
            console.error('Could not find chat input element with ID "chatInput"');
            return;
        }
        
        // Get cursor position
        const cursorPosition = messageInput.selectionStart;
        
        // Insert emoji at cursor position
        const currentValue = messageInput.value;
        messageInput.value = currentValue.slice(0, cursorPosition) + emojiChar + currentValue.slice(cursorPosition);
        
        // Move cursor after inserted emoji
        messageInput.selectionStart = cursorPosition + emojiChar.length;
        messageInput.selectionEnd = cursorPosition + emojiChar.length;
        
        // Focus back on input
        messageInput.focus();
        
        // Trigger input event to adjust textarea height
        const inputEvent = new Event('input', { bubbles: true });
        messageInput.dispatchEvent(inputEvent);
        
        // Also try to call the chatApp's resize function if available
        if (window.chatApp && window.chatApp.resizeInput) {
            window.chatApp.resizeInput();
        }
    }
    
    // Add emoji reaction to a message
    function addEmojiReaction(messageId, emojiChar) {
        const message = document.querySelector(`.message[data-message-id="${messageId}"]`);
        if (!message) return;
        
        // Check if reactions container exists
        let reactionsContainer = message.querySelector('.message-reactions');
        if (!reactionsContainer) {
            // Create reactions container if it doesn't exist
            reactionsContainer = document.createElement('div');
            reactionsContainer.className = 'message-reactions';
            message.appendChild(reactionsContainer);
        }
        
        // Check if this reaction already exists
        const existingReaction = reactionsContainer.querySelector(`.reaction[data-emoji="${emojiChar}"]`);
        
        // If the reaction exists, just increment it
        if (existingReaction) {
            // Increment count
            const countElement = existingReaction.querySelector('.reaction-count');
            let count = parseInt(countElement.textContent);
            countElement.textContent = (count + 1).toString();
            existingReaction.classList.add('user-reacted');
            existingReaction.setAttribute('data-count', count + 1);
            return; // We're done since we're just adding to an existing reaction
        }
        
        // Get all existing reactions
        const existingReactions = reactionsContainer.querySelectorAll('.reaction');
        
        // If we already have 5 different reactions and trying to add a new one, don't allow it
        if (existingReactions.length >= 5) {
            // Show a toast notification using the global function
            window.showReactionLimitToast(message);
            return;
        }
        
        // Create new reaction
        const reaction = document.createElement('div');
        reaction.className = 'reaction user-reacted';
        reaction.setAttribute('data-emoji', emojiChar);
        reaction.setAttribute('data-count', '1');
        reaction.innerHTML = `
            <span class="reaction-emoji">${typeof twemoji !== 'undefined' ? twemoji.parse(emojiChar) : emojiChar}</span>
            <span class="reaction-count">1</span>
        `;
        
        // Add click handler for toggling reaction
        reaction.addEventListener('click', function() {
            toggleReaction(messageId, emojiChar);
        });
        
        reactionsContainer.appendChild(reaction);
    }
    
    // Toggle emoji reaction (add/remove)
    function toggleReaction(messageId, emojiChar) {
        const message = document.querySelector(`.message[data-message-id="${messageId}"]`);
        if (!message) return;
        
        const reactionsContainer = message.querySelector('.message-reactions');
        if (!reactionsContainer) return;
        
        const reaction = reactionsContainer.querySelector(`.reaction[data-emoji="${emojiChar}"]`);
        if (!reaction) return;
        
        const countElement = reaction.querySelector('.reaction-count');
        let count = parseInt(countElement.textContent);
        
        if (reaction.classList.contains('user-reacted')) {
            // Remove user's reaction
            reaction.classList.remove('user-reacted');
            count--;
            
            if (count <= 0) {
                // Remove reaction element if count reaches 0
                reactionsContainer.removeChild(reaction);
                
                // Remove reactions container if empty
                if (reactionsContainer.children.length === 0) {
                    message.removeChild(reactionsContainer);
                }
            } else {
                countElement.textContent = count.toString();
            }
        } else {
            // Add user's reaction
            reaction.classList.add('user-reacted');
            count++;
            countElement.textContent = count.toString();
        }
    }
    
    // Setup emoji buttons on message actions
    function setupMessageEmojiButtons() {
        document.querySelectorAll('.message-action-emoji').forEach(button => {
            button.addEventListener('click', (event) => {
                event.stopPropagation();
                
                const messageId = button.closest('.message').getAttribute('data-message-id');
                openEmojiPickerForReaction(messageId, button);
            });
        });
    }
    
    // Open emoji picker for reaction selection - Discord-like positioning
    function openEmojiPickerForReaction(messageId, triggerElement) {
        // Save current target message
        emojiPickerElement.setAttribute('data-target-message', messageId);
        
        // Mark this as reaction mode
        emojiPickerElement.setAttribute('data-mode', 'reaction');
        
        // Show with animation
        emojiPickerElement.style.display = 'flex';
        emojiPickerElement.style.opacity = '0';
        emojiPickerElement.style.transform = 'translateY(10px) scale(0.98)';
        emojiPickerElement.style.transition = 'opacity 0.15s ease-out, transform 0.15s ease-out';
        
        // Position the picker using our updated positioning function
        positionEmojiPicker();
        
        // Force a reflow to ensure the transition works
        void emojiPickerElement.offsetWidth;
        
        // Then fade it in
        emojiPickerElement.style.opacity = '1';
        emojiPickerElement.style.transform = 'translateY(0) scale(1)';
        
        // Focus the search input
        emojiSearchInput.focus();
        
        // Update state
        isEmojiPickerVisible = true;
        
        // Make sure it doesn't go off the left edge
        if (leftPosition < 10) {
            leftPosition = 10;
        }
        
        // Make sure it doesn't go off the right edge
        if (leftPosition + pickerWidth > window.innerWidth - 10) {
            leftPosition = window.innerWidth - pickerWidth - 10;
        }
        
        emojiPickerElement.style.left = `${leftPosition}px`;
        emojiPickerElement.style.right = 'auto'; // Clear right if it was set
        
        // Show the picker
        toggleEmojiPicker(true);
        
        // Change picker mode for reactions
        emojiPickerElement.setAttribute('data-mode', 'reaction');
        
        // Override the selectEmoji function temporarily
        const originalSelectEmoji = selectEmoji;
        selectEmoji = function(emoji) {
            // Add reaction to the message
            addEmojiReaction(messageId, emoji.emoji);
            
            // Add to recently used
            addToRecentlyUsed(emoji);
            
            // Hide the picker
            toggleEmojiPicker(false);
            
            // Restore original function
            selectEmoji = originalSelectEmoji;
            
            // Reset picker mode
            emojiPickerElement.removeAttribute('data-mode');
            emojiPickerElement.removeAttribute('data-target-message');
        };
    }
    
    // Event handler for emoji picker close button
    function handleEmojiPickerClose() {
        toggleEmojiPicker(false);
        
        // Reset any temporary overrides
        selectEmoji = window.originalSelectEmoji || selectEmoji;
        
        // Reset picker mode
        emojiPickerElement.removeAttribute('data-mode');
        emojiPickerElement.removeAttribute('data-target-message');
    }
    
    // Export functions to global scope
    window.emojiPicker = {
        init: initEmojiPicker,
        toggle: toggleEmojiPicker,
        addReaction: addEmojiReaction,
        toggleReaction: toggleReaction,
        setupMessageButtons: setupMessageEmojiButtons,
        get emojiPickerElement() { return emojiPickerElement; }
    };
    
    // Show toast notification when reaction limit is reached
    function showReactionLimitToast(messageElement) {
        // Create toast element if it doesn't exist
        let toast = document.getElementById('reaction-limit-toast');
        if (!toast) {
            toast = document.createElement('div');
            toast.id = 'reaction-limit-toast';
            toast.className = 'reaction-limit-toast';
            toast.textContent = 'Maximum of 5 reactions per message reached';
            document.body.appendChild(toast);
            
            // Add style if not already in the document
            if (!document.getElementById('reaction-limit-toast-style')) {
                const style = document.createElement('style');
                style.id = 'reaction-limit-toast-style';
                style.textContent = `
                    .reaction-limit-toast {
                        position: fixed;
                        bottom: 20px;
                        left: 50%;
                        transform: translateX(-50%);
                        background: rgba(0, 0, 0, 0.8);
                        color: white;
                        padding: 8px 16px;
                        border-radius: 20px;
                        font-size: 14px;
                        z-index: 10000;
                        opacity: 0;
                        transition: opacity 0.3s ease;
                        pointer-events: none;
                    }
                    .reaction-limit-toast.visible {
                        opacity: 1;
                    }
                `;
                document.head.appendChild(style);
            }
        }
        
        // Show and then hide the toast
        toast.classList.add('visible');
        setTimeout(() => {
            toast.classList.remove('visible');
        }, 2000);
    }
    
    // Make showReactionLimitToast available globally
    window.showReactionLimitToast = showReactionLimitToast;
    
    // Initialize stickers placeholder
    function initStickersPlaceholder() {
        const stickersGrid = document.querySelector('.stickers-grid');
        if (stickersGrid) {
            const placeholder = document.createElement('div');
            placeholder.className = 'placeholder-message';
            placeholder.innerHTML = `
                <div class="placeholder-icon">🎨</div>
                <div class="placeholder-text">
                    <h3>Stickers Coming Soon</h3>
                    <p>This feature is currently in development. Check back later for custom stickers!</p>
                </div>
            `;
            stickersGrid.appendChild(placeholder);
        }
    }
    
    // Initialize GIFs placeholder
    function initGifsPlaceholder() {
        const gifsGrid = document.querySelector('.gifs-grid');
        if (gifsGrid) {
            const placeholder = document.createElement('div');
            placeholder.className = 'placeholder-message';
            placeholder.innerHTML = `
                <div class="placeholder-icon">🎬</div>
                <div class="placeholder-text">
                    <h3>GIFs Coming Soon</h3>
                    <p>This feature is currently in development. GIF support will be added in a future update!</p>
                </div>
            `;
            gifsGrid.appendChild(placeholder);
        }
    }
    
    // Initialize on DOMContentLoaded - with added logging
    document.addEventListener('DOMContentLoaded', () => {
        initEmojiPicker();
        initStickersPlaceholder(); 
        initGifsPlaceholder();
        
        // Setup emoji button in message input
        const emojiButton = document.querySelector('.emoji-button');
        if (emojiButton) {
            emojiButton.addEventListener('click', (event) => {
                event.stopPropagation();
                toggleEmojiPicker();
            });
        }
        
        // Setup close button
        const closeButton = document.getElementById('emoji-picker-close');
        if (closeButton) {
            closeButton.addEventListener('click', handleEmojiPickerClose);
        }
        
        // Close picker when clicking outside
        document.addEventListener('click', (event) => {
            if (isEmojiPickerVisible && !emojiPickerElement.contains(event.target) && 
                !event.target.classList.contains('emoji-button')) {
                toggleEmojiPicker(false);
            }
        });
        
        // Close picker when Escape key is pressed - Discord-like behavior
        document.addEventListener('keydown', (event) => {
            if (isEmojiPickerVisible && event.key === 'Escape') {
                event.preventDefault();
                toggleEmojiPicker(false);
            }
        });
        
        // Setup existing message emoji buttons
        setupMessageEmojiButtons();
        
        console.log('EmojiPicker initialized successfully');
        if (typeof twemoji === 'undefined') {
            console.warn('Warning: Twemoji not loaded, emoji will display as native characters');
        }
    });
    
    // Also initialize immediately if document is already loaded
    if (document.readyState === 'complete' || document.readyState === 'interactive') {
        setTimeout(() => {
            initEmojiPicker();
            initStickersPlaceholder();
            initGifsPlaceholder();
            console.log('EmojiPicker initialized via readyState check');
        }, 1);
    }
    
    // Expose emoji picker functions to the global scope for chat integration
    window.emojiPicker = {
        toggle: toggleEmojiPicker,
        insert: insertEmojiIntoMessage,
        select: selectEmoji,
        init: initEmojiPicker
    };
})(); // End of IIFE
