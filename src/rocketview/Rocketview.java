/* Copyright 2005 David Cassard, Ian Osgood, Karl Hallowell, Jamey Sharp,
 *                Travis Spencer, Peter Welte, Tim Welch
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
package rocketview;

import cansocket.*;
import launchcontrol.*;
import widgets.*;

import java.awt.*;
import java.util.*;
import java.net.*;
import java.text.DateFormat;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Main window for PSAS telemetry viewer.
 */
public class Rocketview extends JFrame
{
	protected static final boolean showStripCharts = true;

	public static void main(String[] args) throws Exception
	{
		CanDispatch dispatch = new CanDispatch();
		Rocketview f = new Rocketview(dispatch, args.length > 0);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		dispatch.run();
	} // end main()

	// construct a Rocketview
	// set up all panels, layout managers, and titles
	public Rocketview(CanDispatch dispatch, boolean showLaunchControl) throws Exception
	{
		super("Rocketview");

		StateGrid.setDispatcher(dispatch);
		StateGrid grid = StateGrid.getStateGrid();
		grid.setColumns(1);
		
		// message box for scrolled text
		TextObserver messScroll = new TextObserver(dispatch);
		messScroll.setBorder( new TitledBorder( "CanId  len  data" ));

		JComponent[][] columns = {
			{ new OtherObserver(dispatch), grid, },
			{ new GPSObserver(dispatch), new IMUStateObserver(dispatch), },
			{ new APSObserver(dispatch), new RecObserver(dispatch), new ATVObserver(dispatch), },
		};

		// rvPane is the outermost content pane
		Container rvPane = getContentPane();
		rvPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = gbc.BOTH;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		rvPane.add(messScroll, gbc);

		gbc.weightx = 0.0;
		for(int col = 0; col < columns.length; ++col)
		{
			JPanel subsys = new JPanel();
			subsys.setLayout(new GridBoxLayout());
			for(int row = 0; row < columns[col].length; ++row)
			{
				if(row != 0)
				{
					JPanel c = new JPanel();
					c.setLayout(new GridLayout(1, 1));
					c.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
					c.add(new JSeparator());
					subsys.add(c);
				}
				columns[col][row].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				subsys.add(columns[col][row]);
			}
			rvPane.add(subsys, gbc);
		}
		
		gbc.gridy = 1;
		gbc.weighty = 0.0;
		gbc.gridwidth = columns.length + 1;
		if(showLaunchControl)
			rvPane.add(new LaunchControl(dispatch), gbc);
		else
			dispatch.setSocket(new LogCanSocket(new UDPCanSocket(), "RocketView.log"));

		if(showStripCharts)
		{
			gbc.gridx = columns.length + 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = gbc.REMAINDER;
			gbc.gridheight = gbc.REMAINDER;
			rvPane.add(new IMUObserver(dispatch), gbc);
		}
	}

	public void outputSizes(Component c, String name) {
		System.out.println(name + ":");
		System.out.println("\tMAX: " + c.getMaximumSize());
		System.out.println("\tMIN: " + c.getMinimumSize());
		System.out.println("\tPREF: " + c.getPreferredSize());
	}
} // end class Rocketview
