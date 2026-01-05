# SignScribe Project Progress

## Project Overview
**Name:** SignScribe  
**Platform:** Fabric (client-side mod)  
**Minecraft Version:** 1.21.x  
**Experience Level:** 5/100  
**Purpose:** Create a Minecraft mod for easily copying stories from a txt file onto signs (similar to SignStory for Forge)

---

## Requirements Analysis

### User Requirements
1. **Text Formatting:** Automatic text wrapping and formatting for signs
   - Wrap text at 15 characters without chopping words
   - Chop words >15 characters every 15 chars
   - 4 lines per sign (15x4 = 60 chars total)
   - Handle newlines properly (start new line after newline)
   - No manual formatting needed - mod handles everything

2. **ModMenu GUI:**
   - Toggle mod on/off
   - Keybind configuration

3. **File Handling:**
   - Load one txt file at a time
   - Simple directory selection

4. **Sign Placement:**
   - Right-click sign → auto-apply next page
   - Auto-advance to next page on subsequent sign placements
   - Seamless placement experience

5. **Target Audience:** Regular players creating stories on signs (like a hobbit writing thousands of signs)

6. **No Compatibility Needed:** No special server-side compatibility required

---

## SignStory Mod Analysis

### Reference Implementation
**Source:** https://github.com/maximuslotro/SignStory  
**Modloader:** Forge (1.7.10)  
**License:** LGPL-3.0

### Core Architecture
```
com.maximuslotro.mc.signpic/
├── SignPicture.java          # Main FML mod class
├── Reference.java            # Mod constants
├── CoreHandler.java          # Central event coordination
├── Client.java               # Client-side utilities
├── ClientProxy.java          # Client proxy
├── CommonProxy.java          # Common proxy
├── Config.java               # Configuration
├── Global_Vars.java          # Global state storage
├── command/
│   └── SignStoryCommand.java # Command handlers
├── gui/                      # GUI components
│   ├── GuiMain.java
│   ├── GuiSignOption.java
│   ├── GuiSettings.java
│   ├── GuiSize.java
│   ├── GuiOffset.java
│   ├── GuiRotation.java
│   └── file/
│       └── McUiTextSelect.java
├── handler/
│   ├── SignHandler.java      # Sign placement events
│   ├── KeyHandler.java       # Keyboard input
│   └── AnvilHandler.java     # Anvil naming
├── entry/                    # Content entry system
│   ├── EntryId.java
│   ├── EntryManager.java
│   └── content/
├── attr/                     # Formatting attributes
│   ├── AttrReaders.java
│   └── prop/
└── util/                     # Utilities
    ├── FileUtilitiy.java
    ├── Sign.java
    └── WordUtil.java         # Text splitting
```

---

## Key Implementation Components

### 1. Text Processing (WordUtil.java)

#### WordUtil.Splitter() - Core Algorithm
```java
public static List<String> Splitter(String text, int line_length, int row_n_combine, char delimiter)
```

**Process:**
1. **splitString()** - Split text into lines respecting word boundaries
2. **addspaces()** - Pad each line to exactly 15 characters
3. **combinelines()** - Combine 4 lines into one sign page (60 chars)

#### Text Formatting Rules (from SignStory example)

**Word Wrapping:**
- Try to place as many words on one line as possible without chopping words
- Maximum line length: 15 characters

**Long Word Handling:**
- If word exceeds 15 characters, chop every 15 characters into new line
- Example: `VeryLargeLargeWord` → `VeryLargeLargeW` + `ord`

**Newline Handling:**
- When encountering newline, move next word to another line
- Example: `Lets place a new line. Cool Stuff is very nice YAY!`
  ```
  [Lets place a   ]
  [new line.      ]
  [Cool Stuff is  ]
  [very nice YAY! ]
  ```

**Incorrect behavior to avoid:**
```
[Lets place a   ]
[new line. Cool ]
[Stuff is very  ]
[nice YAY!      ]
```

#### File Loading Process (FileUtilitiy.loadTextFile())

