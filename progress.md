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
1. **Text Formatting:** Pre-formatted .txth files
    - 14 characters per line (not 15)
    - 4 lines per sign (14x4 = 56 chars total)
    - Sign format: SIGN1:, SIGN2:, etc.
    - Each sign block must have exactly 4 lines
    - Empty lines without {BLANk} = ERROR
    - Intentional blank lines marked with {BLANk}
    - No auto-wrapping - mod expects pre-formatted files

2. **ModMenu GUI:**
    - Full configuration screen (duplicates command functionality)
    - Toggle mod on/off
    - Select starting sign number
    - Paste file path for .txth files
    - Display current status: enabled/disabled, current sign number, total signs loaded

3. **File Handling:**
    - Load one .txth file at a time
    - Custom GUI for pasting file path
    - File format: .txth extension with SIGN1:, SIGN2: format
    - Remember last loaded file path between game restarts
    - Remember last placed sign position while file is loaded
    - Clear saved position when file is unloaded

4. **Sign Placement:**
    - Right-click sign → auto-apply current sign text
    - Auto-advance to next sign on subsequent sign placements
    - Default to last placed sign if available, otherwise SIGN1
    - Support all sign types (oak, birch, spruce, etc.)
    - No hotkey to cancel placement mode
    - Error if sign number > total signs: "There are only [number] signs"

5. **External Formatter App:**
    - Desktop GUI application
    - Modern programming language (Python recommended)
    - Converts normal .txt files to .txth format
    - Auto-wraps text to 14 characters per line
    - Handles SIGN1:, SIGN2: format with 4-line blocks
    - Replaces intentional blank lines with {BLANk}
    - Creates .txth files ready for mod use

6. **Error Handling:**
    - Show errors and refuse to load malformed files
    - Specify which sign and line has errors
    - Validate: exactly 4 lines per sign, exactly 14 chars per line
    - Validate: no empty lines without {BLANk}

7. **Target Audience:** Regular players creating stories on signs (like a hobbit writing thousands of signs)

8. **No Compatibility Needed:** No special server-side compatibility required

9. **License:** ALL RIGHTS RESERVED - Private use only

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

### 4. Command System

**Registered command:** `/signscribe`

**Subcommands:**
1. **on** - Enable the mod
2. **off** - Disable the mod
3. **sign <number>** - Set starting sign number
   - If number > total signs: "There are only [number] signs"
   - Default: last placed sign, or 1 if none
4. **load** - Open file path GUI to load .txth file

