package org.rifidi.edge.core.api.readerplugin.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class CommandDescription {

	private String name;
	private String classname;
	private String xsd;
	private List<String> groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getClassname() {
		return classname.trim();
	}

	public void setClassname(String classname) {
		this.classname = classname.trim();
	}

	public String getXsd() {
		if(xsd!=null){
			return xsd.trim();
		}else return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd.trim();
	}

	@XmlElement(name = "group")
	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

}