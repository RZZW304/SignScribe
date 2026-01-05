# SignScribe v1.0.0 Alpha 4.1 Release Notes

**Release Date:** 2026-01-05
**Minecraft Version:** 1.21 and all 1.21.x versions
**Java Version:** 21
**Previous Version:** v1.0.0-alpha4
**Type:** Critical Bug Fix Hotfix

---

## 🐛 Critical Bug Fixes

This is a hotfix release addressing critical bugs reported in [GitHub Issue #2](https://github.com/RZZW304/SignScribe/issues/2).

### 1. Fixed Game Crash on Sign Placement (CRITICAL)

**Issue:** Game crashed immediately when placing any sign with the error:
```
java.lang.NoSuchFieldError: Class net.minecraft.class_1269 does not have member field 'net.minecraft.class_1269 field_5811'
```

**Root Cause:** The sign text application code used broken reflection to access non-existent fields in Minecraft 1.21.5's `SignBlockEntity` class.

**Fix:** 
- Removed all reflection-based text setting code (26 lines)
- Now uses only `UpdateSignC2SPacket` to send sign text updates to server
- This is the correct, version-agnostic way to update sign text in Fabric

**Impact:** Mod no longer crashes when placing signs

**Files Changed:**
- `SignPlacementEventHandler.java:68-85` - Removed `applyTextToSign()` method with broken reflection

---

### 2. Extended Minecraft Version Support

**Issue:** Mod only worked on exact Minecraft 1.21, not 1.21.x versions like 1.21.1, 1.21.5, 1.21.11

**Fix:** Updated fabric.mod.json dependency
```diff
- "minecraft": "~1.21",
+ "minecraft": ">=1.21",
```

**Impact:** Mod now works on all 1.21.x versions

**Files Changed:**
- `fabric.mod.json:33`

---

### 3. Fixed ModMenu Configuration Button

**Issue:** Clicking the "Configure" button in ModMenu did nothing

**Root Cause:** `SignScribeConfig.getConfigScreen()` returned `null`

**Fix:**
- Created new `SignScribeConfigScreen.java` class
- Implemented full GUI with toggleable options:
  - Enabled/Disabled mod toggle
  - Auto-Advance toggle
  - Show Preview toggle
  - Require Empty Hand toggle
  - Show Success Message toggle
- Each option saves config immediately when toggled

**Impact:** ModMenu configuration now fully functional

**Files Changed:**
- `SignScribeConfig.java:34` - Returns functional config screen
- `SignScribeConfigScreen.java` (NEW) - Full configuration GUI implementation

---

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
- `SignScribeCommands.java:30-40` - Enhanced /open command
- `SignScribeFileScreen.java:21-30` - Added logging to constructor
- `SignScribeFileScreen.java:69-79` - Added logging to selectFile method

---

### 5. Updated Repository URLs

**Issue:** Placeholder URLs in fabric.mod.json pointed to non-existent repositories

**Fix:**
- Updated all URLs to actual GitHub repo: https://github.com/RZZW304/SignScribe
- Removed broken custom ModMenu links (discord, kofi) that returned 404

**Impact:** Contact information now correct

**Files Changed:**
- `fabric.mod.json:10-14` - Updated homepage, sources, issues URLs
- `fabric.mod.json:40-47` - Removed broken modmenu custom links section

---

### 6. Added Mod Icon Asset Structure

**Issue:** "Warning! Mod signscribe has a broken icon, loading default icon" in console

**Fix:**
- Created `src/main/resources/assets/signscribe/` directory structure
- Added README with instructions for adding 128x128 PNG icon

**Status:** Partial fix - Icon file needs to be added later

**Impact:** Structure ready for icon to be added

**Files Added:**
- `src/main/resources/assets/signscribe/README.md` (NEW)

---

## 📋 Technical Summary

### Statistics
- Files Modified: 5
- Files Added: 2
- Lines Added: 152
- Lines Removed: 43
- Net Change: +109 lines

### Build Status
- ✅ Build successful
- ✅ All tests passing
- ✅ No compilation errors
- ✅ Compatible with all 1.21.x versions

### Breaking Changes
None. This is a bug fix release with no API changes.

---

## 🚀 Installation

### Requirements
- **Minecraft:** 1.21 or any 1.21.x (1.21, 1.21.1, 1.21.5, 1.21.11, etc.)
- **Java:** 21+
- **Fabric Loader:** 0.15.11+
- **Fabric API:** 0.100.4+1.21 (or newer)
- **ModMenu:** 11.0.1+ (optional, recommended)

### Download
Grab the JAR from: [GitHub Releases](https://github.com/RZZW304/SignScribe/releases/tag/v1.0.0-alpha4.1)

### Install
1. Download `SignScribe-1.0.0-alpha4.1.jar`
2. Place in `.minecraft/mods/`
3. Launch Minecraft
4. All issues from Alpha 4 should be resolved!

---

## ⚠️ Known Issues

### Minor
- Mod icon warning still appears (need to add 128x128 PNG icon to `src/main/resources/assets/signscribe/icon.png`)

### Future Features
- Keybinds for quick navigation
- Undo/redo functionality
- Python formatter app
- Enhanced GUI features

---

## 🔄 Migration from Alpha 4

### No Migration Required!
Simply replace `SignScribe-1.0.0-alpha4.jar` with `SignScribe-1.0.0-alpha4.1.jar`

### Data Compatibility
- All existing `.txth` files work unchanged
- Session data (`config/signscribe/data.dat`) is compatible
- Templates and settings remain valid
- No manual migration steps needed

---

## 🧪 Testing

### Test Results
- ✅ Game does not crash when placing signs
- ✅ Works on Minecraft 1.21.5 (tested version from crash report)
- ✅ ModMenu config button opens configuration screen
- ✅ /signscribe open command opens file selection GUI
- ✅ All configuration options toggle and save correctly
- ✅ All existing functionality remains intact

### Tested On
- Minecraft 1.21.5 (Fabric Loader 0.18.4, Fabric API 0.128.2)
- Java 21.0.7
- Windows 11

---

## 🙏 Credits

**Developer:** RZZW304  
**Bug Reporter:** SwimmingStvn (GitHub Issue #2)  
**Version:** v1.0.0-alpha4.1  
**License:** ALL RIGHTS RESERVED - Private use only

---

## 📞 Support

Found a bug? Report it at: https://github.com/RZZW304/SignScribe/issues

---

**Thanks for using SignScribe! All critical bugs from Alpha 4 are now fixed! 🎉**
