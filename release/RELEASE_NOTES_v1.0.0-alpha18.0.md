# SignScribe v1.0.0 Alpha 18.0 Release Notes

**Release Date:** 2026-01-06
**Minecraft Version:** 1.21.5
**Java Version:** 21+
**Type:** Bug Fix

## Fixed Issues

### Chat Messages Not Appearing When Placing Signs

**Issue:** Progress chat messages did not display when placing signs.

**Root Cause:** The UseBlockCallback event fires before a sign block exists, so it could not detect newly placed signs.

**Solution:** Added ClientBlockEntityEvents.BLOCK_ENTITY_LOAD callback that fires after sign entities are created. This properly detects both new and existing signs.

**Changes:**
- Registered two event callbacks: UseBlockCallback for existing signs and BLOCK_ENTITY_LOAD for new signs
- Simplified handler logic by consolidating sign text application
- All operations now execute on the client thread for proper timing
- Added enhanced debug logging

**Impact:** Chat messages now correctly show when you place signs, displaying progress like "Sign 1/50 placed".

## Installation

1. Place SignScribe-1.0.0-alpha18.0.jar in .minecraft/mods/
2. Launch Minecraft 1.21.5 with Fabric Loader
3. Ensure Fabric API 0.128.2+1.21.5 is installed

## Testing

1. Load a .txth file using /signscribe load
2. Place signs in-game
3. Verify chat messages appear showing sign progress

## Known Issues

None reported.

## Next Steps

- Add keybinds for quick navigation
- Enhanced GUI features
- Undo/redo functionality
