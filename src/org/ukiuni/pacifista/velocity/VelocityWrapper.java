package org.ukiuni.pacifista.velocity;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.ukiuni.pacifista.Template;

public class VelocityWrapper implements Template {

	private VelocityContext velocityContext;
	private org.apache.velocity.Template template;

	public VelocityWrapper(File templateDir) {
		Velocity.setProperty("file.resource.loader.path", templateDir.getAbsolutePath());
		Velocity.init();
	}

	private VelocityWrapper() {
		this.velocityContext = new VelocityContext();
	}

	@Override
	public Template createWithFile(String templatePath) {
		VelocityWrapper velocityWrapper = new VelocityWrapper();
		velocityWrapper.template = Velocity.getTemplate(templatePath, "UTF-8");
		return velocityWrapper;
	}

	@Override
	public Template create(String templateString) {
		VelocityWrapper velocityWrapper = new VelocityWrapper();
		velocityWrapper.template = newTemplate(templateString);
		return velocityWrapper;
	}

	private org.apache.velocity.Template newTemplate(String templateString) {
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(templateString);
		SimpleNode node;
		try {
			node = runtimeServices.parse(reader, "Template" + new Random().nextInt());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		org.apache.velocity.Template template = new org.apache.velocity.Template();
		template.setRuntimeServices(runtimeServices);
		template.setData(node);
		template.initDocument();

		return template;
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
