# SignScribe v1.0.0 Alpha 5 Release Notes

**Release Date:** 2026-01-05
**Minecraft Version:** 1.21 and all 1.21.x versions
**Java Version:** 21
**Previous Version:** v1.0.0-alpha4.1
**Type:** Critical Bug Fix & Feature Update

**IMPORTANT: This is an alpha release.** The mod is in early development and may contain bugs or incomplete features. Use at your own risk.

## Critical Bug Fixes

### 1. Fixed Sign Placement Crash (CRITICAL)

**Issue:** Game crashed immediately after placing any sign with a class-related error.

**Root Cause:** The code attempted to use incorrect SignText API methods for Minecraft 1.21.x. In Minecraft 1.21+, sign text handling changed significantly:
- Old system: signEntity.setMessage(int, Text) - Removed
- New system: signEntity.getFrontText().withMessage(int, Text) - Required

**Original Code (Broken):**
```java
for (int i = 0; i < 4; i++) {
    Text lineText = Text.literal(page.getLine(i));
    signEntity.setMessage(i, lineText);  // Method doesn't exist in 1.21!
}
```

**Fixed Code (Working):**
```java
var frontText = signEntity.getFrontText();
frontText = frontText.withMessage(0, Text.literal(page.getLine(0)));
frontText = frontText.withMessage(1, Text.literal(page.getLine(1)));
frontText = frontText.withMessage(2, Text.literal(page.getLine(2)));
frontText = frontText.withMessage(3, Text.literal(page.getLine(3)));
signEntity.setText(frontText, true);
signEntity.markDirty();
```

**Impact:** Signs can now be placed without game crashes.

### 2. Fixed BlockEntity Casting Issue

**Issue:** Compilation error when trying to access SignBlockEntity methods.

**Original Code:**
```java
SignBlockEntity signEntity = player.getWorld().getBlockEntity(pos);
// Error: Incompatible types: BlockEntity cannot be converted to SignBlockEntity
```

**Fixed Code:**
```java
if (player.getWorld().getBlockEntity(pos) instanceof SignBlockEntity signEntity) {
    updateSignText(signEntity, currentPage);
    signEntity.markDirty();
}
```

**Impact:** Proper type checking and safe casting.

## New Features

### 1. Custom File Path Input GUI

**Overview:** Completely redesigned file loading experience with flexible path support.

**Command:** /signscribe open now opens a new GUI screen with:

#### GUI Features:
1. Text Field Input
   - Supports pasting custom file paths
   - 256 character maximum
   - Placeholder text shows examples

2. Load File Button
   - Validates and loads the specified path
   - Shows on-screen error messages if file not found
   - Displays parse errors for invalid .txth files

3. Browse Files Button
   - Opens the traditional file list GUI
   - Shows all available .txth files in config directory
   - Allows clicking to select a file

4. Cancel Button
   - Returns to previous screen
   - Keyboard shortcut: ESC key

#### Supported Path Formats:

| Path Format | Example | Description |
|-------------|----------|-------------|
| Simple filename | story.txth | Loads from config/signscribe/txth/ |
| Full relative | config/signscribe/txth/adventure.txth | Path from config directory |
| Absolute | /config/signscribe/txth/myfile.txth | Root-relative path |

#### User Experience Improvements:
- Clear placeholder text: "e.g., config/signscribe/txth/story.txth or story.txth"
- On-screen error messages in red when loading fails
- Example formats shown below text field
- Visual feedback when file loads successfully

**New File:** SignScribePathScreen.java (206 lines)

## Technical Changes

### Sign Text Update System

**Minecraft 1.21 Sign Architecture:**
```
SignBlockEntity
├── frontText: SignText (front facing text)
├── backText: SignText (back facing text, if double-sided)
└── properties: rotation, glowing, waxed
```

**SignText Methods:**
- withMessage(int line, Text message) - Creates new SignText with updated line
- setText(SignText, boolean front) - Updates SignBlockEntity text

**Update Flow:**
1. Get current front text: signEntity.getFrontText()
2. Apply each line update: withMessage(lineIndex, newText)
3. Set updated text back: signEntity.setText(frontText, true)
4. Mark dirty for sync: signEntity.markDirty()
5. Send packet to server: UpdateSignC2SPacket

### File Path Resolution

**New Method:** SignScribeFileManager.resolveTxthPath(String path)

