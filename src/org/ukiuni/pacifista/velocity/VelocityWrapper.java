package org.ukiuni.pacifista.velocity;

import java.io.File;
import java.io.StringWriter;

import javax.naming.Context;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.ukiuni.pacifista.Template;

public class VelocityWrapper implements Template {

	private VelocityContext velocityContext;
	private org.apache.velocity.Template template;

	public VelocityWrapper(File templateDir) {
		Velocity.setProperty("file.resource.loader.path", templateDir.getAbsolutePath());
		Velocity.init();
	}
	private VelocityWrapper(){
		this.velocityContext = new VelocityContext();
	}

	public Template create(String templatePath) {
		VelocityWrapper velocityWrapper = new VelocityWrapper();
		velocityWrapper.template = Velocity.getTemplate(templatePath, "UTF-8");
		return velocityWrapper;
	}

	@Override
	public void put(String key, Object value) {
		this.velocityContext.put(key, value);
	}

	@Override
	public String toValue() {
		StringWriter writer = new StringWriter();
		this.template.merge(this.velocityContext, writer);
		return writer.toString();
	}
}
