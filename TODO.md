# SignScribe TODO List

## Priority Legend
- 🔴 High Priority - Core functionality
- 🟡 Medium Priority - Important features
- 🟢 Low Priority - Nice to have

---

## FABRIC MOD IMPLEMENTATION

### Phase 1: Project Setup
- [x] Set up Fabric development environment for 1.21.x
- [x] Create basic mod structure (mod.json, fabric.mod.json)
- [x] Set up Gradle/loom configuration
- [x] Add Fabric API dependencies
- [x] Add ModMenu dependency
- [x] Create mod entry points (ModInitializer, ClientModInitializer)
- [ ] Test basic mod loads in Minecraft

### Phase 2: File Format & Parsing (Core)
- [x] 🔴 Create .txth file parser class
- [x] 🔴 Parse SIGN1:, SIGN2: markers
- [x] 🔴 Extract 4-line blocks per sign
- [x] 🔴 Validate exactly 4 lines per sign block
- [x] 🔴 Validate exactly 14 characters per line
- [x] 🔴 Detect empty lines without {BLANk} (error)
- [x] 🔴 Convert {BLANk} to actual blank lines
- [x] 🔴 Store sign pages in List<String>
- [x] 🔴 Count total signs in file
- [x] 🔴 Test parser with valid .txth file
- [x] 🔴 Test parser with invalid files (empty lines, wrong counts)

### Phase 3: Data Storage & Configuration (Core)
- [ ] 🔴 Create global state management class
- [ ] 🔴 Store loaded .txth file path
- [ ] 🔴 Store sign pages (List<String>)
- [ ] 🔴 Track current sign number
- [ ] 🔴 Track total signs loaded
- [ ] 🔴 Track mod enabled/disabled state
- [ ] 🟡 Implement configuration storage (Config API)
- [ ] 🟡 Save last loaded file path to config
- [ ] 🟡 Save last placed sign number to config
- [ ] 🟡 Save mod enabled state to config
- [ ] 🔴 Clear saved position when file is unloaded
- [ ] 🟡 Load config on startup
- [ ] 🟡 Test persistence (restart game)

### Phase 4: Sign Placement Logic (Core)
- [x] 🔴 Register sign placement event handler
- [x] 🔴 Hook into sign placement for all sign types (oak, birch, spruce, etc.)
- [x] 🔴 Cancel default sign edit GUI
- [x] 🔴 Get current sign text from loaded .txth file
- [x] 🔴 Apply pre-formatted text to sign TileEntity
- [x] 🔴 Send sign update packet to server
- [x] 🔴 Auto-advance to next sign after placement
- [x] 🔴 Save current sign number to config
- [x] 🟡 Display progress notification (Sign X of Y placed)
- [x] 🟡 Display success notification (Sign text applied)
- [x] 🔴 Test sign placement flow
- [x] 🔴 Test sign progression
- [x] 🔴 Test with all sign types

### Phase 5: GUI Implementation (Important)
- [ ] 🟡 Create file path GUI screen
- [ ] 🟡 Add text field for pasting file path
- [ ] 🟡 Add "Load" button
- [ ] 🟡 Add error message display area
- [ ] 🟡 Add success message display
- [ ] 🟡 Handle file path validation
- [ ] 🟡 Load .txth file and parse
- [ ] 🟡 Close GUI on successful load
- [ ] 🟡 Test file path GUI

### Phase 6: ModMenu Configuration (Important)
- [ ] 🟡 Create ModMenu configuration screen
- [ ] 🟡 Add mod on/off toggle switch
- [ ] 🟡 Add "Select starting sign number" input field
- [ ] 🟡 Add "Paste file path" text field
- [ ] 🟡 Add "Load file" button
- [ ] 🟡 Display current status:
  - [ ] 🟡 Mod enabled/disabled
  - [ ] 🟡 Current sign number
  - [ ] 🟡 Total signs loaded
  - [ ] 🟡 Loaded file name
