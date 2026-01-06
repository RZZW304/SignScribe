# SignScribe v1.0.0 Alpha 2 Release Notes

**Release Date:** 2025-01-05
**Minecraft Version:** 1.21
**Java Version:** 21
**Previous Version:** v1.0.0-alpha1

**IMPORTANT: This is an alpha release.** The mod is in early development and may contain bugs or incomplete features. Use at your own risk.

## Phase 3 Complete: Data Storage & Configuration

This release adds comprehensive data storage, configuration options, session management, and user controls to SignScribe.

## New Features

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

## Testing

Added comprehensive test suite with 21 tests across 4 test classes:

- TxthFileParserTest (4 tests) - File parsing validation
- SignScribeDataTest (5 tests) - NBT storage operations
- SignScribeFileManagerTest (6 tests) - File I/O operations
- SignScribePlacementTest (10 tests) - Session management

All tests passing

## Technical Improvements

### Build System
- Added JUnit 5 test framework (v5.10.0)
- Added JUnit Platform Launcher for test execution
- Improved test infrastructure

### Code Quality
- Fixed Minecraft 1.21 API compatibility:
  - Updated NbtIo.readCompressed() for Path parameter
  - Updated NbtIo.writeCompressed() for Path parameter
  - Fixed GUI render methods for 1.21
- Fixed parser integration with SignScribePlacement
- Comprehensive error handling throughout

### Initialization
- Proper mod initialization sequence:
  - SignScribeConfig initialization on mod load
  - SignScribeFileManager setup with config directory
  - SignScribeData load on startup
  - SignScribeCommands registration
  - Automatic directory structure creation

## Directory Structure

```
config/signscribe/
├── txth/           # Store your .txth files here
│   ├── story1.txth
│   └── story2.txth
└── data.dat        # Session state (NBT format)
```

## Usage

### Basic Workflow

1. Prepare .txth files
   ```
   Place your .txth files in:
   config/signscribe/txth/
   ```

2. Start a session
   ```
   /signscribe open
   (select file from GUI)
   ```

3. Check status
   ```
   /signscribe status
   (shows: Session: story.txth (1/10 signs))
   ```

4. Navigate pages
   ```
   /signscribe next   (next sign)
   /signscribe prev   (previous sign)
   ```

5. End session
   ```
   /signscribe stop
   ```

## Changes from Alpha 1

### Added
- Configuration system with 6 options
- File manager for .txth operations
- NBT data persistence
- Session management system
- File selection GUI
- 5 client-side commands
- 21 comprehensive tests
- JUnit 5 test framework

### Fixed
- Minecraft 1.21 API compatibility issues
- NBT I/O operations for Path parameters
- GUI rendering methods
- Parser integration

### Updated
- Mod initialization sequence
- Build configuration with test dependencies

## Known Limitations

### Not Implemented (Phase 4+)
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

## Roadmap

### Phase 4: Sign Placement & Interaction (Next)
- Sign placement in world (right-click)
- Sign text rendering
- Text preview system
- Keybinds for navigation
- ModMenu configuration GUI
- Auto-advance timer
- Undo/redo functionality

### Remaining Tasks
- Total: 120 tasks
- Completed: 29 tasks (24%)
- Remaining: 91 tasks (76%)

## Known Issues

As an alpha release, you may encounter:
- Unexpected behavior
- Missing features
- Breaking changes between versions
- Performance issues

## Bug Reports

Found a bug? Report it at:
https://github.com/RZZW304/SignScribe/issues

## Credits

Development: RZZW304
Version: v1.0.0-alpha2
License: ALL RIGHTS RESERVED - Private use only

## Installation

1. Download SignScribe-1.0.0-alpha2.jar
2. Place in .minecraft/mods/
3. Launch Minecraft 1.21 with Fabric Loader 0.15.11+
4. Required: Fabric API 0.100.4+1.21

Thank you for testing SignScribe v1.0.0 Alpha 2
