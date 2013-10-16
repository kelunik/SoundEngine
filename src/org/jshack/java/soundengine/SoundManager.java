package org.jshack.java.soundengine;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Der SoundManager verwaltet die einzelnen Sounds.
 * 
 * @author Niklas Keller
 * @version September 2013
 *
 */

public class SoundManager {
	private static HashMap<String, byte[]> soundData;
	private static HashMap<String, Sound> soundThreads;
	
	/**
	 * Der Static-Block dient als eine Art Konstruktor-Ersatz
	 */
	static {
		soundData = new HashMap<String, byte[]>();
		soundThreads = new HashMap<String, Sound>();
	}
	
	/**
	 * Lädt eine Sounddatei aus dem Dateisystem.
	 * 
	 * @param key Schlüssel, unter dem der Sound intern gespeichert wird
	 * @param filename Dateiname, relativ zu /res/ innerhalb der .jar / des Projektverzeichnisses
	 */
	public static void load(String key, String filename) {
		byte[] bytes = null;
		
		try {
			InputStream is = SoundManager.class.getResourceAsStream("/"+filename);
			
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
	 * Startet das Abspielen eines Sounds.
	 * 
	 * @param key Schlüssel, unter dem der Sound intern gespeichert wird
	 * @return true, falls Sound existiert, andernfalls false
	 */
	public static boolean play(String key) {
		return play(key, false);
	}
	
	/**
	 * 
	 * @param key Schlüssel, unter dem der Sound intern gespeichert wird
	 * @param loop true, falls der Sound in einer Dauerschleife widergegeben werden soll, andernfalls false.
	 * @return true, falls Sound existiert, andernfalls false
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
	 * 
	 * @param key
	 * @param pause
	 */
	public static void pause(String key, boolean pause) {
		if(soundThreads.containsKey(key)) {
			soundThreads.get(key).pause(pause);
		}
	}
	
	/**
	 * 
	 * @param pause
	 */
	public static void pauseAll(boolean pause) {
		for(String key : soundThreads.keySet()) {
			soundThreads.get(key).pause(pause);
		}
	}
	
	/**
	 * 
	 * @param key
	 */
	public static void stop(String key) {
		if(soundThreads.containsKey(key)) {
			soundThreads.get(key).stopSound();
		}
	}
	
	/**
	 * 
	 */
	public static void stopAll() {
		for(String key : soundThreads.keySet()) {
			soundThreads.get(key).stopSound();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param volume
	 */
	public static void setVolume(String key, float volume) {
		if(soundThreads.containsKey(key)) {
			soundThreads.get(key).setVolume(volume);
		}
	}
}