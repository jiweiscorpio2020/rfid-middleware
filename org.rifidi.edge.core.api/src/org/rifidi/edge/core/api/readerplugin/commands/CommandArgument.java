/*
 *  CommandArgument.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.api.readerplugin.commands;

import java.io.Serializable;

/**
 * This is a value object used to store an argument to a command.
 * 
 * If the error flag is set, then the argValue for the Argument should be the
 * cause of the error
 * 
 * @see CommandConfiguration
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandArgument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the argument
	 */
	private String _argName;

	/**
	 * The value for the argument
	 */
	private String _argValue;

	/**
	 * Whether or not there is an error associated with this argument
	 */
	private boolean _error;

	public CommandArgument(String argName, String argValue, boolean error) {
		if(argName==null){
			throw new NullPointerException("argName cannot be null");
		}
		if(argValue==null){
			throw new NullPointerException("argValue cannot be null");
		}
		_argName = argName;
		_argValue = argValue;
		_error = error;
	}

	/**
	 * 
	 * @return The name of the argument
	 */
	public String getName() {
		return _argName;
	}

	/**
	 * If the error flag is set, this string will be the reason for the error
	 * 
	 * @return The value of the argument
	 */
	public String getValue() {
		return _argValue;
	}

	/**
	 * 
	 * @return True if there is an error associated with this argument
	 */
	public boolean hasError() {
		return _error;
	}
}
