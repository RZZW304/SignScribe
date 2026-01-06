# SignScribe v1.0.0 Alpha 3 Release Notes

**Release Date:** 2025-01-05
**Minecraft Version:** 1.21
**Java Version:** 21
**Previous Version:** v1.0.0-alpha2

**IMPORTANT: This is an alpha release.** The mod is in early development and may contain bugs or incomplete features. Use at your own risk.

## Phase 3 Features - Fully Functional

This release contains Phase 1-3 features. Phase 4 features are documented for preview but are not yet functional due to Minecraft 1.21 API compatibility issues.

## Included Features (Phase 1-3)

### Configuration System
- SignScribeConfig - Fully configurable mod options:
  - autoAdvance - Automatically advance to next sign after placement
  - autoAdvanceDelay - Configure delay in ticks (default: 20)
  - showPreview - Display text preview before sign placement
  - requireEmptyHand - Require empty hand to place signs
  - placeSignsInstantly - Place signs without right-click confirmation
  - showSuccessMessage - Display success messages on sign placement

### File Management
- SignScribeFileManager - Complete file operation support:
  - Load .txth files from config/signscribe/txth/ directory
  - Save .txth files to config directory
  - List all available .txth files
  - Check file existence
  - Automatic directory creation on startup

### Data Persistence
- SignScribeData - NBT-based storage system:
  - Saves session state to config/signscribe/data.dat
  - Persists between game sessions
  - Tracks current .txth filename
  - Saves current page index and total signs
  - Restores active session on game load

### Session Management
- SignScribePlacement - Complete session lifecycle:
  - Start placement sessions with .txth files
  - Navigate pages (next/previous)
  - Jump to specific page numbers
  - Track session boundaries (first/last page)
  - End sessions and save state

### User Interface
- SignScribeFileScreen - File selection GUI:
  - Browse all .txth files from config directory
  - Click to load and start session
  - Display current session status (file, page/total)
  - Error messages for file load/parse failures
  - Close button to return to game

### Commands
- Client-side commands - Full control from chat:
  - /signscribe open - Open file selection GUI
  - /signscribe next - Advance to next sign page
  - /signscribe prev - Go to previous sign page
  - /signscribe status - Show current session info
  - /signscribe stop - End current placement session

### Testing
- 21 comprehensive tests across 4 test classes
- All tests passing

## Preview of Phase 4 Features (Not Implemented Yet)

### Sign Placement & Interaction
- Sign placement in world (right-click)
- Sign text rendering
- Text preview system
- Keybinds for navigation
- ModMenu configuration GUI
- Auto-advance timer
- Undo/redo functionality

## Installation

1. Download SignScribe-1.0.0-alpha3.jar
2. Place in .minecraft/mods/
3. Launch Minecraft 1.21 with Fabric Loader 0.15.11+
4. Required: Fabric API 0.100.4+1.21

## Known Limitations

### Not Implemented
- Actual sign placement in world
- Sign text rendering on placed signs
- Text preview before placement
- Right-click interaction with signs
- ModMenu configuration GUI (stub only)
- Auto-advance timer implementation
- Keybinds for quick navigation
- Undo/redo for sign placement

### Current Capabilities
- Configuration system fully functional
- File operations working
- Data persistence operational
- Session management complete
- GUI and commands ready
- All tests passing

## Known Issues

As an alpha release, you may encounter:
- Unexpected behavior
- Missing features
- Breaking changes between versions
- Sign placement features are preview only and not functional

## Bug Reports

Found a bug? Report it at:
https://github.com/RZZW304/SignScribe/issues

## Credits

Development: RZZW304
Version: v1.0.0-alpha3
License: ALL RIGHTS RESERVED - Private use only

## Status

Total tasks: 120
Completed: 29 tasks (24%)
Remaining: 91 tasks (76%)
Next: Phase 4 - Sign Placement & Interaction

Thank you for testing SignScribe v1.0.0 Alpha 3
