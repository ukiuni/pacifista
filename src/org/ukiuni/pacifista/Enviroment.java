package org.ukiuni.pacifista;


public class Enviroment {
	public String get(String key) {
		return System.getenv(key);
	}
}