```java
1. Read entire file as UTF-8 string
2. Check for special delimiters: &, %, $, @, {, }
3. Replace newlines with delimiter + space
4. Call WordUtil.Splitter() to process
5. Store result in Global_Vars.Text (List<String>)
6. Set first page
7. Enable PLACE mode
8. Enable PREVIEW state
```

---

### 2. Sign Placement Logic (SignHandler.java)

#### Event Flow
```
Player right-clicks sign → GuiEditSign opens → SignHandler.onSign()
    → Cancel GUI → Sign.placeSign() → C12PacketUpdateSign → Server
```

#### Sign.placeSign() Implementation
```java
public static void placeSign(EntryId entryId, TileEntitySign sourceentity) {
    // Multi-player check
    if (Config.multiplayPAAS.get() && !Client.mc.isSingleplayer()) {
        // Show PAAS (Prevent Anti-Auto Sign) delay screen
        Client.mc.displayGuiScreen(new GuiPAAS(new SendPacketTask(entryId, sourceentity)));
    } else {
        // Single player: send immediately
        sendSign(entryId, sourceentity);
    }
}
```

#### Page Progression System
```
Place sign → Update Global_Vars.CurrentPage 
    → Get next page from Global_Vars.Text 
    → Display chat notification with progress %
```

#### EntryId.toEntity() - Apply Text to TileEntity
```java
public void toEntity(TileEntitySign tile) {
    if (tile != null) {
        toStrings(tile.signText);
    }
}

public void toStrings(String[] sign) {
    // ID string is up to 60 characters (15 x 4)
    for (int i = 0; i < 4; i++) {
        sign[i] = substring(id(), 15*i, Math.min(15*(i+1), length()));
    }
}
```

---

### 3. GUI Implementation (BnnWidget Framework)

#### Main GUI Components

**GuiMain.java (Main Editor)**
- SignEditor panel with:
  - Size controls (GuiSize)
  - Offset controls (GuiOffset)
  - Rotation controls (GuiRotation)
  - URL input field (MainTextField)
  - Preview panel (SignPicLabel)
- Button panel:
  - "See" - Toggle visibility
  - "Preview" - Set preview mode
  - "File" - Open file picker
  - "Paste" - Paste from clipboard
  - "Continue" - Continue placing signs
  - "Option" - Open options menu
  - "Place" - Enter place mode

**McUiTextSelect.java (File Picker)**
- Opens file selection dialog
- Reads .txt files
- Automatically processes text into sign pages
- Uses FileUtilitiy.loadTextFile() for processing

**GuiSignOption.java (Options Menu)**
- Load button (with sub-options)
- Open URL button
- Reload button
- Redownload button
- Cancel load button
- Block/Unblock button

---

### 4. Command System (SignStoryCommand.java)

**Registered command:** `/SignStory`

**Subcommands:**
1. **load** (l) - Open file picker to load text file
2. **select** (s) - Select specific page by number
3. **toggle** (t) - Toggle between SignPicture and SignStory mode

**Usage Examples:**
```
/SignStory load           # Opens file picker
/SignStory select 3       # Select page 3
/SignStory select         # Show total pages and usage
/SignStory toggle         # Toggle mode
```

---

### 5. Global State Management (Global_Vars.java)

```java
public static List<String> Text;      // List of sign pages
public static int CurrentPage = 0;     // Current page index
```

---

### 6. Mode System (CurrentMode.java)

```java
Mode enum: NONE, PLACE, SETPREVIEW, OPTION
State enum: PREVIEW, CONTINUE, SEE

// Mode meanings:
NONE       - Normal gameplay
PLACE      - Placing signs with loaded text
SETPREVIEW - Setting preview position
OPTION     - Managing sign options

// State meanings:
PREVIEW    - Show floating sign preview
CONTINUE   - Continue placing signs without closing
SEE        - Make preview sign visible
```

---

### 7. Configuration System (Config.java)

