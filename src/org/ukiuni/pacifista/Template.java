package org.ukiuni.pacifista;

public interface Template {
	public void put(String key, Object value);

	public String templateToValue(String templatePath);
}
