SoundEngine
===========
This is a small and simple SoundEngine written in pure Java.

## How to use

```java
public void load() {
  SoundManager.load("player.jump", "sounds/player/jump.wav");
}

public void ...() {
  if(...) {
    SoundManager.play("player.jump");
  }
}
```
