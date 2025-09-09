/**
 * Emoji data with categories and shortcodes
 * Inspired by Discord's emoji system
 */
const emojiData = {
    // Frequently used (will be dynamically populated based on usage)
    "frequently-used": [],
    
    // Smileys & Emotion
    "smileys": [
        { emoji: "😀", name: "grinning face", shortcode: ":grinning:" },
        { emoji: "😃", name: "grinning face with big eyes", shortcode: ":smiley:" },
        { emoji: "😄", name: "grinning face with smiling eyes", shortcode: ":smile:" },
        { emoji: "😁", name: "beaming face with smiling eyes", shortcode: ":grin:" },
        { emoji: "😆", name: "grinning squinting face", shortcode: ":laughing:" },
        { emoji: "😅", name: "grinning face with sweat", shortcode: ":sweat_smile:" },
        { emoji: "🤣", name: "rolling on the floor laughing", shortcode: ":rofl:" },
        { emoji: "😂", name: "face with tears of joy", shortcode: ":joy:" },
        { emoji: "🙂", name: "slightly smiling face", shortcode: ":slightly_smiling_face:" },
        { emoji: "🙃", name: "upside-down face", shortcode: ":upside_down_face:" },
        { emoji: "😉", name: "winking face", shortcode: ":wink:" },
        { emoji: "😊", name: "smiling face with smiling eyes", shortcode: ":blush:" },
        { emoji: "😇", name: "smiling face with halo", shortcode: ":innocent:" },
        { emoji: "🥰", name: "smiling face with hearts", shortcode: ":smiling_face_with_three_hearts:" },
        { emoji: "😍", name: "smiling face with heart-eyes", shortcode: ":heart_eyes:" },
        { emoji: "🤩", name: "star-struck", shortcode: ":star_struck:" },
        { emoji: "😘", name: "face blowing a kiss", shortcode: ":kissing_heart:" },
        { emoji: "😗", name: "kissing face", shortcode: ":kissing:" },
        { emoji: "☺️", name: "smiling face", shortcode: ":relaxed:" },
        { emoji: "😚", name: "kissing face with closed eyes", shortcode: ":kissing_closed_eyes:" },
        { emoji: "😙", name: "kissing face with smiling eyes", shortcode: ":kissing_smiling_eyes:" },
        { emoji: "🥲", name: "smiling face with tear", shortcode: ":smiling_face_with_tear:" },
        { emoji: "😋", name: "face savoring food", shortcode: ":yum:" },
        { emoji: "😛", name: "face with tongue", shortcode: ":stuck_out_tongue:" },
        { emoji: "😜", name: "winking face with tongue", shortcode: ":stuck_out_tongue_winking_eye:" },
        { emoji: "😝", name: "squinting face with tongue", shortcode: ":stuck_out_tongue_closed_eyes:" },
        { emoji: "🤑", name: "money-mouth face", shortcode: ":money_mouth_face:" },
        { emoji: "🤗", name: "hugging face", shortcode: ":hugs:" },
        { emoji: "🤭", name: "face with hand over mouth", shortcode: ":hand_over_mouth:" },
        { emoji: "🤫", name: "shushing face", shortcode: ":shushing_face:" },
        { emoji: "🤔", name: "thinking face", shortcode: ":thinking:" },
        { emoji: "🤐", name: "zipper-mouth face", shortcode: ":zipper_mouth_face:" },
        { emoji: "🤨", name: "face with raised eyebrow", shortcode: ":raised_eyebrow:" },
        { emoji: "😐", name: "neutral face", shortcode: ":neutral_face:" },
        { emoji: "😑", name: "expressionless face", shortcode: ":expressionless:" },
        { emoji: "😶", name: "face without mouth", shortcode: ":no_mouth:" },
        { emoji: "😶‍🌫️", name: "face in clouds", shortcode: ":face_in_clouds:" },
        { emoji: "😏", name: "smirking face", shortcode: ":smirk:" },
        { emoji: "😒", name: "unamused face", shortcode: ":unamused:" },
        { emoji: "🙄", name: "face with rolling eyes", shortcode: ":roll_eyes:" },
        { emoji: "😬", name: "grimacing face", shortcode: ":grimacing:" },
        { emoji: "😮‍💨", name: "face exhaling", shortcode: ":face_exhaling:" },
        { emoji: "🤥", name: "lying face", shortcode: ":lying_face:" },
        { emoji: "😌", name: "relieved face", shortcode: ":relieved:" },
        { emoji: "😔", name: "pensive face", shortcode: ":pensive:" },
        { emoji: "😪", name: "sleepy face", shortcode: ":sleepy:" },
        { emoji: "🤤", name: "drooling face", shortcode: ":drooling_face:" },
        { emoji: "😴", name: "sleeping face", shortcode: ":sleeping:" },
        { emoji: "😷", name: "face with medical mask", shortcode: ":mask:" },
        { emoji: "🤒", name: "face with thermometer", shortcode: ":face_with_thermometer:" },
        { emoji: "🤕", name: "face with head-bandage", shortcode: ":face_with_head_bandage:" },
        { emoji: "🤢", name: "nauseated face", shortcode: ":nauseated_face:" },
        { emoji: "🤮", name: "face vomiting", shortcode: ":face_vomiting:" },
        { emoji: "🤧", name: "sneezing face", shortcode: ":sneezing_face:" },
        { emoji: "🥵", name: "hot face", shortcode: ":hot_face:" },
        { emoji: "🥶", name: "cold face", shortcode: ":cold_face:" },
        { emoji: "🥴", name: "woozy face", shortcode: ":woozy_face:" },
        { emoji: "😵", name: "knocked-out face", shortcode: ":dizzy_face:" },
        { emoji: "😵‍💫", name: "face with spiral eyes", shortcode: ":face_with_spiral_eyes:" },
        { emoji: "🤯", name: "exploding head", shortcode: ":exploding_head:" },
        { emoji: "🤠", name: "cowboy hat face", shortcode: ":cowboy_hat_face:" },
        { emoji: "🥳", name: "partying face", shortcode: ":partying_face:" },
        { emoji: "🥸", name: "disguised face", shortcode: ":disguised_face:" },
        { emoji: "😎", name: "smiling face with sunglasses", shortcode: ":sunglasses:" },
        { emoji: "🤓", name: "nerd face", shortcode: ":nerd_face:" },
        { emoji: "🧐", name: "face with monocle", shortcode: ":monocle_face:" },
        { emoji: "😕", name: "confused face", shortcode: ":confused:" },
        { emoji: "😟", name: "worried face", shortcode: ":worried:" },
        { emoji: "🙁", name: "slightly frowning face", shortcode: ":slightly_frowning_face:" },
        { emoji: "☹️", name: "frowning face", shortcode: ":frowning2:" },
        { emoji: "😮", name: "face with open mouth", shortcode: ":open_mouth:" },
        { emoji: "😯", name: "hushed face", shortcode: ":hushed:" },
        { emoji: "😲", name: "astonished face", shortcode: ":astonished:" },
        { emoji: "😳", name: "flushed face", shortcode: ":flushed:" },
        { emoji: "🥺", name: "pleading face", shortcode: ":pleading_face:" },
        { emoji: "😦", name: "frowning face with open mouth", shortcode: ":frowning:" },
        { emoji: "😧", name: "anguished face", shortcode: ":anguished:" },
        { emoji: "😨", name: "fearful face", shortcode: ":fearful:" },
        { emoji: "😰", name: "anxious face with sweat", shortcode: ":cold_sweat:" },
        { emoji: "😥", name: "sad but relieved face", shortcode: ":disappointed_relieved:" },
        { emoji: "😢", name: "crying face", shortcode: ":cry:" },
        { emoji: "😭", name: "loudly crying face", shortcode: ":sob:" },
        { emoji: "😱", name: "face screaming in fear", shortcode: ":scream:" },
        { emoji: "😖", name: "confounded face", shortcode: ":confounded:" },
        { emoji: "😣", name: "persevering face", shortcode: ":persevere:" },
        { emoji: "😞", name: "disappointed face", shortcode: ":disappointed:" },
        { emoji: "😓", name: "downcast face with sweat", shortcode: ":sweat:" },
        { emoji: "😩", name: "weary face", shortcode: ":weary:" },
        { emoji: "😫", name: "tired face", shortcode: ":tired_face:" },
        { emoji: "🥱", name: "yawning face", shortcode: ":yawning_face:" },
        { emoji: "😤", name: "face with steam from nose", shortcode: ":triumph:" },
        { emoji: "😡", name: "pouting face", shortcode: ":rage:" },
        { emoji: "😠", name: "angry face", shortcode: ":angry:" },
        { emoji: "🤬", name: "face with symbols on mouth", shortcode: ":cursing_face:" },
        { emoji: "😈", name: "smiling face with horns", shortcode: ":smiling_imp:" },
        { emoji: "👿", name: "angry face with horns", shortcode: ":imp:" },
        { emoji: "💀", name: "skull", shortcode: ":skull:" },
        { emoji: "☠️", name: "skull and crossbones", shortcode: ":skull_and_crossbones:" },
        { emoji: "💩", name: "pile of poo", shortcode: ":poop:" },
        { emoji: "🤡", name: "clown face", shortcode: ":clown_face:" },
        { emoji: "👹", name: "ogre", shortcode: ":japanese_ogre:" },
        { emoji: "👺", name: "goblin", shortcode: ":japanese_goblin:" },
        { emoji: "👻", name: "ghost", shortcode: ":ghost:" },
        { emoji: "👽", name: "alien", shortcode: ":alien:" },
        { emoji: "👾", name: "alien monster", shortcode: ":space_invader:" },
        { emoji: "🤖", name: "robot", shortcode: ":robot:" }
    ],
    
    // People & Body (partial list)
    "people": [
        { emoji: "👋", name: "waving hand", shortcode: ":wave:" },
        { emoji: "🤚", name: "raised back of hand", shortcode: ":raised_back_of_hand:" },
        { emoji: "✋", name: "raised hand", shortcode: ":raised_hand:" },
        { emoji: "🖖", name: "vulcan salute", shortcode: ":vulcan_salute:" },
        { emoji: "👌", name: "OK hand", shortcode: ":ok_hand:" },
        { emoji: "🤌", name: "pinched fingers", shortcode: ":pinched_fingers:" },
        { emoji: "🤏", name: "pinching hand", shortcode: ":pinching_hand:" },
        { emoji: "✌️", name: "victory hand", shortcode: ":v:" },
        { emoji: "🤞", name: "crossed fingers", shortcode: ":crossed_fingers:" },
        { emoji: "🤟", name: "love-you gesture", shortcode: ":love_you_gesture:" },
        { emoji: "🤘", name: "sign of the horns", shortcode: ":metal:" },
        { emoji: "🤙", name: "call me hand", shortcode: ":call_me_hand:" },
        { emoji: "👈", name: "backhand index pointing left", shortcode: ":point_left:" },
        { emoji: "👉", name: "backhand index pointing right", shortcode: ":point_right:" },
        { emoji: "👆", name: "backhand index pointing up", shortcode: ":point_up_2:" },
        { emoji: "👇", name: "backhand index pointing down", shortcode: ":point_down:" },
        { emoji: "☝️", name: "index pointing up", shortcode: ":point_up:" },
        { emoji: "👍", name: "thumbs up", shortcode: ":thumbsup:" },
        { emoji: "👎", name: "thumbs down", shortcode: ":thumbsdown:" },
        { emoji: "✊", name: "raised fist", shortcode: ":fist_raised:" },
        { emoji: "👊", name: "oncoming fist", shortcode: ":fist_oncoming:" },
        { emoji: "🤛", name: "left-facing fist", shortcode: ":fist_left:" },
        { emoji: "🤜", name: "right-facing fist", shortcode: ":fist_right:" },
        { emoji: "👏", name: "clapping hands", shortcode: ":clap:" },
        { emoji: "🙌", name: "raising hands", shortcode: ":raised_hands:" },
        { emoji: "👐", name: "open hands", shortcode: ":open_hands:" },
        { emoji: "🤲", name: "palms up together", shortcode: ":palms_up_together:" },
        { emoji: "🤝", name: "handshake", shortcode: ":handshake:" },
        { emoji: "🙏", name: "folded hands", shortcode: ":pray:" },
        { emoji: "✍️", name: "writing hand", shortcode: ":writing_hand:" }
    ],
    
    // Animals & Nature (partial list)
    "nature": [
        { emoji: "🐶", name: "dog face", shortcode: ":dog:" },
        { emoji: "🐱", name: "cat face", shortcode: ":cat:" },
        { emoji: "🐭", name: "mouse face", shortcode: ":mouse:" },
        { emoji: "🐹", name: "hamster", shortcode: ":hamster:" },
        { emoji: "🐰", name: "rabbit face", shortcode: ":rabbit:" },
        { emoji: "🦊", name: "fox", shortcode: ":fox_face:" },
        { emoji: "🐻", name: "bear", shortcode: ":bear:" },
        { emoji: "🐼", name: "panda", shortcode: ":panda_face:" },
        { emoji: "🐨", name: "koala", shortcode: ":koala:" },
        { emoji: "🐯", name: "tiger face", shortcode: ":tiger:" },
        { emoji: "🦁", name: "lion", shortcode: ":lion_face:" },
        { emoji: "🐮", name: "cow face", shortcode: ":cow:" },
        { emoji: "🐷", name: "pig face", shortcode: ":pig:" }
    ],
    
    // Food & Drink (partial list)
    "food": [
        { emoji: "🍎", name: "red apple", shortcode: ":apple:" },
        { emoji: "🍐", name: "pear", shortcode: ":pear:" },
        { emoji: "🍊", name: "tangerine", shortcode: ":tangerine:" },
        { emoji: "🍋", name: "lemon", shortcode: ":lemon:" },
        { emoji: "🍌", name: "banana", shortcode: ":banana:" },
        { emoji: "🍉", name: "watermelon", shortcode: ":watermelon:" },
        { emoji: "🍇", name: "grapes", shortcode: ":grapes:" },
        { emoji: "🍓", name: "strawberry", shortcode: ":strawberry:" },
        { emoji: "🍈", name: "melon", shortcode: ":melon:" },
        { emoji: "🍒", name: "cherries", shortcode: ":cherries:" },
        { emoji: "🍑", name: "peach", shortcode: ":peach:" },
        { emoji: "🥭", name: "mango", shortcode: ":mango:" }
    ],
    
    // Activities (partial list)
    "activities": [
        { emoji: "⚽", name: "soccer ball", shortcode: ":soccer:" },
        { emoji: "🏀", name: "basketball", shortcode: ":basketball:" },
        { emoji: "🏈", name: "american football", shortcode: ":football:" },
        { emoji: "⚾", name: "baseball", shortcode: ":baseball:" },
        { emoji: "🥎", name: "softball", shortcode: ":softball:" },
        { emoji: "🎾", name: "tennis", shortcode: ":tennis:" },
        { emoji: "🏐", name: "volleyball", shortcode: ":volleyball:" },
        { emoji: "🏉", name: "rugby football", shortcode: ":rugby_football:" }
    ],
    
    // Travel & Places (partial list)
    "travel": [
        { emoji: "🚗", name: "car", shortcode: ":car:" },
        { emoji: "🚕", name: "taxi", shortcode: ":taxi:" },
        { emoji: "🚙", name: "sport utility vehicle", shortcode: ":blue_car:" },
        { emoji: "🚌", name: "bus", shortcode: ":bus:" },
        { emoji: "🚎", name: "trolleybus", shortcode: ":trolleybus:" }
    ],
    
    // Objects (partial list)
    "objects": [
        { emoji: "⌚", name: "watch", shortcode: ":watch:" },
        { emoji: "📱", name: "mobile phone", shortcode: ":iphone:" },
        { emoji: "💻", name: "laptop", shortcode: ":computer:" },
        { emoji: "⌨️", name: "keyboard", shortcode: ":keyboard:" },
        { emoji: "🖥️", name: "desktop computer", shortcode: ":desktop_computer:" }
    ],
    
    // Symbols (partial list)
    "symbols": [
        { emoji: "❤️", name: "red heart", shortcode: ":heart:" },
        { emoji: "🧡", name: "orange heart", shortcode: ":orange_heart:" },
        { emoji: "💛", name: "yellow heart", shortcode: ":yellow_heart:" },
        { emoji: "💚", name: "green heart", shortcode: ":green_heart:" },
        { emoji: "💙", name: "blue heart", shortcode: ":blue_heart:" }
    ],
    
    // Flags (partial list)
    "flags": [
        { emoji: "🏁", name: "chequered flag", shortcode: ":checkered_flag:" },
        { emoji: "🚩", name: "triangular flag", shortcode: ":triangular_flag_on_post:" },
        { emoji: "🎌", name: "crossed flags", shortcode: ":crossed_flags:" },
        { emoji: "🏴", name: "black flag", shortcode: ":black_flag:" },
        { emoji: "🏳️", name: "white flag", shortcode: ":white_flag:" }
    ]
};

