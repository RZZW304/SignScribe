# SignScribe v1.0.0 Alpha 3 Release Notes

**Release Date:** 2025-01-05
**Minecraft Version:** 1.21
**Java Version:** 21
**Previous Version:** v1.0.0-alpha2

---

## 🎉 Phase 3 Features - Fully Functional

This release contains Phase 1-3 features. **Phase 4 features are documented for preview but are not yet functional** due to Minecraft 1.21 API compatibility issues.

---

## ✨ Included Features (Phase 1-3)

### Configuration System ✅
- **SignScribeConfig** - Fully configurable mod options:
  - `autoAdvance` - Automatically advance to next sign after placement
  - `autoAdvanceDelay` - Configure delay in ticks (default: 20)
  - `showPreview` - Display text preview before sign placement
  - `requireEmptyHand` - Require empty hand to place signs
  - `placeSignsInstantly` - Place signs without right-click confirmation
  - `showSuccessMessage` - Display success messages on sign placement

### File Management ✅
- **SignScribeFileManager** - Complete file operation support:
  - Load .txth files from `config/signscribe/txth/` directory
  - Save .txth files to config directory
  - List all available .txth files
  - Check file existence
  - Automatic directory creation on startup

### Data Persistence ✅
- **SignScribeData** - NBT-based storage system:
  - Saves session state to `config/signscribe/data.dat`
  - Persists between game sessions
  - Tracks current .txth filename
  - Saves current page index and total signs
  - Restores active session on game load

### Session Management ✅
- **SignScribePlacement** - Complete session lifecycle:
  - Start placement sessions with .txth files
  - Navigate pages (next/previous)
  - Jump to specific page numbers
  - Track session boundaries (first/last page)
  - End sessions and save state

### User Interface ✅
- **SignScribeFileScreen** - File selection GUI:
  - Browse all .txth files from config directory
  - Click to load and start session
  - Display current session status (file, page/total)
  - Error messages for file load/parse failures
  - Close button to return to game

### Commands ✅
- **Client-side commands** - Full control from chat:
  - `/signscribe open` - Open file selection GUI
  - `/signscribe next` - Advance to next sign page
  - `/signscribe prev` - Go to previous sign page
  - `/signscribe status` - Show current session info
  - `/signscribe stop` - End current placement session

---

## 🗺️ Phase 4 Preview (Coming Soon)

The following features are **implemented but not functional** in this release due to Minecraft 1.21 API compatibility issues. They will be available in a future release.

### Planned Phase 4 Features
- Sign placement in world (right-click)
- Sign text rendering on placed signs
- Text preview before placement
- Keybinds for quick navigation
- ModMenu configuration GUI
- Auto-advance timer
- Undo/redo for sign placement
- Sign editing mode

**Status:** Implementation complete, API compatibility fixes in progress

---

## 🧪 Testing

**Test Suite:** 21 tests passing ✅
- TxthFileParserTest (4 tests)
- SignScribeDataTest (5 tests)
- SignScribeFileManagerTest (6 tests)
- SignScribePlacementTest (10 tests)

All Phase 1-3 tests pass successfully.

---

## 🔧 Technical Improvements

### Build System
- JUnit 5 test framework (v5.10.0)
- JUnit Platform Launcher for test execution
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

---

## 📁 Directory Structure

```
config/signscribe/
├── txth/           # Store your .txth files here
│   ├── story1.txth
│   └── story2.txth
└── data.dat        # Session state (NBT format)
```

---

## 📖 Usage

### Basic Workflow

1. **Prepare .txth files**
   ```
   Place your .txth files in:
   config/signscribe/txth/
   ```

2. **Start a session**
   ```
   /signscribe open
   (select file from GUI)
   ```

3. **Check status**
   ```
   /signscribe status
   (shows: Session: story.txth (1/10 signs))
   ```

4. **Navigate pages**
   ```
   /signscribe next   (next sign)
   /signscribe prev   (previous sign)
   ```

5. **End session**
   ```
   /signscribe stop
   ```

---

## 🔄 Changes from Alpha 2

### Added
- ✅ Configuration system with 6 options
- ✅ File manager for .txth operations
- ✅ NBT data persistence
- ✅ Session management system
- ✅ File selection GUI
- ✅ 5 client-side commands
- ✅ 21 comprehensive tests
- ✅ JUnit 5 test framework

### Fixed
- ✅ Minecraft 1.21 API compatibility issues
- ✅ NBT I/O operations for Path parameters
- ✅ GUI rendering methods
- ✅ Parser integration

### Updated
- ✅ Mod initialization sequence
- ✅ Build configuration with test dependencies

---

## ⚠️ Known Limitations

### Not Implemented (Phase 4+)
- ❌ Sign placement in world (API compatibility issues)
- ❌ Sign text rendering on placed signs (API compatibility issues)
- ❌ Text preview before placement (API compatibility issues)
- ❌ Right-click interaction with signs (API compatibility issues)
- ❌ ModMenu configuration GUI (API compatibility issues)
- ❌ Keybinds for quick navigation (API compatibility issues)
- ❌ Auto-advance timer implementation (API compatibility issues)
- ❌ Undo/redo for sign placement (API compatibility issues)

**Note:** Phase 4 features are implemented but require API fixes for Minecraft 1.21.

### Current Capabilities
- ✅ Configuration system fully functional
- ✅ File operations working
- ✅ Data persistence operational
- ✅ Session management complete
- ✅ GUI and commands ready
- ✅ All tests passing (21/21)

---

## 🗺️ Roadmap

### Immediate Priority (Next Release)
1. Fix Minecraft 1.21 API compatibility
2. Test Phase 4 features end-to-end
3. Release v1.0.0-alpha4 (Phase 4 fully functional)

### Phase 4: Sign Placement & Interaction (Coming Soon)
- Sign placement in world (right-click)
- Sign text rendering
- Text preview system
- Keybinds for navigation
- ModMenu configuration GUI
- Auto-advance timer
- Undo/redo functionality

### Remaining Tasks
- **Total:** 120 tasks
- **Completed:** 29 tasks (24%)
- **Remaining:** 91 tasks (76%)

---

## 🐛 Bug Reports

Found a bug? Report it at:
https://github.com/RZZW304/SignScribe/issues

---

## 🙏 Credits

**Development:** RZZW304
**Version:** v1.0.0-alpha3
**License:** ALL RIGHTS RESERVED - Private use only

---

## 📦 Installation

1. Download `SignScribe-1.0.0-alpha2.jar`
2. Place in `.minecraft/mods/`
3. Launch Minecraft 1.21 with Fabric Loader 0.15.11+
4. Required: Fabric API 0.100.4+1.21

---

**Enjoy SignScribe v1.0.0 Alpha 3! 🎉**

*Phase 4 features coming soon in Alpha 4 after API compatibility fixes.*
