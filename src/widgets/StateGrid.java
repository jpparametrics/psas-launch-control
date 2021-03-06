/* Copyright 2005 Jamey Sharp, Josh Triplett, Keith Packard, Sarah Bailey,
 *                Peter Welte, Tim Welch
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * Portland State Aerospace Society (PSAS) is a student branch chapter of the
 * Institute of Electrical and Electronics Engineers Aerospace and Electronics
 * Systems Society. You can reach PSAS at info@psas.pdx.edu.  See also
 * http://psas.pdx.edu/
 */
package widgets;

import cansocket.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/** A grid of leds displaying info about different elements, 
 * which are nodes in our case, and each of which is labeled. */
public class StateGrid extends JPanel
{
	protected static final String[] nodes = {
		"APS",
		"IMU",
		"GPS",
		"ATV",
		"REC",
	};

	protected static final String[] flags = {
		"APS_SWITCH_2", /* CAN bus */
		"APS_SWITCH_3", /* ATV power amp (and DC/DC converter) */
		"APS_SWITCH_4", /* WIFI power amp */
		"UMB_ROCKETREADY",
		"UMB_CONNECTOR",

		"GPS_POWER",

		"ATV_POWER_CAMERA",
		"ATV_POWER_OVERLAY",
		"ATV_POWER_TX",
		"ATV_POWER_PA",
	};

	protected static final String[] enables = {
		"APS_DATA_VOLTS",
		"APS_DATA_AMPS",
		"APS_DATA_CHARGE",

		"IMU_ACCEL_DATA",
		"IMU_GYRO_DATA",
		"PRESS_REPORT_DATA",
		"TEMP_REPORT_DATA",

		"GPS_UART_TRANSMIT",
	};

	protected static final String[] tests = {
		/* Shared tests */
		"PRESSURE_VALID",
		"GPS_LOCKED",
		"SAFE_DESCENT_GPS",
		"DROGUE_DEPLOY_SAFE_GPS",
		"SAFE_DESCENT_PRESSURE",
		"DROGUE_DEPLOY_SAFE_PRESSURE",
		"HEIGHT_MATCH_GPS_PRESSURE",
	
		/* Preflight check tests. */
		"SANE_ANTENNAS",
		"SANE_IMU_ACCEL",
		"QUIET_PRESSURE_BASE",
		"GOT_GPS",
		"SANE_GPS",
	
		/* Arming tests. */
		"ARMING_IMU_FAST",
	
		/* Armed tests. */
		"LOG_AVAIL",

		/* Coast tests */
		"COAST_IMU",

		/* Boost tests */
		"BOOST_GPS",
		"BOOST_IMU",
		"BOOST_UMB",
		"BOOST_PRESSURE",
	
		/* Coast tests */
		"APOGEE_PRESSURE",
	
		/* Deploy drogue tests */
		"DROGUE_PRESSURE",
		"DROGUE_GPS",
		"DROGUE_IMU",
		"DROGUE_WORKING",
	
		/* Descent drogue tests */
		"DESCEND_GPS",
		"DESCEND_PRESSURE",
		"DESCEND_MAIN_FUTURE",
	
		/* Descend main tests */
		"TOUCHDOWN_GPS",
		"TOUCHDOWN_PRESSURE",
	
		/* Recovery wait tests */
		"RECOVERY_VOLTS",
	};

	protected final Map labels = new HashMap();

	public StateGrid(CanDispatch dispatch)
	{
		try {
			int bit = 0;
			for(int i = 0; i < nodes.length; ++i, ++bit)
				add(dispatch, nodes[i], new NodeModeLabel(nodes[i], bit));
			for(int i = 0; i < flags.length; ++i, ++bit)
				add(dispatch, flags[i], new NodeFlagLabel(flags[i], bit));
			for(int i = 0; i < enables.length; ++i, ++bit)
				add(dispatch, enables[i], new NodeEnableLabel(enables[i], bit));
			for(int i = 0; i < tests.length; ++i, ++bit)
				add(dispatch, tests[i], new NodeStateLabel(tests[i], bit));
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch(NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		setColumns(4);
	}

	protected void add(CanDispatch dispatch, String name, NodeStateLabel c)
	{
		labels.put(name, c);
		dispatch.add(c);
		add(c);
	}

	public void setColumns(int columns)
	{
		setLayout(new GridLayout(0, columns));
	}

	protected static StateGrid grid;

	public static void setDispatcher(CanDispatch dispatch)
	{
		grid = new StateGrid(dispatch);
	}

	public static StateGrid getStateGrid()
	{
		return grid;
	}

	public static NameDetailLabel getLabel(String name)
	{
		return (NameDetailLabel) getStateGrid().labels.get(name);
	}
}
