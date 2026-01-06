# SignScribe

A Minecraft Fabric mod for automatically placing pre-formatted text onto signs.

## What is SignScribe

SignScribe lets you automatically copy pre-formatted stories from .txth files onto signs in Minecraft. Good for creating stories, shop signs, adventure logs, or any multi-sign content without typing each line manually.

## Key Features

- Automatic Sign Placement - Right-click any sign to auto-apply text
- Pre-formatted Files - Use .txth files with exact formatting
- Auto-Advance - Automatically moves to next sign on each placement
- Session Persistence - Remembers your position between game sessions
- Template System - Save and reuse sign layouts
- Statistics - Track your sign placement history
- Batch Operations - Manage multiple files at once
- Backup/Restore - Full system backup capabilities

## Features Overview

### Core Features

| Feature | Status | Description |
|---------|----------|-------------|
| Sign Placement | Implemented | Right-click signs to auto-apply text from loaded .txth files |
| File Parsing | Implemented | Parse .txth files with validation (14 chars/line, 4 lines/sign) |
| Session Management | Implemented | Load/unload sessions, track current position, auto-save |
| Data Persistence | Implemented | NBT-based storage, survives game restarts |
| Auto-Advance | Implemented | Automatically move to next sign after placement |
| Progress Notifications | Implemented | Chat messages showing sign progress |

### Advanced Features

| Feature | Status | Description |
|---------|----------|-------------|
| Sign Templates | Implemented | Save reusable sign text patterns with usage tracking |
| Session Templates | Implemented | Save entire .txth file configurations as templates |
| Batch Operations | Implemented | Rename, delete, copy, validate multiple files at once |
| File Filtering | Implemented | Filter by name, date, size; sort by multiple criteria |
| Session History | Implemented | Track all sessions with timestamps and statistics |
| Statistics | Implemented | Comprehensive analytics (sessions, signs, time, popular files) |
| Import/Export | Implemented | ZIP-based backup and data transfer |
| Backup System | Implemented | Scheduled backups with automatic rotation |

### User Interface

| Feature | Status | Description |
|---------|----------|-------------|
| File Selection GUI | Implemented | Browse and select .txth files |
| Chat Commands | Implemented | All commands available |
| ModMenu Config | Implemented | Toggle mod, set options, view status |
| Keybinds | Planned | Quick navigation without commands |

## Installation

### Requirements

- Minecraft: 1.21.5
- Java: 21+
- Fabric Loader: 0.16.9+
- Fabric API: 0.128.2+1.21.5
- ModMenu: 11.0.1+ (optional, recommended)

### Download

Grab the latest release from Releases or build from source.

### Install

1. Download SignScribe-1.0.0-alpha5.7.jar
2. Place the file in .minecraft/mods/
3. Launch Minecraft 1.21.5 with Fabric Loader
4. Ensure Fabric API is installed

### Directory Structure

After first launch, SignScribe creates:

```
config/signscribe/
├── txth/                    # Place your .txth files here
│   ├── story1.txth
│   └── adventure.txth
├── templates/                # Sign text templates
│   ├── shop_sign.template
│   └── warning_sign.template
├── session_templates/        # Session configuration templates
│   └── adventure_story.zip
├── session_history.log       # All past sessions
├── data.dat                 # Current session state (NBT)
└── signscribe.properties     # Configuration file
```

## Usage Guide

### Quick Start

1. Prepare your .txth file (see format below)
2. Place it in config/signscribe/txth/
3. Load the file using:
   ```
   /signscribe load
   ```
   Then select your file from the GUI
4. Check status:
   ```
   /signscribe status
   # Output: Session: story.txth (1/50 signs)
   ```
5. Place signs! Just right-click any sign block

### Basic Workflow

```
1. /signscribe load          → Open file selection GUI
2. [Click your .txth file]  → Load file, start session
3. Place a sign               → Auto-applies SIGN1 text
4. Place another sign         → Auto-applies SIGN2 text
5. ...                        → Continues through all signs
6. /signscribe status         → Check your progress
7. /signscribe stop           → End session when done
```

## Commands

All commands are client-side (start with /signscribe):

| Command | Description | Example |
|---------|-------------|----------|
| /signscribe load | Open file selection GUI | /signscribe load |
| /signscribe next | Advance to next sign | /signscribe next |
| /signscribe prev | Go to previous sign | /signscribe prev |
| /signscribe status | Show current session info | /signscribe status |
| /signscribe stop | End current session | /signscribe stop |
| /signscribe on | Enable mod | /signscribe on |
| /signscribe off | Disable mod | /signscribe off |
| /signscribe sign | Jump to current sign | /signscribe sign |

