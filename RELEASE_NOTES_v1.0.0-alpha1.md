# SignScribe v1.0.0 Alpha 1 Release Notes

This is the first alpha release of SignScribe, a Minecraft Fabric mod for easily applying pre-formatted text to signs.

## What's Included

### Phase 1: Project Setup ✅
- Complete Fabric mod project structure
- Configured for Minecraft 1.21.x
- Fabric API and ModMenu integration
- Gradle build system

### Phase 2: File Format & Parsing ✅
- **.txth file parser** with full validation
- SignPage data model (4 lines per sign)
- TxthParseException for detailed error reporting
- Test suite with 4 test scenarios

## File Format (.txth)

Each sign must have:
- Exactly 4 lines
- Exactly 14 characters per line
- SIGN1:, SIGN2: markers
- Use `{BLANk}` for intentional blank lines

Example:
```
SIGN1:
Once upon a time
in a land so far
there lived a hobbit
who loved to write
```

## Installation

1. Download `SignScribe-1.0.0.jar` from build/libs/
2. Place in your Minecraft `mods` folder
3. Requires Minecraft 1.21, Fabric Loader, and Fabric API

## Limitations

This is an alpha release. Only file parsing is implemented:
- ❌ No sign placement functionality yet
- ❌ No GUI for loading files
- ❌ No commands
- ❌ No ModMenu configuration

## Testing

The mod will load in Minecraft but has no gameplay features yet.
File parsing has been tested with:
- Valid files (4 signs with {BLANk})
- Empty line detection
- Line count validation
- Line length validation

## License

ALL RIGHTS RESERVED - Private use only

---

**Status:** Phase 1 & 2 complete (21/120 total tasks)
**Next:** Phase 3 - Data Storage & Configuration
