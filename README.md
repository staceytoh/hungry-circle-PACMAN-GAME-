# 🟡 hungry-circle

A procedurally generated, Pacman-inspired maze game built in Java — featuring line-of-sight rendering, collectible coins, and an immersive tile-based world. Every playthrough spawns a brand new dungeon filled with rooms, hallways, and hidden treasure.

> Built from scratch using a custom rendering engine and no external libraries (except StdDraw).

---

### ✨ Features

- 🧠 Procedural World Generation  
  Every game starts with a fresh world layout based on a random or user-supplied seed — no two maps are the same.

- 🪙 Collectible Coins  
  Explore and gather coins placed randomly in the world. Great for future scoring or upgrade mechanics!

- 👀 Line-of-Sight Rendering  
  Players can only see tiles within their visibility radius, making exploration feel mysterious and strategic.

- 💾 Save & Load Support  
  Quit and resume your progress seamlessly with keyboard shortcuts.

- 🧍 Smooth WASD Movement  
  Navigate through rooms, hallways, and fog-of-war environments using simple keyboard controls.

- 🎨 Custom Tile Engine  
  Built with Java using a handcrafted tile-rendering system (TERenderer, TETile, and Tileset).

---

### 🛠️ Tech Stack

- Java (with StdDraw for graphics)
- Custom tile-based engine
- Random world generation

---

### 📦 Project Structure

src/  
├── core/          # Main game logic and menu system  
│   ├── Main.java  
│   ├── MainMenu.java  
│   ├── World.java  
│   └── Avatar.java  
├── tileengine/    # Tile engine for rendering  
│   ├── TERenderer.java  
│   ├── TETile.java  
│   └── Tileset.java  
├── utils/         # Random number helpers, file utilities  
│   ├── RandomUtils.java  
│   ├── FileUtils.java  
│   └── StdDraw.java   <-- Drop this file here

---

### ▶️ How to Run

1. Clone the Repo

git clone https://github.com/staceytoh/hungry-circle.git  
cd hungry-circle

2. Download StdDraw.java

Download StdDraw.java from: https://introcs.cs.princeton.edu/java/stdlib/StdDraw.java  
Save it as:  
src/utils/StdDraw.java

3. Compile Everything

javac -cp . $(find src -name "*.java")

4. Run the Game

java -cp src core.Main

---

### 🎮 Controls

| Key        | Action                         |  
|------------|--------------------------------|  
| N + seed   | Start a new game with a seed   |  
| L          | Load previous game             |  
| Q          | Quit and auto-save             |  
| W A S D    | Move your player around        |

---

### 📌 Future Plans

- Add score tracking and HUD display  
- Implement basic enemy AI behavior  
- Add music and sound effects  
- Enhance level design with difficulty scaling  
- Add pause menu and smoother animations

---

### 🙌 Contributions

Open to feedback, forks, and fun features! Feel free to open issues or submit PRs 🚀