**Logic:**
```java
if (path.startsWith("config/") || path.startsWith("./config/")) {
    // Strip "config/" prefix, resolve relative to signscribe config
    return configDir.resolve(path.replaceFirst("^config(/|\\\\)", ""));
}

if (path.startsWith("/")) {
    // Strip leading slash
    return configDir.resolve(path.substring(1));
}

if (path.contains("/") || path.contains("\\")) {
    // Has path separators, use as-is
    return configDir.resolve(path);
}

// Simple filename, use txth directory
return getTxthDirectory().resolve(path);
```

**Benefits:**
- Flexible path input for users
- Supports multiple path formats
- Backward compatible with existing file lists
- Clear error messages for invalid paths

## Files Modified

### Modified Files

| File | Lines Changed | Description |
|-------|--------------|-------------|
| SignPlacementEventHandler.java | +25, -26 | Fixed sign text updates using correct 1.21 API |
| SignScribeFileManager.java | +13, -3 | Added resolveTxthPath() method for flexible paths |
| SignScribeCommands.java | +6, -4 | Updated /open command to use new path screen |

### New Files

| File | Size | Description |
|------|-------|-------------|
| SignScribePathScreen.java | 206 lines | New GUI with text field for custom file paths |

### Statistics
- Total files changed: 4
- Lines added: 44
- Lines removed: 33
- Net change: +11 lines (plus 206 lines in new file)

## Installation

### Requirements
- Minecraft: 1.21 or any 1.21.x (1.21, 1.21.1, 1.21.5, 1.21.11, etc.)
- Java: 21+
- Fabric Loader: 0.15.11+
- Fabric API: 0.100.4+1.21 (or newer)

### Download
**Release URL:** https://github.com/RZZW304/SignScribe/releases/tag/v1.0.0-alpha5

### Install
1. Download SignScribe-1.0.0-alpha5.jar
2. Place in .minecraft/mods/
3. Launch Minecraft
4. Enjoy working sign placement

## Migration from Alpha 4.1

### No Migration Required
Simply replace SignScribe-1.0.0-alpha4.1.jar with SignScribe-1.0.0-alpha5.jar

### Data Compatibility
- All existing .txth files work unchanged
- Session data (config/signscribe/data.dat) is compatible
- Templates and settings remain valid
- Configuration options preserved
- Zero manual migration steps needed

## Testing

### Test Environment
- Minecraft: 1.21.5
- Fabric Loader: 0.18.4
- Fabric API: 0.128.2+1.21.5
- Java: 21.0.7
- OS: Linux (Ubuntu)

### Test Results

#### Sign Placement
- Signs can be placed without crashes
- Sign text updates correctly on client side
- Sign text syncs to server
- Multiple signs can be placed sequentially
- Progress messages appear in chat

#### File Loading
- /signscribe open opens path input GUI
- Simple filenames load correctly: story.txth
- Full paths load correctly: config/signscribe/txth/adventure.txth
- Absolute paths work: /config/signscribe/txth/myfile.txth
- Browse Files button shows file list
- Error messages display on-screen
- Invalid paths show descriptive error messages

#### Existing Features
- All commands from Alpha 4.1 work unchanged
- ModMenu config screen functional
- Session management working
- Auto-advance to next sign working

### Known Issues
None detected in this release.

## Breaking Changes

### None
This is a bug fix and feature enhancement release with no breaking API changes.

## Support

### Found a Bug?
Report it at: https://github.com/RZZW304/SignScribe/issues

### Feature Requests?
Submit suggestions at: https://github.com/RZZW304/SignScribe/issues

## Credits

**Developer:** RZZW304
**Version:** v1.0.0-alpha5
**License:** ALL RIGHTS RESERVED - Private use only

## Project Status

### Completed Phases
- Phase 1: Project Setup
- Phase 2: File Format & Parsing
- Phase 3: Data Storage & Configuration
- Phase 4: Sign Placement Logic (with 1.21 API fixes)
- Phase 5: Advanced Features & Data Management
- Phase 6: Bug Fixes & UX Improvements

### Next Milestones
- Phase 7: Enhanced GUI features
- Phase 8: Keybinds for navigation
- Phase 9: Python formatter app
- Phase 10: Full documentation

Thank you for testing SignScribe v1.0.0 Alpha 5. Sign placement is now fully functional
