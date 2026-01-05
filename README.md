# SignScribe ✍️

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0--alpha5.7-green)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.5-brightgreen)
![Fabric](https://img.shields.io/badge/Fabric-Loader-blue)
![License](https://img.shields.io/badge/license-Private%20Use-red)
![Java](https://img.shields.io/badge/Java-21-orange)

**A Minecraft Fabric mod for effortlessly placing stories on signs**

[![Installation Guide](#-installation)](https://img.shields.io/badge/docs-installation-blue)
[![Usage](#-usage-guide)](https://img.shields.io/badge/docs-usage-brightgreen)
[![Commands](#-commands)](https://img.shields.io/badge/docs-commands-yellow)
[![Python App](#-python-formatter-app)](https://img.shields.io/badge/docs-python-purple)

</div>

---

## 📖 What is SignScribe?

SignScribe is a powerful Minecraft Fabric mod that allows you to automatically copy pre-formatted stories from `.txth` files onto signs. Perfect for creating immersive stories, shop signs, adventure logs, or any multi-sign content without manually typing each line.

### ✨ Key Features

- 🎯 **Automatic Sign Placement** - Right-click any sign to auto-apply text
- 📄 **Pre-formatted Files** - Use `.txth` files with exact formatting
- ➡️ **Auto-Advance** - Automatically moves to next sign on each placement
- 💾 **Session Persistence** - Remembers your position between game sessions
- 🎨 **Template System** - Save and reuse sign layouts
- 📊 **Statistics** - Track your sign placement history
- 🔄 **Batch Operations** - Manage multiple files at once
- 💾 **Backup/Restore** - Full system backup capabilities
- 🐍 **Python Formatter App** - Convert normal text to `.txth` format

---

## 🎯 Features Overview

### Core Mod Features

| Feature | Status | Description |
|---------|----------|-------------|
| Sign Placement | ✅ Complete | Right-click signs to auto-apply text from loaded `.txth` files |
| File Parsing | ✅ Complete | Parse `.txth` files with validation (14 chars/line, 4 lines/sign) |
| Session Management | ✅ Complete | Load/unload sessions, track current position, auto-save |
| Data Persistence | ✅ Complete | NBT-based storage, survives game restarts |
| Auto-Advance | ✅ Complete | Automatically move to next sign after placement |
| Progress Notifications | ✅ Complete | Chat messages showing "Sign X/Y placed" |

### Advanced Features

| Feature | Status | Description |
|---------|----------|-------------|
| Sign Templates | ✅ Complete | Save reusable sign text patterns with usage tracking |
| Session Templates | ✅ Complete | Save entire `.txth` file configurations as templates |
| Batch Operations | ✅ Complete | Rename, delete, copy, validate multiple files at once |
| File Filtering | ✅ Complete | Filter by name, date, size; sort by multiple criteria |
| Session History | ✅ Complete | Track all sessions with timestamps and statistics |
| Statistics | ✅ Complete | Comprehensive analytics (sessions, signs, time, popular files) |
| Import/Export | ✅ Complete | ZIP-based backup and data transfer |
| Backup System | ✅ Complete | Scheduled backups with automatic rotation |

### User Interface

| Feature | Status | Description |
|---------|----------|-------------|
| File Selection GUI | ✅ Complete | Browse and select `.txth` files |
| Chat Commands | ✅ Partial | 5/8 commands implemented |
| ModMenu Config | ⚠️ Planned | Toggle mod, set options, view status |
| Keybinds | ❌ Planned | Quick navigation without commands |

---

## 📥 Installation

### Requirements

- **Minecraft:** 1.21.5
- **Java:** 21+
- **Fabric Loader:** 0.16.9+
- **Fabric API:** 0.106.0+1.21.5
- **ModMenu:** 11.0.1+ (optional, recommended)

### Download

Grab the latest release from [Releases](../../releases) or build from source.

```bash
# Download the JAR file
wget https://github.com/RZZW304/SignScribe/releases/download/v1.21.5/SignScribe-1.21.5.jar

# Or use the included JAR from build/
```

### Install

1. Download `SignScribe-1.21.5.jar`
2. Place the file in `.minecraft/mods/`
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

---

## 📖 Usage Guide

### Quick Start

1. **Prepare your .txth file** (see format below)
2. **Place it in** `config/signscribe/txth/`
3. **Load the file** using:
   ```
   /signscribe open
   ```
   Then select your file from the GUI
4. **Check status:**
   ```
   /signscribe status
   # Output: Session: story.txth (1/50 signs)
   ```
5. **Place signs!** Just right-click any sign block

### Basic Workflow

```
1. /signscribe open          → Open file selection GUI
2. [Click your .txth file]  → Load file, start session
3. Place a sign               → Auto-applies SIGN1 text
4. Place another sign         → Auto-applies SIGN2 text
5. ...                        → Continues through all signs
6. /signscribe status         → Check your progress
7. /signscribe stop          → End session when done
```

---

## 💬 Commands

All commands are client-side (start with `/signscribe`):

| Command | Description | Example |
|---------|-------------|----------|
| `/signscribe open` | Open file selection GUI | `/signscribe open` |
| `/signscribe next` | Advance to next sign | `/signscribe next` |
| `/signscribe prev` | Go to previous sign | `/signscribe prev` |
| `/signscribe status` | Show current session info | `/signscribe status` |
| `/signscribe stop` | End current session | `/signscribe stop` |
| `/signscribe on` | Enable mod (planned) | `/signscribe on` |
| `/signscribe off` | Disable mod (planned) | `/signscribe sign 5` |
| `/signscribe sign <n>` | Jump to specific sign (planned) | `/signscribe sign 10` |

### Status Output

```
/signscribe status

→ [SignScribe] Session: adventure_story.txth (5/100 signs)
```

---

## 📄 .txth File Format

### Structure

The `.txth` file format is strict and pre-formatted:

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

- ✅ Exactly **4 lines** per sign block
- ✅ Exactly **14 characters** per line
- ✅ Signs separated by `SIGN1:`, `SIGN2:`, etc.
- ✅ Use `{BLANk}` for intentional blank lines
- ❌ Empty lines without `{BLANk}` will cause errors
- ❌ Lines longer than 14 characters will cause errors

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

---

## 🐍 Python Formatter App

> **Status:** 🚧 In Development (Coming Soon!)

The Python Formatter App makes creating `.txth` files effortless. Convert any plain text file into the strict `.txth` format automatically.

### Features (Planned)

| Feature | Description |
|---------|-------------|
| 📝 **Text Input** | Paste or load any `.txt` file |
| 🔄 **Auto-Wrap** | Automatically wraps text to 14 characters/line |
| ✂️ **Smart Word Break** | Breaks words at 14 chars if needed |
| 📏 **Blank Lines** | Preserves intentional blank lines as `{BLANk}` |
| 📦 **Format Conversion** | Converts to `.txth` with SIGN1:, SIGN2: blocks |
| 👁 **Preview** | See formatted output before saving |
| 💾 **Export** | Save as `.txth` file ready for SignScribe |
| 📊 **Stats** | Shows line count, sign count, character count |

### Usage Example

```python
# Coming soon!
python formatter_app.py

# GUI will open where you can:
# 1. Load or paste your text
# 2. Click "Convert to .txth"
# 3. Preview the formatted output
# 4. Save as your_story.txth
# 5. Use in SignScribe mod!
```

### Algorithm

The formatter will:
1. **Read** input text (UTF-8)
2. **Split** by newlines (preserving intentional breaks)
3. **Wrap** each line to 14 characters max
4. **Convert** long words: `Supercalifragilistic` → `Supercalifrag` + `ilistic`
5. **Mark** blank lines with `{BLANk}`
6. **Group** into 4-line blocks (56 characters per sign)
7. **Add** `SIGN1:`, `SIGN2:`, etc. prefixes
8. **Validate** output (exactly 14 chars per line)
9. **Save** as `.txth`

### Example Transformation

**Input (story.txt):**
```
Once upon a time, in a magical forest, there lived a brave knight.
He protected the villagers and fought the evil dragon.

The end.
```

**Output (story.txth):**
```txth
SIGN1:
Once upon a time
in a magical
forest, there lived
a brave knight.

SIGN2:
He protected the
villagers and foug
ht the evil dragon
{BLANk}

SIGN3:
{BLANk}
{BLANk}
The end.
{BLANk}
{BLANk}
```

---

## 🎨 Templates & Advanced Features

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

Save entire `.txth` file configurations:

```bash
# Export current session
/signscribe export session adventure

# Creates: adventure_story.zip
# Contains: adventure.txth + metadata
```

### Batch Operations

```bash
# Validate multiple files
/signscribe batch validate story1 story2 story3

# Apply template to multiple files
/signscribe batch apply template "Shop" *.txth

# Rename multiple files
/signscribe batch rename old1 new1 old2 new2
```

### Statistics

```bash
/signscribe stats

→ General: Files: 12 | Size: 245 KB | Templates: 5
→ Usage: Sessions: 45 | Signs: 234 | Time: 2h 34m
→ Popular Files: adventure.txth (12 sessions), shop.txth (8)
```

### Backup & Restore

```bash
# Create backup
/signscribe backup create backup.zip

# Scheduled backup (keeps last 7 days)
/signscribe backup schedule /backups 7

# Restore from backup
/signscribe backup restore backup.zip
```

---

## 🖼️ Screenshots

> **Note:** Screenshots coming soon!

- [ ] File selection GUI
- [ ] Sign placement in action
- [ ] Progress notifications
- [ ] ModMenu configuration
- [ ] Python formatter app
- [ ] Template management

---

## 🔧 Configuration

### Config Options

Edit `config/signscribe/signscribe.properties`:

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

---

## 🐛 Troubleshooting

### Common Issues

**Q: Signs show wrong text?**
A: Ensure your `.txth` file has exactly 14 characters per line. Check for extra spaces.

**Q: Session not saving?**
A: Check `config/signscribe/data.dat` exists and is writable.

**Q: Mod not working?**
A: Ensure Fabric API is installed and you're using Minecraft 1.21.5.

**Q: "There are only X signs" error?**
A: You tried to jump past the last sign in your file.

### Getting Help

- 📖 [Documentation](../../wiki)
- 🐛 [Report Issues](../../issues)
- 💬 [Discord](https://discord.gg/yourdiscord)

---

## 🏗️ Building from Source

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

---

## 📝 Development Roadmap

### Alpha 5.7 (Current) ✅
- [x] Sign placement logic
- [x] Advanced data management
- [x] Templates & batch operations
- [x] Statistics & backups

### Alpha 5 (Next)
- [ ] ModMenu configuration screen
- [ ] Keybinds for navigation
- [ ] Enhanced GUI features
- [ ] Undo/redo functionality

### Beta 1
- [ ] Python Formatter App
- [ ] Full documentation
- [ ] Stability improvements
- [ ] Performance optimizations

### 1.0.0 Release
- [ ] All features polished
- [ ] Extensive testing
- [ ] Video tutorials
- [ ] Community templates

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

**Note:** This is a private use project. All rights reserved.

---

## 📄 License

**ALL RIGHTS RESERVED - Private use only**

This mod is for personal use only. Distribution, modification, or commercial use is prohibited without explicit permission.

---

## 🙏 Credits & Acknowledgments

- **Development:** [RZZW304](https://github.com/RZZW304)
- **Inspiration:** SignStory mod (Forge)
- **Special Thanks:** Fabric team, Yarn mappings community

---

## 📊 Project Statistics

- **Lines of Code:** ~5,000+
- **Classes:** 20+
- **Tests:** 21 passing
- **Phases Complete:** 5/5 (85%)
- **Development Time:** Ongoing since Jan 2025

---

<div align="center">

**Made with ❤️ for Minecraft storytellers**

[⬆ Back to Top](#signscribe-)

</div>
