package org.jshack.java.soundengine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * stores all sounds and controls them
 * 
 * @author Niklas Keller
 * @version v1.0
 * @since v1.0
 */
public class SoundManager {
	private static HashMap<String, byte[]> soundData;
	private static HashMap<String, Sound> soundThreads;
	
	// this class doesn't have a constructor, 
	// so this is kind of a stand-in
	static {
		soundData = new HashMap<String, byte[]>();
		soundThreads = new HashMap<String, Sound>();
	}
	
	/**
	 * private! don't use!
	 */
	private SoundManager() {
		
	}
	
	/**
	 * loads a file from the filesystem
	 * 
	 * @param key key to access the sound
	 * @param filename filename passed to {@link java.io.FileInputStream}
	 */
	public static void load(String key, String filename) {
		byte[] bytes = null;
		
		try {
			InputStream is = new FileInputStream(filename);
			
			bytes = new byte[is.available()];
			
			int off = 0;
			int n;
			
			while(off < bytes.length && (n = is.read(bytes, off, bytes.length-off)) >= 0) {
				off += n;
			}
			
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		soundData.put(key, bytes);
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
		if(soundThreads.containsKey(key)) {
			soundThreads.get(key).stopSound();
			soundThreads.put(key, null);
		}
		
		if(soundData.containsKey(key)) {
			Sound sound = new Sound(soundData.get(key), loop);
			sound.start();
			
			soundThreads.put(key, sound);
			
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
			soundThreads.get(key).pause(pause);
		}
	}
	
	/**
	 * pause or unpause all sounds
	 * 
	 * @param pause if sounds should be paused, true, if sounds should be unpaused false
	 */
	public static void pauseAll(boolean pause) {
		for(String key : soundThreads.keySet()) {
			soundThreads.get(key).pause(pause);
		}
	}
	
	/**
	 * stop sound if it's playing
	 * 
	 * @param key key provided during {@link #load(String, String)}
	 */
	public static void stop(String key) {
		if(soundThreads.containsKey(key)) {
			soundThreads.get(key).stopSound();
		}
	}
	
	/**
	 * stop all sounds that're playing
	 */
	public static void stopAll() {
		for(String key : soundThreads.keySet()) {
			soundThreads.get(key).stopSound();
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
			soundThreads.get(key).setVolume(volume);
		}
	}
}