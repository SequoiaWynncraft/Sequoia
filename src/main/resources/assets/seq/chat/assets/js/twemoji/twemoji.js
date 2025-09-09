/**
 * Simplified Twemoji implementation for Sequoia2 chat
 * Based on Twitter's Twemoji but with CDN support
 */
/**
 * Simplified Twemoji implementation for Sequoia2 chat
 * Based on Twitter's Twemoji but with CDN support
 */
(function(global) {
  
  // CDN base URL for Twitter Emoji
  const base = 'https://cdn.jsdelivr.net/gh/twitter/twemoji@latest/assets/';
  
  /**
   * Parse text or DOM nodes and replace emoji with images
   * @param {string|HTMLElement} what - Text or DOM node to parse
   * @param {object} options - Optional configuration settings
   * @returns {string|HTMLElement} - Parsed result
   */
  function parse(what, options) {
    options = options || {};
    
    if (typeof what === 'string') {
      return parseString(what, options);
    } else if (what instanceof HTMLElement) {
      parseNode(what, options);
      return what;
    }
    return what;
  }
  
  /**
   * Parse a string and replace emoji with image tags
   * @param {string} text - Text to parse
   * @param {object} options - Configuration options
   * @returns {string} - HTML string with emoji replaced by images
   */
  function parseString(text, options) {
    return text.replace(/[\u{1F300}-\u{1F6FF}\u{1F900}-\u{1F9FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}\u{1F100}-\u{1F1FF}\u{1F680}-\u{1F6FF}]/gu, function(match) {
      const codePoint = toCodePoint(match);
      return toImage(codePoint, options);
    });
  }
  
  /**
   * Parse a DOM node and replace emoji with images
   * @param {HTMLElement} node - Node to parse
   * @param {object} options - Configuration options
   */
  function parseNode(node, options) {
    const nodeName = node.nodeName.toLowerCase();
    
    if (nodeName === 'textarea' || nodeName === 'input') {
      return; // Skip form elements
    }
    
    // Process only text nodes
    for (let i = 0; i < node.childNodes.length; i++) {
      const childNode = node.childNodes[i];
      
      if (childNode.nodeType === 3) { // Text node
        const text = childNode.nodeValue;
        const newText = parseString(text, options);
        
        if (text !== newText) {
          const temp = document.createElement('div');
          temp.innerHTML = newText;
          
          // Replace the text node with parsed content
          while (temp.firstChild) {
            node.insertBefore(temp.firstChild, childNode);
          }
          
          node.removeChild(childNode);
          i--; // Adjust index since DOM has changed
        }
      } else if (childNode.nodeType === 1) { // Element node
        parseNode(childNode, options);
      }
    }
  }
  
  /**
   * Convert emoji to its code point representation
   * @param {string} emoji - Single emoji character
   * @returns {string} - Hexadecimal code point
   */
  function toCodePoint(emoji) {
    let codePoint = '';
    
    // Convert emoji to code points
    for (let i = 0; i < emoji.length; i++) {
      const char = emoji.charCodeAt(i);
      
      if (char >= 0xD800 && char <= 0xDBFF) { // High surrogate
        const low = emoji.charCodeAt(i + 1);
        if (low >= 0xDC00 && low <= 0xDFFF) { // Low surrogate
          // Calculate code point from surrogate pair
          const highShift = (char - 0xD800) << 10;
          const lowShift = low - 0xDC00;
          const combined = 0x10000 + highShift + lowShift;
          codePoint += combined.toString(16);
          i++; // Skip the next character as we've already processed it
        } else {
          codePoint += char.toString(16);
        }
      } else {
        codePoint += char.toString(16);
      }
    }
    
    return codePoint;
  }
  
  /**
   * Convert code point to an image tag
   * @param {string} codePoint - Emoji code point in hex
   * @param {object} options - Configuration options
   * @returns {string} - HTML image tag
   */
  function toImage(codePoint, options) {
    const size = options.size || 72;
    const ext = options.ext || '.png';
    const className = options.className || 'emoji';
    
    const imgSrc = base + 'svg/' + codePoint + '.svg';
    
    return '<img class="' + className + '" ' +
           'src="' + imgSrc + '" ' +
           'alt="' + codePoint + '" ' +
           'width="' + size + '" ' +
           'height="' + size + '">';
  }
  
  // Export parse function
  global.twemoji = {
    parse: parse
  };
  
})(window);
