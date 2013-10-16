package org.jshack.java.soundengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class Sound extends Thread {
	public static final int STATUS_UNKNOWN = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSED = 2;
	public static final int STATUS_ENDED = 3;

	private int status;
	private byte[] data;
	private boolean loop;

	private float volume;

	private AudioInputStream ais;
	private SourceDataLine line;

	public Sound(byte[] data, boolean loop) {
		this.setDaemon(true);
		this.init(data, loop);
		this.volume = 1f;
	}

	private void init(byte[] data, boolean loop) {
		this.data = data;
		this.loop = loop;

		if (this.ais != null) {
			try {
				this.ais.close();
				this.ais = null;
			} catch (IOException e) {
			}
		}

		InputStream is = new ByteArrayInputStream(this.data.clone());

		try {
			this.ais = AudioSystem.getAudioInputStream(is);
		} catch (Exception e) {

		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void start() {
		if(startLine()) {
			super.start();
		}
	}

	public void run() {
		playSound();
		
		this.status = STATUS_UNKNOWN;
	}

	private boolean startLine() {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				ais.getFormat());

		try {
			this.line = (SourceDataLine) AudioSystem.getLine(info);
			this.line.open(ais.getFormat());
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		line.start();

		return true;
	}

	private synchronized void playSound() {
		int num = 0;
		byte[] buffer = new byte[1024];

		try {
			while (num != -1) {
				if (this.status == STATUS_PAUSED) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						this.status = STATUS_ENDED;
					}
				}

				if (this.status == STATUS_ENDED) {
					line.start();
					line.flush();
					break;
				}

				num = this.ais.read(buffer, 0, buffer.length);

				if (num >= 0) {
					line.write(buffer, 0, num);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ais.close();
				ais = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (loop && this.status != Sound.STATUS_ENDED) {
				this.init(data, loop);
				this.playSound();
			} else {
				line.drain();
				line.close();
			}
		}
	}

	public void setVolume(float toVolume) {
		if(line == null)
			return;
		
		this.volume = toVolume;
		
		FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(Math.max((float) (Math.log(volume)	/ Math.log(10.0) * 20.0), -80.0f));
	}

	public void pause(boolean pause) {
		this.status = pause ? Sound.STATUS_PAUSED : Sound.STATUS_PLAYING;

		synchronized (this) {
			if (!pause)
				this.notify();
		}
	}

	public void stopSound() {
		this.status = Sound.STATUS_ENDED;
	}

	public int getStatus() {
		return status;
	}
}