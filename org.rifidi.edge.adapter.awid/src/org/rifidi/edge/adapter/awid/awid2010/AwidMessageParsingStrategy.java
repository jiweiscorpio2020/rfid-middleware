/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.awid.awid2010;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;

/**
 * The message parsing strategy for an Awid. The purpose of this class is to
 * decide when a stream of bytes forms a logical Awid message. It works like
 * this: the isMessage method is called each time a byte is read from the
 * socket. If the collected bytes forms a complete message, it should reset
 * itself and return them. If they do not, it should return null.
 * 
 * It follows this algorithm: Read the first byte. If it is a x00 or xFF, the
 * the incoming message is an acknowledgement for a previously sent command. If
 * not, read the second byte. If both the first byte and the second byte are a
 * 'i', then the message is a welcome message, and we continue reading until we
 * see the string "MODULE", which is the end of the message. Otherwise, the
 * first byte is the number of bytes in the message, and we need to continue
 * reading until we see that number of bytes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidMessageParsingStrategy implements MessageParsingStrategy {

	/** All the bytes seen so far. */
	private byte[] bytes;
	private ArrayList<Byte> debugBytes = new ArrayList<Byte>();
	/** The first byte which normally represents the lengthByte */
	private byte lengthByte = -1;
	/** The message byte converted to an int */
	private int messageLength = -1;
	/** The second byte which normally represents the typeByte */
	private byte typeByte = -1;
	/** bytes seen so far */
	private int count = 0;
	/** A boolean that is set to true if the first two bytes are 'i' */
	private boolean isWelcomeMessage = false;
	/**
	 * A string builder to append characters to if we are reading in a welcome
	 * message
	 */
	private StringBuilder welcomeMessage;

	private static final String AWID_2010_WELCOME_STRING_ENDING = "MODULE";

	private static final String AWID_3014_WELCOME_STRING_ENDING = ("MODULE" + new String(
			new byte[] { (byte) 0x00 }));

	private static final String AWID_3014_IDENTIFIER = "1518";

	private boolean is3014 = false;

	/** The logger */
	private static final Log logger = LogFactory
			.getLog(AwidMessageParsingStrategy.class);

	private static final Log awidlogger = LogFactory.getLog("awid");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.threads.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		// first increment the counter
		count++;
		debugBytes.add(message);
		awidlogger.debug(getMessage(debugBytes));
		// If this is the first byte of the message
		if (count == 1) {
			// if the message is a one-byte ack message
			if (message == (byte) 0x00 || message == (byte) 0xFF) {
				return new byte[] { message };
			}
			// otherwise, save the length bit
			lengthByte = message;
			messageLength = unsignedByteToInt(lengthByte);
			if (messageLength <= 1) {
				logger.error("Invalid length: " + messageLength);
				awidlogger.error("error!");
				throw new IllegalStateException("Invalid length: "
						+ messageLength);
			}
		}
		// if this is the second byte of the message
		else if (count == 2) {
			typeByte = message;
			// if the message is a welcome message
			if (lengthByte == (byte) 'i' && typeByte == (byte) 'i') {
				isWelcomeMessage = true;
				welcomeMessage = new StringBuilder();
				welcomeMessage.append((char) lengthByte);
				welcomeMessage.append((char) typeByte);
			}

			// otherwise, its a normal message
			else {
				bytes = new byte[messageLength];
				bytes[0] = lengthByte;
				bytes[1] = typeByte;
			}
		}
		// Do this block for every byte after the first two
		else {
			if (!isWelcomeMessage) {
				// add the byte to the array
				bytes[count - 1] = message;
				// check to see if we have received everything.
				if (count == messageLength) {
					byte[] retVal = bytes;
					return retVal;
				}
			}
			// if the message is a welcome message
			else {
				if (!is3014
						&& welcomeMessage.toString().contains(
								AWID_3014_IDENTIFIER)) {
					is3014 = true;
				}
				// append every byte to the string builder
				welcomeMessage.append((char) message);
				String wm = welcomeMessage.toString();
				// TODO: Might be a bug here if someone initially connects to a
				// 3014, then tries to connect to a 2010 using the same module.
				// It would be stupid if they did do that, but it would lead to
				// some problems.
				if (!is3014) {
					if (wm.endsWith(AWID_2010_WELCOME_STRING_ENDING)) {
						return wm.getBytes();
					}
				} else {
					if (wm.endsWith(AWID_3014_WELCOME_STRING_ENDING)) {
						return wm.getBytes();
					}
				}
			}
		}
		// if we have not seen a whole message yet, return null
		return null;
	}

	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
	
	private String getMessage(List<Byte> message){
		StringBuilder sb = new StringBuilder();
		for(byte b : message){
			String s = new String(Hex.encodeHex(new byte[]{b}));
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private String getMessage(byte[] message){
		StringBuilder sb = new StringBuilder();
		for(byte b : message){
			String s = new String(Hex.encodeHex(new byte[]{b}));
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	
}