**Usage Examples:**
```
/signscribe on              # Enable the mod
/signscribe off             # Disable the mod
/signscribe sign 5          # Start from sign 5
/signscribe load            # Open file path GUI
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

## SignScribe Project Components

The project consists of two main components:

### 1. Fabric Mod (SignScribe)
- Platform: Fabric 1.21.x
- Language: Java
- Purpose: Minecraft mod for applying pre-formatted text to signs

### 2. External Formatter App
- Type: Desktop GUI application
- Language: Python (modern, cross-platform)
- Purpose: Convert normal .txt files to .txth format
- Output: .txth files with proper sign formatting

---

## SignScribe Implementation Plan (Fabric 1.21.x)

### Phase 1: Project Setup
- [x] Set up Fabric development environment for 1.21.x
- [x] Create basic mod structure
- [x] Add ModMenu integration
- [x] Set up build configuration (gradle)
- [ ] Test basic mod loads in Minecraft

### Phase 2: File Format & Parsing
- [ ] Implement .txth file format parser
- [ ] Parse SIGN1:, SIGN2: blocks
- [ ] Validate exactly 4 lines per sign block
- [ ] Validate exactly 14 characters per line
- [ ] Detect empty lines without {BLANk} (error)
- [ ] Convert {BLANk} to actual blank lines
- [ ] Store sign pages in List<String>
- [ ] Count total signs in file

### Phase 3: Data Storage & Configuration
- [ ] Create global state management
- [ ] Store loaded .txth file path
- [ ] Store sign pages (List<String>)
- [ ] Track current sign number
- [ ] Track total signs loaded
- [ ] Implement configuration storage (last file, last sign, enabled state)
- [ ] Clear saved position when file is unloaded

### Phase 4: Sign Placement Logic
- [ ] Hook into sign placement event (all sign types)
- [ ] Cancel default sign edit GUI
- [ ] Apply pre-formatted text to sign TileEntity (exactly as parsed)
- [ ] Send update packet to server
- [ ] Auto-advance to next sign
- [ ] Save current sign number to config
- [ ] Display progress notifications

### Phase 5: GUI Implementation
- [ ] File path GUI (text field for pasting file path)
- [ ] Load .txth file button
- [ ] ModMenu configuration screen
  - Toggle mod on/off switch
  - Select starting sign number input
  - Paste file path text field
  - Load file button
  - Display current status (enabled, current sign, total signs)

### Phase 6: Commands
- [ ] Implement `/signscribe on` command
- [ ] Implement `/signscribe off` command
- [ ] Implement `/signscribe sign <number>` command
  - Validate number ≤ total signs
  - Show error if invalid: "There are only [number] signs"
  - Default to last placed sign or 1
- [ ] Implement `/signscribe load` command (opens file path GUI)

### Phase 7: Testing & Polish
- [ ] Test .txth file parsing (all formats)
- [ ] Test error detection (empty lines, wrong line count)
- [ ] Test sign placement flow
- [ ] Test sign progression
- [ ] Test persistence (remember last sign, clear on unload)
- [ ] Test ModMenu configuration
- [ ] Test commands
- [ ] Add user feedback (chat notifications, error messages)
- [ ] Test with all sign types

---

## External Formatter App Implementation Plan (Python)

### Phase 1: App Setup
- [ ] Set up Python project with modern GUI framework (PyQt6/Tkinter)
- [ ] Create basic window structure
- [ ] Set up build configuration (PyInstaller for .exe)

### Phase 2: File Input
- [ ] Open .txt file dialog
- [ ] Read file contents
- [ ] Display text preview

### Phase 3: Text Processing
- [ ] Implement text wrapping algorithm (14 chars/line)
- [ ] Handle long words (>14 chars)
- [ ] Handle newlines properly (preserve intentional line breaks)
- [ ] Replace intentional blank lines with {BLANk}

### Phase 4: Format Conversion
- [ ] Split text into 4-line blocks (56 chars per sign)
- [ ] Add SIGN1:, SIGN2: prefixes
- [ ] Validate line lengths (exactly 14 chars)
- [ ] Pad lines with spaces to exactly 14 chars
- [ ] Detect and flag intentional blank lines

### Phase 5: GUI Features
- [ ] Input text file selection
- [ ] Output .txth file selection
- [ ] Preview formatted output
- [ ] Convert button
- [ ] Progress indicator
- [ ] Error display (invalid lines, formatting issues)

### Phase 6: Output Generation
- [ ] Write .txth file with proper format
- [ ] Save file to selected location
- [ ] Success notification

### Phase 7: Testing
- [ ] Test with simple text (5-10 signs)
- [ ] Test with long text requiring auto-wrap
- [ ] Test edge cases (very long words, empty lines, special characters)
- [ ] Test {BLANk} replacement
- [ ] Test line padding (exactly 14 chars)
- [ ] Test with special characters
- [ ] Build .exe and test

---

## Key Technical Considerations for Fabric Mod

### File Format (.txth)
```
SIGN1:
[14 chars      ]  <- Line 1
[14 chars      ]  <- Line 2
[14 chars      ]  <- Line 3
[14 chars      ]  <- Line 4
SIGN2:
[14 chars      ]  <- Line 1
{BLANk}           <- Line 2 (intentional blank)
[14 chars      ]  <- Line 3
[14 chars      ]  <- Line 4
```

### Validation Rules
- Each sign must have exactly 4 lines
- Each line must have exactly 14 characters
- Empty lines (without {BLANk}) cause errors
- {BLANk} is converted to actual blank lines
- SIGN1:, SIGN2: markers separate signs

### Fabric API Components Needed
- Use Fabric's `ClientCommandRegistrationCallback` for commands
- Use `ClientBlockEntityEvents` for sign interaction
- Use `ModMenu` integration for configuration screen
- Use `ConfigApi` for configuration storage
- Use `ScreenEvents` for custom GUI
- Use `FileWatcher` (optional) for file monitoring

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
        // Commands
        // GUI
        // Sign events
        // Config loading
    }
}
```

---

## External Formatter App Technology Stack

### Recommended Stack
- **Language:** Python 3.11+
- **GUI Framework:** PyQt6 or Tkinter
- **Packaging:** PyInstaller (for .exe generation)
- **Platform:** Cross-platform (Windows, Linux, macOS)