// Initialize frequently used with most common emojis
emojiData['frequently-used'] = [
    { emoji: "👍", name: "thumbs up", shortcode: ":thumbsup:" },
    { emoji: "❤️", name: "red heart", shortcode: ":heart:" },
    { emoji: "😂", name: "face with tears of joy", shortcode: ":joy:" },
    { emoji: "🔥", name: "fire", shortcode: ":fire:" },
    { emoji: "😊", name: "smiling face with smiling eyes", shortcode: ":blush:" },
    { emoji: "🙏", name: "folded hands", shortcode: ":pray:" },
    { emoji: "✨", name: "sparkles", shortcode: ":sparkles:" },
    { emoji: "😭", name: "loudly crying face", shortcode: ":sob:" },
    { emoji: "😍", name: "smiling face with heart-eyes", shortcode: ":heart_eyes:" },
    { emoji: "🥰", name: "smiling face with hearts", shortcode: ":smiling_face_with_three_hearts:" },
    { emoji: "🤔", name: "thinking face", shortcode: ":thinking:" },
    { emoji: "🙄", name: "face with rolling eyes", shortcode: ":roll_eyes:" },
    { emoji: "👏", name: "clapping hands", shortcode: ":clap:" },
    { emoji: "🤣", name: "rolling on the floor laughing", shortcode: ":rofl:" },
    { emoji: "⭐", name: "star", shortcode: ":star:" },
    { emoji: "✅", name: "check mark button", shortcode: ":white_check_mark:" }
];

