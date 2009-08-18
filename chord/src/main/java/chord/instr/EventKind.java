/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.instr;

/**
 * The kind of an event.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class EventKind {
	public static final byte ENTER_METHOD = 0;
	public static final byte LEAVE_METHOD = 1;
	public static final byte BEF_NEW = 2;
	public static final byte AFT_NEW = 3;
	public static final byte NEW = 4;
	public static final byte NEW_ARRAY = 5;
	public static final byte GETSTATIC_PRIMITIVE = 6;
	public static final byte GETSTATIC_REFERENCE = 7;
	public static final byte PUTSTATIC_PRIMITIVE = 8;
	public static final byte PUTSTATIC_REFERENCE = 9;
	public static final byte GETFIELD_PRIMITIVE = 10;
	public static final byte GETFIELD_REFERENCE = 11;
	public static final byte PUTFIELD_PRIMITIVE = 12;
	public static final byte PUTFIELD_REFERENCE = 13;
	public static final byte ALOAD_PRIMITIVE = 14;
	public static final byte ALOAD_REFERENCE = 15;
	public static final byte ASTORE_PRIMITIVE = 16; 
	public static final byte ASTORE_REFERENCE = 17; 
	public static final byte THREAD_START = 18;
	public static final byte THREAD_JOIN = 19;
	public static final byte ACQUIRE_LOCK = 20;
	public static final byte RELEASE_LOCK = 21;
	public static final byte WAIT = 22;
	public static final byte NOTIFY = 23;
}