### Status Output

```
/signscribe status

→ Session: adventure_story.txth (5/100 signs)
```

## .txth File Format

### Structure

The .txth file format is strict and pre-formatted:

```
SIGN1:
This is line 1 (14 chars)
This is line 2 (14 chars)
This is line 3 (14 chars)
This is line 4 (14 chars)

SIGN2:
{BLANk}              ← Intentional blank line
Another line here
14 chars per line
Exactly!

SIGN3:
Story continues here
Every sign has 4 lines
Each line is 14 chars
No exceptions
```

### Validation Rules

- Exactly 4 lines per sign block
- Exactly 14 characters per line
- Signs separated by SIGN1:, SIGN2:, etc.
- Use {BLANk} for intentional blank lines
- Empty lines without {BLANk} will cause errors
- Lines longer than 14 characters will cause errors

### Example .txth File

```txth
SIGN1:
Once upon a time
In a land far away
There lived a hero
Who changed the world

SIGN2:
The journey began
With a single step
Through forests dark
And mountains tall

SIGN3:
{BLANk}
But hope remained
Bright as the stars
Guiding the way
Home once more
```

## Python Formatter App

Status: Not implemented yet

The Python Formatter App will make creating .txth files effortless. It will convert any plain text file into the strict .txth format automatically.

## Templates & Advanced Features

### Sign Templates

Save frequently used sign layouts:

```properties
# config/signscribe/templates/shop.template
name=Shop Sign
description=Common shop sign
author=YourName
line1=Welcome to my
line2=shop! Buy items
line3=on the right
line4=Thank you!
```

### Session Templates

Save entire .txth file configurations:

Export current session using the import/export functionality to create session templates.

### Batch Operations

Use the BatchOperations class to manage multiple files at once:

- Validate multiple files
- Apply templates to multiple files
- Rename multiple files

### Statistics

Track your sign placement history with comprehensive analytics including:

- Total sessions
- Total signs placed
- Time spent
- Most used files

### Backup & Restore

Full backup and restore functionality to protect your data.

## Configuration

### Config Options

Edit config/signscribe/signscribe.properties or use ModMenu configuration:

```properties
# Auto-advance to next sign after placement
autoAdvance=true

# Delay before auto-advance (ticks, 20 = 1 second)
autoAdvanceDelay=20

# Show sign preview before placement
showPreview=true

# Require empty hand to place signs
requireEmptyHand=true

# Place signs instantly without confirmation
placeSignsInstantly=false

# Show success messages in chat
showSuccessMessage=true
```

## Troubleshooting

### Common Issues

Q: Signs show wrong text?
A: Ensure your .txth file has exactly 14 characters per line. Check for extra spaces.

Q: Session not saving?
A: Check config/signscribe/data.dat exists and is writable.

Q: Mod not working?
A: Ensure Fabric API is installed and you're using Minecraft 1.21.5.

Q: "There are only X signs" error?
A: You tried to jump past the last sign in your file.

### Getting Help

- Report Issues on GitHub
- Check documentation wiki

## Building from Source

### Prerequisites

- Java 21+
- Gradle 8.7+

### Build Steps

```bash
# Clone repository
git clone https://github.com/RZZW304/SignScribe.git
cd SignScribe

# Build
./gradlew build

# Output
build/libs/SignScribe-1.0.0-alpha5.7.jar
```

### Run Tests

```bash
./gradlew test
```

## Development Roadmap

### Alpha 5.7 (Current)
- Sign placement logic
- Advanced data management
- Templates & batch operations
- Statistics & backups

### Future Alpha Releases
- Keybinds for navigation
- Enhanced GUI features
- Undo/redo functionality

### Beta Releases
- Python Formatter App
- Full documentation
- Stability improvements
- Performance optimizations

### 1.0.0 Release
- All features polished
- Extensive testing
- Video tutorials
- Community templates

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

Note: This is a private use project. All rights reserved.

## License

ALL RIGHTS RESERVED - Private use only

This mod is for personal use only. Distribution, modification, or commercial use is prohibited without explicit permission.

## Credits & Acknowledgments

- Development: RZZW304
- Inspiration: SignStory mod (Forge)
- Special Thanks: Fabric team, Yarn mappings community

## Project Statistics

- Lines of Code: ~5,000+
- Classes: 20+
- Development Time: Ongoing since Jan 2025
- Current Version: 1.0.0-alpha5.7

## Version Information

This project is currently in alpha development. Features are still being added and bugs may exist. Use at your own risk.

Back to Top