// Function to search emojis
function searchEmojis(query) {
    if (!query || query.trim() === '') return [];
    
    query = query.toLowerCase().trim();
    
    let results = [];
    
    // Search across all categories
    for (let category in emojiData) {
        const matches = emojiData[category].filter(item => 
            item.name.toLowerCase().includes(query) || 
            item.shortcode.toLowerCase().includes(query)
        );
        results = results.concat(matches);
    }
    
    // Remove duplicates (in case an emoji appears in multiple categories)
    const uniqueResults = [];
    const seen = new Set();
    
    for (const item of results) {
        if (!seen.has(item.emoji)) {
            seen.add(item.emoji);
            uniqueResults.push(item);
        }
    }
    
    return uniqueResults;
}

// API for custom emojis (placeholder for future implementation)
const customEmojiAPI = {
    // Will be populated with methods to handle custom emojis from server
    emojis: {}, // Will store emoji name:id -> data
    
    // Method to initialize custom emojis
    initialize: function(emojiData) {
        this.emojis = emojiData || {};
    },
    
    // Method to get custom emoji by name
    getByName: function(name) {
        return this.emojis[name] || null;
    },
    
    // Method to get all custom emojis
    getAll: function() {
        return Object.keys(this.emojis).map(name => ({
            name: name,
            id: this.emojis[name].id,
            data: this.emojis[name].data
        }));
    }
};
