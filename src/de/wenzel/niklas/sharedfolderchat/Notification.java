/*
 * Shared Folder Chat is a program for chatting via a shared folder in a local area network (LAN).
 * Copyright (C) 2013  Niklas Wenzel <nikwen.developer@gmail.com>
 * 
 * This file is part of Shared Folder Chat.
 *  
 * Shared Folder Chat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Shared Folder Chat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Shared Folder Chat.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.wenzel.niklas.sharedfolderchat;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Dimension;
import net.miginfocom.swing.MigLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTextArea;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

public class Notification extends JFrame {

	private static final long serialVersionUID = 7616034034840646061L;
	
	private JPanel contentPane;
	JTextArea lblText;
	private JLabel lblName;
	
	private Point firstClick;
	
	private static long closeTime;

	
	public Notification() {
		this.setSize(200, 120);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		setLocation(scrSize.width - getWidth(), scrSize.height - toolHeight.bottom - getHeight());
		setResizable(false);
		setBackground(Color.YELLOW);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.setBackground(Color.YELLOW);
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[175.00]", "[29.00][grow]"));
		
		lblName = new JLabel("");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPane.add(lblName, "cell 0 0");
		
		
		lblText = new JTextArea("");
		lblText.setLineWrap(true);
		lblText.setEditable(false);
		lblText.setWrapStyleWord(true);
		lblText.setBackground(Color.YELLOW);
		lblText.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblText, "cell 0 1,growx,aligny top");
		
		addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            firstClick = e.getPoint();
//	            getComponentAt(firstClick);
	        }
	    });

	    addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(MouseEvent e) {

	            int frameX = getLocation().x;
	            int frameY = getLocation().y;

	            int xMovement = (frameX + e.getX()) - (frameX + firstClick.x);
	            int yMovement = (frameY + e.getY()) - (frameY + firstClick.y);

	            int x = frameX + xMovement;
	            int y = frameY + yMovement;
	            setLocation(x, y);
	        }
	    });
	    
	    Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (isVisible() && closeTime < System.currentTimeMillis() && getMousePosition() == null) {
					setVisible(false);
				}
				
			}};
		timer.schedule(task, 0, 1000);
	}
		

	public void setNotificationText(String name, String message) {
		setVisible(true);
		lblText.setText(message);
		lblName.setText(name.concat(" wrote:"));
		closeTime = System.currentTimeMillis() + 4000;
	}

}
