package org.ukiuni.pacifista;

/**
 * Template can create config file. use like below.
 * 
 * var template = Template.create("testTemplate.vm"); template.put("key1",
 * "value1"); template.put("key2", "value2"); var config =
 * template.templateToValue(); remote.send(config, "sendDir", "configFileName");
 * 
 * @author tito
 * 
 */
public interface Template {
	/**
	 * Create new Template
	 * 
	 * @param templatePath
	 * @return
	 */
	public Template create(String templatePath);

	/**
	 * Create new Template with templates folder file
	 * 
	 * @param templatePath
	 * @return
	 */
	public Template createWithFile(String templatePath);

	/**
	 * Put argument for attach to template
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);

	/**
	 * Generate value
	 * 
	 * @return
	 */
	public String toValue();
}
