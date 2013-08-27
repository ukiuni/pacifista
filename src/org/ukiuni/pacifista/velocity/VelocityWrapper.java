package org.ukiuni.pacifista.velocity;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.ukiuni.pacifista.Template;

public class VelocityWrapper implements Template {
	static {
		Velocity.init();
	}

	private VelocityContext velocityContext;

	public VelocityWrapper() {
		this.velocityContext = new VelocityContext();
	}

	public static Template create() {
		return new VelocityWrapper();
	}

	@Override
	public void put(String key, Object value) {
		this.velocityContext.put(key, value);
	}

	@Override
	public String templateToValue(String templatePath) {
		org.apache.velocity.Template template = Velocity.getTemplate(templatePath, "UTF-8");
		StringWriter writer = new StringWriter();
		template.merge(this.velocityContext, writer);
		return writer.toString();
	}
}