- [ ] 🟡 Implement "Apply changes" functionality
- [ ] 🟡 Test ModMenu configuration

### Phase 7: Commands (Important)
- [ ] 🟡 Register `/signscribe` command
- [ ] 🟡 Implement `/signscribe on` subcommand
- [ ] 🟡 Implement `/signscribe off` subcommand
- [ ] 🟡 Implement `/signscribe sign <number>` subcommand
  - [ ] 🟡 Validate number ≤ total signs
  - [ ] 🟡 Show error: "There are only [number] signs"
  - [ ] 🟡 Default to last placed sign or 1
  - [ ] 🟡 Update current sign number
- [ ] 🟡 Implement `/signscribe load` subcommand (opens file path GUI)
- [ ] 🟡 Add command help messages
- [ ] 🟡 Test all commands

### Phase 8: Error Handling & User Feedback (Important)
- [ ] 🔴 Add .txth file validation errors
- [ ] 🔴 Specify which sign and line has errors
- [ ] 🔴 Refuse to load malformed files
- [ ] 🔴 Display error messages in chat
- [ ] 🔴 Display error messages in GUI
- [ ] 🟡 Add sign placement errors (no file loaded, sign out of range)
- [ ] 🟡 Add file loading errors (file not found, invalid format)
- [ ] 🟡 Add permission errors (if applicable)
- [ ] 🟡 Test error scenarios

### Phase 9: Testing & Polish (Important)
- [ ] 🟡 Test .txth file parsing with all formats
- [ ] 🟡 Test error detection (empty lines, wrong line count)
- [ ] 🔴 Test sign placement flow
- [ ] 🔴 Test sign progression
- [ ] 🟡 Test persistence (remember last sign, clear on unload)
- [ ] 🟡 Test ModMenu configuration
- [ ] 🟡 Test all commands
- [ ] 🟡 Test with all sign types
- [ ] 🟡 Test in singleplayer
- [ ] 🟡 Test in multiplayer
- [ ] 🟡 Test with large .txth files (100+ signs)
- [ ] 🟡 Add user feedback notifications
- [ ] 🟡 Polish error messages

### Phase 10: Build & Release (Final)
- [ ] 🟢 Build .jar file
- [ ] 🟢 Test .jar in clean Minecraft installation
- [ ] 🟢 Create README with usage instructions
- [ ] 🟢 Add .txth file format documentation
- [ ] 🟢 Add command reference
- [ ] 🟢 Create example .txth files
- [ ] 🟢 Package for distribution
- [ ] 🟢 Final testing

---

## EXTERNAL FORMATTER APP (Python)

### Phase 1: App Setup
- [ ] Set up Python 3.11+ environment
- [ ] Choose GUI framework (PyQt6 or Tkinter)
- [ ] Create project structure
- [ ] Set up virtual environment
- [ ] Create requirements.txt
- [ ] Create basic window structure
- [ ] Test basic GUI opens

### Phase 2: File Input (Important)
- [ ] 🟡 Add "Open .txt file" button
- [ ] 🟡 Create file selection dialog
- [ ] 🟡 Read file contents (UTF-8)
- [ ] 🟡 Display text preview area
- [ ] 🟡 Handle file not found errors
- [ ] 🟡 Test with various text files

### Phase 3: Text Processing (Core)
- [ ] 🔴 Implement text wrapping algorithm (14 chars/line)
- [ ] 🔴 Handle long words (>14 chars) - chop at 14 chars
- [ ] 🔴 Handle newlines properly (preserve intentional line breaks)
- [ ] 🔴 Replace intentional blank lines with {BLANk}
- [ ] 🔴 Test wrapping with simple text
- [ ] 🔴 Test with long words
- [ ] 🔴 Test with newlines
- [ ] 🔴 Test with blank lines

