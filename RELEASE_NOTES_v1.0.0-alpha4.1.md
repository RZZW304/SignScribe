# SignScribe v1.0.0 Alpha 4.1 Release Notes

**Release Date:** 2026-01-05
**Minecraft Version:** 1.21 and all 1.21.x versions
**Java Version:** 21
**Previous Version:** v1.0.0-alpha4
**Type:** Critical Bug Fix Hotfix

**IMPORTANT: This is an alpha release.** The mod is in early development and may contain bugs or incomplete features. Use at your own risk.

## Critical Bug Fixes

This is a hotfix release addressing critical bugs reported in GitHub Issue #2.

### 1. Fixed Game Crash on Sign Placement (CRITICAL)

**Issue:** Game crashed immediately when placing any sign with the error:
```
java.lang.NoSuchFieldError: Class net.minecraft.class_1269 does not have member field 'net.minecraft.class_1269 field_5811'
```

**Root Cause:** The sign text application code used broken reflection to access non-existent fields in Minecraft 1.21.5's SignBlockEntity class.

**Fix:**
- Removed all reflection-based text setting code (26 lines)
- Now uses only UpdateSignC2SPacket to send sign text updates to server
- This is the correct, version-agnostic way to update sign text in Fabric

**Impact:** Mod no longer crashes when placing signs

**Files Changed:**
- SignPlacementEventHandler.java:68-85 - Removed applyTextToSign() method with broken reflection

### 2. Extended Minecraft Version Support

**Issue:** Mod only worked on exact Minecraft 1.21, not 1.21.x versions like 1.21.1, 1.21.5, 1.21.11

**Fix:** Updated fabric.mod.json dependency
```
- "minecraft": "~1.21",
+ "minecraft": ">=1.21",
```

**Impact:** Mod now works on all 1.21.x versions

**Files Changed:**
- fabric.mod.json:33

### 3. Fixed ModMenu Configuration Button

**Issue:** Clicking the "Configure" button in ModMenu did nothing

**Root Cause:** SignScribeConfig.getConfigScreen() returned null

**Fix:**
- Created new SignScribeConfigScreen.java class
- Implemented full GUI with toggleable options:
  - Enabled/Disabled mod toggle
  - Auto-Advance toggle
  - Show Preview toggle
  - Require Empty Hand toggle
  - Show Success Message toggle
- Each option saves config immediately when toggled

**Impact:** ModMenu configuration now fully functional

**Files Changed:**
- SignScribeConfig.java:34 - Returns functional config screen
- SignScribeConfigScreen.java (NEW) - Full configuration GUI implementation

### 4. Fixed /signscribe open Command

**Issue:** Command appeared to do nothing when executed

**Fix:**
- Added try-catch error handling
- Added feedback messages to user
- Added console logging for debugging
- Shows "Opening file selection..." confirmation
- Displays error messages to user if GUI fails to open

**Impact:** Command now properly opens file selection GUI and reports errors

**Files Changed:**
- SignScribeCommands.java:30-40 - Enhanced /open command
- SignScribeFileScreen.java:21-30 - Added logging to constructor
- SignScribeFileScreen.java:69-79 - Added logging to selectFile method

### 5. Updated Repository URLs

**Issue:** Placeholder URLs in fabric.mod.json pointed to non-existent repositories

**Fix:** Updated all URLs to point to actual GitHub repository

**Impact:** Mod metadata now correctly points to repository

**Files Changed:**
- fabric.mod.json - Updated contact and sources URLs

## Installation

### Requirements
- Minecraft: 1.21 or any 1.21.x (1.21, 1.21.1, 1.21.5, 1.21.11, etc.)
- Java: 21+
- Fabric Loader: 0.15.11+
- Fabric API: 0.100.4+1.21 (or newer)

### Download
**Release URL:** https://github.com/RZZW304/SignScribe/releases/tag/v1.0.0-alpha4.1

### Install
1. Download SignScribe-1.0.0-alpha4.1.jar
2. Place in .minecraft/mods/
3. Launch Minecraft
4. Enjoy working sign placement

## Migration from Alpha 4

### No Migration Required
Simply replace SignScribe-1.0.0-alpha4.jar with SignScribe-1.0.0-alpha4.1.jar

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
- All commands from Alpha 4 work unchanged
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
**Version:** v1.0.0-alpha4.1
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

Thank you for testing SignScribe v1.0.0 Alpha 4.1. Sign placement is now fully functional