**Important Settings:**
```java
// General
defaultUsage = true/false      // Use SignStory or SignPicture mode
signTooltip = true/false       // Show tooltip on sign items

// Multi-player anti-cheat
multiplayPAAS = true                    // Enable PAAS
multiplayPAASMinEditTime = 150          // Base edit time (ms)
multiplayPAASMinLineTime = 50           // Time per line (ms)
multiplayPAASMinCharTime = 50           // Time per character (ms)

// Rendering
renderOverlayPanel = true
renderGuiOverlay = true
renderUseMipmap = true
```

---

## Implementation Patterns to Replicate

### 1. Text Processing Pipeline
```
TXT File → loadTextFile() → WordUtil.Splitter() 
    → List<String> (60-char pages) → EntryId → TileEntitySign
```

### 2. Sign Placement Event Flow
```
Player right-clicks sign → Cancel GuiEditSign event
    → Apply text to TileEntitySign → Send update packet → Server
```

### 3. Page Progression System
```
Place sign → Update current page index 
    → Get next page from loaded text 
    → Display chat notification with progress %
```

---

## SignScribe Implementation Plan (Fabric 1.21.x)

### Phase 1: Project Setup
- [ ] Set up Fabric development environment for 1.21.x
- [ ] Create basic mod structure
- [ ] Add ModMenu integration
- [ ] Set up build configuration (gradle)

### Phase 2: Core Text Processing
- [ ] Implement text splitting algorithm (WordUtil.Splitter equivalent)
- [ ] Implement file loading utility
- [ ] Handle word wrapping (max 15 chars/line)
- [ ] Handle long word chopping (>15 chars)
- [ ] Handle newline properly
- [ ] Pad lines to exactly 15 characters
- [ ] Combine 4 lines into sign pages (60 chars)

### Phase 3: Data Storage
- [ ] Create global state management
- [ ] Store loaded text pages (List<String>)
- [ ] Track current page index
- [ ] Implement configuration storage

### Phase 4: Sign Placement Logic
- [ ] Hook into sign placement event
- [ ] Cancel default sign edit GUI
- [ ] Apply text to sign TileEntity
- [ ] Send update packet to server
- [ ] Auto-advance to next page
- [ ] Display progress notifications

### Phase 5: GUI Implementation
- [ ] File picker GUI (using Fabric API)
- [ ] ModMenu configuration screen
- [ ] Toggle on/off switch
- [ ] Keybind configuration
- [ ] Load file button

### Phase 6: Commands
- [ ] Implement `/signscribe load` command
- [ ] Implement `/signscribe select <page>` command
- [ ] Implement `/signscribe toggle` command

### Phase 7: Testing & Polish
- [ ] Test text formatting edge cases
- [ ] Test sign placement flow
- [ ] Test page progression
- [ ] Add user feedback (chat notifications)
- [ ] Test with various txt file formats

---

## Key Technical Considerations for Fabric

### Fabric API Differences from Forge
- Use Fabric's `ClientTickEvent` instead of FML events
- Use `ClientCommandRegistrationCallback` for commands
- Use `ScreenEvents` for GUI events
- Use `ClientBlockEntityEvents` for sign events
- Different packet handling system (Fabric's networking API)

### Fabric Mod Structure
```java
public class SignScribeMod implements ModInitializer {
    public static final String MODID = "signscribe";
    
    @Override
    public void onInitialize() {
        // Register mod components
    }
}
```

### Fabric Client Entry Point
```java
public class SignScribeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register client-side components
        // Keybinds
        // GUI
        // Render events
    }
}
```

---

## Notes & Next Steps

### Current Status
- Project requirements clarified
- SignStory reference implementation analyzed
- Implementation patterns documented
- Ready to begin development

### User Requirements Confirmed
✓ Text formatting: Auto-wrap, chop long words, handle newlines  
✓ ModMenu: Toggle on/off, keybinds  
✓ File handling: Load one txt file at a time  
✓ Sign placement: Auto-apply, auto-advance  
✓ Platform: Fabric 1.21.x  
✓ No compatibility needed  

### Development Level
- Experience: 5/100 (beginner)
- Approach: Start simple, build incrementally
- Focus: Core text processing and sign placement first

---

**Last Updated:** January 4, 2026  
**Status:** Requirements gathering complete, ready to begin implementation