### Phase 4: Format Conversion (Core)
- [ ] 🔴 Split text into 4-line blocks (56 chars per sign)
- [ ] 🔴 Add SIGN1:, SIGN2: prefixes
- [ ] 🔴 Validate line lengths (exactly 14 chars)
- [ ] 🔴 Pad lines with spaces to exactly 14 chars
- [ ] 🔴 Detect intentional blank lines
- [ ] 🔴 Mark intentional blank lines with {BLANk}
- [ ] 🔴 Test conversion with simple text
- [ ] 🔴 Test with long text
- [ ] 🔴 Test edge cases

### Phase 5: GUI Features (Important)
- [ ] 🟡 Add "Select output .txth file" button
- [ ] 🟡 Add file save dialog
- [ ] 🟡 Add "Convert" button
- [ ] 🟡 Add preview area for formatted output
- [ ] 🟡 Add progress indicator
- [ ] 🟡 Add error display panel
- [ ] 🟡 Add success notification
- [ ] 🟡 Add "Reset" button
- [ ] 🟡 Test GUI workflow

### Phase 6: Output Generation (Core)
- [ ] 🔴 Write .txth file with proper format
- [ ] 🔴 Save file to selected location
- [ ] 🔴 Validate file was written correctly
- [ ] 🔴 Show success notification with file path
- [ ] 🔴 Handle write errors
- [ ] 🔴 Test output file with Fabric mod

### Phase 7: Testing (Important)
- [ ] 🟡 Test with simple story (5-10 signs)
- [ ] 🟡 Test with long story requiring auto-wrap
- [ ] 🟡 Test edge cases:
  - [ ] 🟡 Very long words (>14 chars)
  - [ ] 🟡 Empty lines
  - [ ] 🟡 Special characters
  - [ ] 🟡 Unicode characters
  - [ ] 🟡 Numbers
- [ ] 🟡 Test {BLANk} replacement
- [ ] 🟡 Test line padding (exactly 14 chars)
- [ ] 🟡 Test with large files (1000+ lines)
- [ ] 🟡 Test file overwrites
- [ ] 🟡 Test file permissions

### Phase 8: Build & Release (Final)
- [ ] 🟢 Set up PyInstaller for .exe generation
- [ ] 🟢 Build .exe file
- [ ] 🟢 Test .exe on clean Windows machine
- [ ] 🟢 Create README with usage instructions
- [ ] 🟢 Add screenshots of GUI
- [ ] 🟢 Add .txth format documentation
- [ ] 🟢 Create example files
- [ ] 🟢 Package for distribution
- [ ] 🟢 Final testing

---

## DOCUMENTATION

### User Documentation
- [ ] Create README.md for Fabric mod
- [ ] Create README.md for Formatter app
- [ ] Document .txth file format
- [ ] Document all commands
- [ ] Document ModMenu configuration
- [ ] Create usage examples
- [ ] Create troubleshooting guide

### Developer Documentation
- [ ] Document code structure
- [ ] Document key classes and methods
- [ ] Add inline code comments
- [ ] Document build process
- [ ] Document testing procedures

---

## CURRENT STATUS
- [x] Requirements gathering complete
- [x] Implementation plan created
- [x] TODO list created
- [x] Phase 1: Project Setup (partial - needs testing)
- [x] Phase 2: File Format & Parsing (complete)
- [ ] Ready to start Phase 3 (Data Storage & Configuration)

---

## NEXT STEPS
1. Test basic mod loads in Minecraft (complete Phase 1)
2. Implement global state management (Phase 3)
3. Implement sign placement logic (Phase 4)
4. Create file path GUI (Phase 5)
5. Implement commands (Phase 6)
6. Create ModMenu configuration screen (Phase 5)
7. Test entire flow
8. Start external formatter app
9. Build and release both components

**Total Tasks:** ~120 tasks
**Estimated Timeline:** 4-6 weeks for beginner level (5/100)
