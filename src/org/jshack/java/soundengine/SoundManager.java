package org.jshack.java.soundengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * stores all sounds and controls them
 * 
 * @author Niklas Keller
 * @version v1.1
 * @since v1.0
 */
public class SoundManager {
	private static HashMap<String, byte[]> soundData;
	private static HashMap<String, CustomArrayList<Sound>> soundThreads;
	
	// this class doesn't have a constructor, 
	// so this is kind of a stand-in
	static {
		soundData = new HashMap<>();
		soundThreads = new HashMap<>();
	}
	
	// private to deactivate
	private SoundManager() {
		
	}
	
	/**
	 * loads a file from the filesystem
	 * 
	 * @param key key to access the sound
	 * @param filename filename passed to {@link java.io.FileInputStream}
	 */
	public static boolean load(String key, String filename) {
		try {
			return loadFromStream(key, new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * loads a file from inner project
	 * 
	 * @param key key to access the sound
	 * @param filename filename passed to {@link java.io.FileInputStream}
	 */
	public static boolean loadFromProject(String key, String filename) {
		return loadFromStream(key, SoundManager.class.getResourceAsStream("/" + filename));
	}
	
	/**
	 * loads a file
	 * 
	 * @param key key to access the sound
	 * @param is used {@link java.io.InputStream}
	 */
	public static boolean loadFromStream(String key, InputStream is) {
		byte[] bytes = null;
		
		if(is == null)
			return false;
		
		try {
			bytes = new byte[is.available()];
			
			int off = 0;
			int n;
			
			while(off < bytes.length && (n = is.read(bytes, off, bytes.length-off)) >= 0) {
				off += n;
			}
			
			is.close();
			
			soundData.put(key, bytes);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * starts to play a sound
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 * @return true if sound is loaded, otherwise false
	 */
	public static boolean play(String key) {
		return play(key, false);
	}
	
	/**
	 * starts to play a sound
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 * @param loop true if sound should be looped, otherwise false
	 * @return true if sound is loaded, otherwise false
	 */
	public static boolean play(String key, boolean loop) {
		if(soundData.containsKey(key)) {
			Sound sound = new Sound(soundData.get(key).clone(), loop);
			sound.start();
			
			if(soundThreads.containsKey(key)) {
				soundThreads.get(key).add(sound);
			} else {
				soundThreads.put(key, new CustomArrayList<>(sound));
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * pause or unpause a sound
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 * @param pause if sound should be paused, true, if sound should be unpaused false
	 */
	public static void pause(String key, boolean pause) {
		if(soundThreads.containsKey(key)) {
			for(Sound sound : soundThreads.get(key)) {
				sound.pause(pause);
			}
		}
	}
	
	/**
	 * pause or unpause all sounds
	 * 
	 * @param pause if sounds should be paused, true, if sounds should be unpaused false
	 */
	public static void pauseAll(boolean pause) {
		for(String key : soundThreads.keySet()) {
			for(Sound sound : soundThreads.get(key)) {
				sound.pause(pause);
			}
		}
	}
	
	/**
	 * stop sound if it's playing
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 */
	public static void stop(String key) {
		if(soundThreads.containsKey(key)) {
			for(Sound sound : soundThreads.get(key)) {
				sound.stopSound();
			}
		}
	}
	
	/**
	 * stop all sounds that're playing
	 */
	public static void stopAll() {
		for(String key : soundThreads.keySet()) {
			for(Sound sound : soundThreads.get(key)) {
				sound.stopSound();
			}
		}
	}
	
	/**
	 * set volume of a already playing sound
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 * @param volume volume level, between 1f (max) and .0f (min)
	 */
	public static void setVolume(String key, float volume) {
		if(soundThreads.containsKey(key)) {
			for(Sound sound : soundThreads.get(key)) {
				sound.setVolume(volume);
			}
		}
	}
}