### Why Python?
- Modern, easy to read and maintain
- Excellent text processing capabilities
- Cross-platform GUI frameworks
- Easy to package as .exe
- Fast development cycle
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
- Project requirements clarified and updated
- SignStory reference implementation analyzed
- File format specifications defined (.txth)
- External formatter app requirements documented
- Implementation plan for both components created
- Phase 1: Project Setup completed (except testing)
- Ready to begin Phase 2: File Format & Parsing

### User Requirements Confirmed
✓ Text formatting: Pre-formatted .txth files, 14 chars/line, 4 lines/sign
✓ File format: SIGN1:, SIGN2: blocks with validation
✓ External app: Python GUI for .txt → .txth conversion
✓ ModMenu: Full configuration screen (duplicate commands)
✓ Commands: on/off, sign <number>, load
✓ File handling: Load one .txth file at a time, remember last placed sign
✓ Sign placement: Auto-apply, auto-advance, support all sign types
✓ Persistence: Saved in config, cleared when file unloaded
✓ Platform: Fabric 1.21.x
✓ License: ALL RIGHTS RESERVED - Private use only
✓ Error handling: Show errors, specify sign/line, refuse to load

### Development Level
- Experience: 5/100 (beginner)
- Approach: Start with Fabric mod, build formatter app second
- Focus: Core file parsing and sign placement first, then GUI and commands

### Key Changes from Original Plan
- Changed from 15 to 14 characters per line (56 vs 60 chars total)
- Changed from auto-wrap to pre-formatted .txth files
- Added external formatter app for file conversion
- Changed from txt to .txth file extension
- Added {BLANk} for intentional blank lines
- Added strict validation (4 lines, 14 chars each)
- Changed persistence behavior (clear on file unload)
- Added ModMenu full configuration screen

---

## Phase 1 Progress: Project Setup

### Completed Tasks
✅ Set up Gradle/Loom configuration for Fabric 1.21.x
✅ Created project structure
✅ Added Fabric API dependencies (0.100.4+1.21)
✅ Added ModMenu dependency (11.0.1)
✅ Created fabric.mod.json with mod metadata
✅ Created signscribe.mixins.json for mixin configuration
✅ Created SignScribeMod.java (ModInitializer)
✅ Created SignScribeClient.java (ClientModInitializer)
✅ Created ModMenuIntegration.java for ModMenu API
✅ Added LICENSE file (ALL RIGHTS RESERVED)
✅ Created README.md with basic information
✅ Created .gitignore for project
✅ Set up Gradle wrapper (gradlew, gradlew.bat)
✅ Configured gradle.properties with version settings

### Project Structure
```
SignScribe/
├── build.gradle                 # Gradle build configuration
├── gradle.properties           # Project properties (versions, etc.)
├── settings.gradle              # Gradle settings
├── gradlew                      # Unix Gradle wrapper script
├── gradlew.bat                  # Windows Gradle wrapper script
├── LICENSE                      # License file
├── README.md                    # Project documentation
├── .gitignore                   # Git ignore file
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/main/
│   ├── java/com/signscribe/
│   │   ├── SignScribeMod.java           # Main mod class
│   │   ├── SignScribeClient.java        # Client entry point
│   │   └── integration/
│   │       └── ModMenuIntegration.java  # ModMenu API
│   └── resources/
│       ├── fabric.mod.json              # Fabric mod metadata
│       └── signscribe.mixins.json       # Mixin configuration
├── progress.md                  # Project progress documentation
└── TODO.md                      # Task list
```

### Configuration Details
- **Minecraft Version:** 1.21
- **Yarn Mappings:** 1.21+build.1
- **Fabric Loader:** 0.15.11
- **Fabric API:** 0.100.4+1.21
- **ModMenu:** 11.0.1
- **Java Version:** 21
- **Mod Version:** 1.0.0
- **Mod ID:** signscribe

### Remaining Phase 1 Tasks
- [ ] Test basic mod loads in Minecraft
  - Requires Minecraft 1.21 installation
  - Requires Fabric Loader
  - Requires Fabric API
  - Requires running gradlew genSources
  - Requires testing in-game

---

**Last Updated:** January 5, 2026
**Status:** Phase 1 nearly complete (needs testing), ready to begin Phase 2
