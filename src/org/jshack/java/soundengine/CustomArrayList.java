package org.jshack.java.soundengine;

import java.util.ArrayList;

/**
 * @author Niklas Keller
 * @version v1.1
 * @since v1.1
 */
public class CustomArrayList<T> extends ArrayList<T> {
	private static final long serialVersionUID = -5689527770087621110L;
	
	@SafeVarargs
	public CustomArrayList(T... items) {
		for(T item : items) {
			add(item);
		}
	}
}
