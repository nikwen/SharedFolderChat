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

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import net.miginfocom.swing.MigLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import java.awt.Toolkit;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JSeparator;

public class ChatWindow {

	public static final String CHAT_DIR_NAME = ".chat";
	public static final String MSG_SUFFIX = ".msg";
	protected static final String USERNAME = "Username";
	private static final String SETTINGS_FILE_NAME = ".chat_properties";
	
	
	public ArrayList<File> chatDir = new ArrayList<File>();
	int savedLength = 0;
	protected int selectedChatIndex = 0;
	protected static boolean showAboutDialogAtStart;
	private boolean usernamePrompted = false;
	protected boolean notificationsOn = true;
	private String iconPath = "default";
	
	private Timer timer;
	UpdateTask task;
	
	
	private JTextField usernameText;
	private JButton btnNewChat;
	private JButton btnEnterAChat;
	private JButton btnLeaveTheChat;
	final JFileChooser fc = new JFileChooser();
	private JList chatsList;
	final DefaultListModel msgListModel = new DefaultListModel();
	final DefaultListModel chatsListModel = new DefaultListModel();
	private JFrame frmChat;
	private JTextField msgText;
	private JList msgList;
	private Notification notification = new Notification();
	private JButton btnAbout;
	private JPopupMenu popupMenu;
	private JMenuItem mntmSave;
	private JMenuItem mntmCopy;
	private JMenuItem mntmSaveAll;
	private JPopupMenu popupMenu_1;
	private JMenuItem menuItemToggleNotifications;
	private JMenuItem mntmRefresh;
	private JMenuItem mntmSetUpdatingTime;
	private JPopupMenu popupMenu_2;
	private JMenuItem mntmSetWindowTitle;
	private JMenuItem mntmSetWindowIcon;
	private JMenuItem mntmSetDefaultIcon;
	private JSeparator separator;
	private JMenuItem mntmSetDefaultTitle;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindow window = new ChatWindow();
					window.frmChat.setVisible(true);
					if (showAboutDialogAtStart) {
						AboutDialog ad = new AboutDialog(window.frmChat);
						ad.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ChatWindow() {
		initialize();
	}
	
	private void initialize() {
		frmChat = new JFrame();
		frmChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				JOptionPane.showMessageDialog(frmChat, "Nothing is selected!", "Error", JOptionPane.ERROR_MESSAGE);
				if (event.getKeyCode() == KeyEvent.VK_COPY) {
					msgList.getTransferHandler().exportToClipboard(msgList, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
				}
			}
		});
		frmChat.setIconImage(Toolkit.getDefaultToolkit().getImage(ChatWindow.class.getResource("/de/wenzel/niklas/sharedfolderchat/icon1.png")));
		frmChat.setTitle("Shared Folder Chat");
		frmChat.setBounds(100, 100, 530, 400);
		frmChat.setMinimumSize(new Dimension(465, 300));
		frmChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		popupMenu_2 = new JPopupMenu();
		addPopup(frmChat.getContentPane(), popupMenu_2);
		
		mntmSetWindowTitle = new JMenuItem("Set window title");
		mntmSetWindowTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = JOptionPane.showInputDialog(frmChat, "Please enter the new title.", frmChat.getTitle());
				if (title != null) {
					frmChat.setTitle(title);
					saveSettings();
				}
			}
		});
		popupMenu_2.add(mntmSetWindowTitle);
		
		mntmSetWindowIcon = new JMenuItem("Set window icon");
		mntmSetWindowIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setAcceptAllFileFilterUsed(true);
				fc.setDialogTitle("Select the new icon.");
				int returnVal = fc.showOpenDialog(frmChat);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						frmChat.setIconImage(ImageIO.read(fc.getSelectedFile()));
						iconPath = fc.getSelectedFile().getAbsolutePath();
						saveSettings();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frmChat, "Error loading the image!", "Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		});
		popupMenu_2.add(mntmSetWindowIcon);
		
		separator = new JSeparator();
		popupMenu_2.add(separator);
		
		mntmSetDefaultTitle = new JMenuItem("Set to default title");
		mntmSetDefaultTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmChat.setTitle("Shared Folder Chat");
				saveSettings();
			}
		});
		popupMenu_2.add(mntmSetDefaultTitle);
		
		mntmSetDefaultIcon = new JMenuItem("Set to default icon");
		mntmSetDefaultIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmChat.setIconImage(Toolkit.getDefaultToolkit().getImage(ChatWindow.class.getResource("/de/wenzel/niklas/sharedfolderchat/icon1.png")));
				iconPath = "default";
				saveSettings();
			}
		});
		popupMenu_2.add(mntmSetDefaultIcon);
		
		frmChat.getContentPane().setLayout(new MigLayout("", "[:82.00px:108.00px,grow][grow]", "[][grow][]"));
		
		chatsList = new JList(chatsListModel);
		chatsList.setToolTipText("<html> List of all entered chats. <br> Right click to enable/disable notifications or to change the updating time. </html>");
		chatsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chatsList.setSelectedIndex(0);
		chatsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectedChatIndex = chatsList.getSelectedIndex();
				updateMsgList();
				saveSettings();
			}
		});
		
		btnAbout = new JButton("About");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutDialog aboutDialog = new AboutDialog(frmChat);
				aboutDialog.setVisible(true);
			}
		});
		frmChat.getContentPane().add(btnAbout, "cell 1 0,alignx right");
		
		msgList = new JList(msgListModel);
		msgList.setToolTipText("The messages in the selected chat are shown here.");
		msgList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_COPY) {
					msgList.getTransferHandler().exportToClipboard(msgList, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
				}
			}
		});
		msgList.setFocusable(true);
		msgList.setDragEnabled(true);
		
		JScrollPane msgScrollPane = new JScrollPane(msgList);
		
		popupMenu = new JPopupMenu();
		addPopup(msgList, popupMenu);
		
		mntmSave = new JMenuItem("Save selected lines");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int[] selected = msgList.getSelectedIndices();
				if (selected.length > 0) {
					FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Textfile (*.txt)", "TXT");
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setAcceptAllFileFilterUsed(false);
					fc.setFileFilter(txtFilter);
					fc.setDialogTitle("Save chat");
					int returnVal = fc.showOpenDialog(msgList);

					fc.removeChoosableFileFilter(txtFilter);
					fc.setAcceptAllFileFilterUsed(true);
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File textFile = fc.getSelectedFile();
						
						if (txtFilter.accept(textFile)) {
							FileWriter fw = null;
							try {
								fw = new FileWriter(textFile, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
							PrintWriter pw = new PrintWriter(fw);
							try {
								for (int i : selected) {
									pw.println(msgListModel.get(i));
								}
							} finally {
								try {
									fw.flush();
									fw.close();
								} catch (IOException e) {
									e.printStackTrace();
								}							
								pw.flush();
								pw.close();
							}
						} else {
							JOptionPane.showMessageDialog(frmChat, "Select a file with a \".txt\"-extension!", "Error", JOptionPane.ERROR_MESSAGE);
							actionPerformed(event);
						}
					}
				} else {
					JOptionPane.showMessageDialog(frmChat, "Nothing is selected!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateMsgList();
			}
		});
		popupMenu.add(mntmRefresh);
		popupMenu.addSeparator();
		
		mntmCopy = new JMenuItem("Copy");
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (msgList.getSelectedIndices().length > 0) {
					msgList.getTransferHandler().exportToClipboard(msgList, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
				} else {
					JOptionPane.showMessageDialog(frmChat, "Nothing is selected!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		popupMenu.add(mntmCopy);
		popupMenu.addSeparator();
		
		mntmSaveAll = new JMenuItem("Save all");
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				msgList.setSelectionInterval(0, msgListModel.getSize()-1);
				mntmSave.doClick();
			}
		});
		popupMenu.add(mntmSaveAll);
		popupMenu.add(mntmSave);
		JScrollPane chatsScrollPane = new JScrollPane(chatsList);
		
		popupMenu_1 = new JPopupMenu();
		popupMenu_1.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				if (notificationsOn) {
					menuItemToggleNotifications.setText("Disable Notifications");
				} else {
					menuItemToggleNotifications.setText("Enable Notifications");
				}
			}
		});
		addPopup(chatsList, popupMenu_1);
		
		menuItemToggleNotifications = new JMenuItem("");
		menuItemToggleNotifications.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notificationsOn = !notificationsOn;
			}
		});
		popupMenu_1.add(menuItemToggleNotifications);
		
		mntmSetUpdatingTime = new JMenuItem("Set updating time");
		mntmSetUpdatingTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				askForNewUpdateTime();
				
			}
		});
		popupMenu_1.add(mntmSetUpdatingTime);
		
		
		frmChat.getContentPane().add(msgScrollPane, "cell 1 1,grow");
		frmChat.getContentPane().add(chatsScrollPane, "cell 0 1,grow");
		
		msgText = new JTextField();
		msgText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (chatsList.getSelectedIndex() >= 0) {
					String message = msgText.getText();
					if (chatDir.get(selectedChatIndex).exists() && chatDir.get(selectedChatIndex).isDirectory()) {
						if (!message.isEmpty()) {
							if (usernameText.getText().equals(USERNAME) && !usernamePrompted) {
								JOptionPane.showMessageDialog(frmChat, "Please change your username!", "Warning", JOptionPane.WARNING_MESSAGE);
								usernamePrompted = true;
								return;
							} else if (usernameText.getText().isEmpty()) {
								JOptionPane.showMessageDialog(frmChat, "Please enter a username!", "Warning", JOptionPane.ERROR_MESSAGE);
								return;
							}
							File msgFile = new File(chatDir.get(selectedChatIndex), String.valueOf(System.currentTimeMillis()).concat(MSG_SUFFIX));
							FileWriter fw = null;
							PrintWriter pw = null;
							try {
								msgFile.createNewFile();
								
								fw = new FileWriter(msgFile.getPath(), false);
								
								pw = new PrintWriter(fw);
								pw.println(usernameText.getText());
								pw.println(message);
								
								msgText.setText("");
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								try {
									fw.flush();
									fw.close();
									
									pw.flush();
									pw.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							task.run();
								
							hideFile(msgFile);
						}
					} else {
						chatDir.remove(selectedChatIndex);
						updateChatsList();
					}
					
				} else {
					JOptionPane.showMessageDialog(frmChat, "Select a chat.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		msgText.requestFocusInWindow();
		msgText.setToolTipText("<html>Type your message here. <br> Press Enter to send.</html>");
		
		usernameText = new JTextField();
		usernameText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				saveSettings();
			}
		});
		usernameText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}
		});
		frmChat.getContentPane().add(usernameText, "cell 0 2,growx");
		usernameText.setColumns(10);
		usernameText.setToolTipText("Your username");
		
		frmChat.getContentPane().add(msgText, "cell 1 2,growx");
		msgText.setColumns(10);
		
		btnNewChat = new JButton("Create a new chat");
		btnNewChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select the parent directory where to create a new chat");
				int returnVal = fc.showOpenDialog(btnEnterAChat.getParent());
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File newChat = new File(fc.getSelectedFile(), CHAT_DIR_NAME);
					if (!newChat.exists() && !newChat.isDirectory()) {
						newChat.mkdir();
						hideFile(newChat);
						chatDir.add(newChat);
						updateChatsList();
						chatsList.setSelectedIndex(chatsListModel.getSize()-1);
					} else {
						JOptionPane.showMessageDialog(frmChat, "There is already a directory named \"".concat(CHAT_DIR_NAME).concat("\"! (Every chat directory has to be named \"").concat(CHAT_DIR_NAME).concat("\".)"), "Error: Failed to create a new chat", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}
		});
		frmChat.getContentPane().add(btnNewChat, "flowx,cell 0 0");
		
		btnLeaveTheChat = new JButton("Leave the chat");
		btnLeaveTheChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = chatsList.getSelectedIndex();
				if (selectedIndex >= 0) {
					chatDir.remove(selectedIndex);
					updateChatsList();
					chatsList.setSelectedIndex(selectedIndex-1);
					if ((chatsList.getSelectedIndex() < 0) && (chatsListModel.getSize() > 0)) {
						chatsList.setSelectedIndex(0);
					}
				} else {
					JOptionPane.showMessageDialog(frmChat, "No chat has been selected!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frmChat.getContentPane().add(btnLeaveTheChat, "cell 0 0");
		
		btnEnterAChat = new JButton("Enter a chat");
		btnEnterAChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter chatFilter = new FileNameExtensionFilter("Chat (.chat)", "CHAT");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(chatFilter);
				fc.setDialogTitle("Select the chat to enter");
				int returnVal = fc.showOpenDialog(btnEnterAChat.getParent());
				
				fc.removeChoosableFileFilter(chatFilter);
				fc.setAcceptAllFileFilterUsed(true);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File newChat = fc.getSelectedFile();
					if (newChat.getName().equals(CHAT_DIR_NAME)) {
						if (!chatDir.contains(newChat)) {
							chatDir.add(newChat);
							updateChatsList();
							chatsList.setSelectedIndex(chatsListModel.getSize()-1);
						} else {
							chatsList.setSelectedIndex(chatDir.indexOf(newChat));
						}
					} else {
						JOptionPane.showMessageDialog(frmChat, "A chat directory has to be named \"".concat(CHAT_DIR_NAME).concat("\"!"), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		frmChat.getContentPane().add(btnEnterAChat, "cell 0 0");
		
		timer = new Timer();
		task = new UpdateTask();

		boolean initialize = true;
		File settingsFile = new File(System.getProperty("user.home"), SETTINGS_FILE_NAME);
		if (settingsFile.exists() && settingsFile.isFile()) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(settingsFile));
				frmChat.setTitle(in.readLine());
				iconPath = in.readLine();
				if (!iconPath.equals("default")) {
					try {
						frmChat.setIconImage(ImageIO.read(new File(iconPath)));
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frmChat, "There was a problem loading the window icon.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				notificationsOn = Boolean.parseBoolean(in.readLine());
				task.updatingTime = Integer.parseInt(in.readLine());
				usernameText.setText(in.readLine());
				selectedChatIndex = Integer.parseInt(in.readLine());
				String chatDirectory;
				File addChat;
				while ((chatDirectory = in.readLine()) != null) {
					addChat = new File(chatDirectory);
					if (addChat.exists() && addChat.isDirectory()) {
						chatDir.add(addChat);
					}
					
				}
				updateChatsList();
				initialize = false;
			} catch (Exception e) {
				settingsFile.delete();
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (initialize) {
			showAboutDialogAtStart = true;
			usernameText.setText(JOptionPane.showInputDialog(frmChat, "Please enter your username!", System.getProperty("user.name")));
			if (!usernameText.getText().isEmpty()) {
				saveSettings();
			} else {
				usernameText.setText(USERNAME);
			}
		}
		
		timer.schedule(task, 0, task.updatingTime);
	}

	protected void askForNewUpdateTime() {
		String timeString = JOptionPane.showInputDialog(frmChat, "Please enter the updating time in milliseconds.", task.updatingTime);
		if (timeString != null) {
			try {
				int timeInt = Integer.parseInt(timeString);
				if (timeInt > 100 && timeInt < 3600000) {
					setNewUpdateTime(timeInt);
					return;
				} else {
					JOptionPane.showMessageDialog(frmChat, "The updating time has to be a value between 100 and 3600000 milliseconds.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frmChat, "Please enter a valid updating time.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			askForNewUpdateTime();
		}
		
	}

	private void setNewUpdateTime(int time) {
		task.cancel();
		timer.purge();
		task = new UpdateTask(time);
		timer.schedule(task, 0, task.updatingTime);
		saveSettings();
	}

	protected void updateMsgList() {
		msgListModel.removeAllElements();
		savedLength = 0;
		task.run();
	}

	private void updateChatsList() {
		chatsListModel.removeAllElements();
		for (int i = 0; i < chatDir.size(); i++) {
			chatsListModel.addElement(chatDir.get(i).getParentFile().getName());
		}
		chatsList.setSelectedIndex(selectedChatIndex);
		saveSettings();
	}
	
	public int find(File[] array, File value) {
	    for(int i=0; i<array.length; i++) {
	         if(array[i] == value) {
	             return i;
	         }
	    }
		return -1;
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	public void saveSettings() {
		File settingsFile = new File(System.getProperty("user.home"), SETTINGS_FILE_NAME);
		FileWriter fw = null;
		PrintWriter pw = null;
		
		try {
			fw = new FileWriter(settingsFile, false);
			pw = new PrintWriter(fw);
			pw.println(frmChat.getTitle());
			pw.println(iconPath);
			pw.println(notificationsOn);
			pw.println(task.updatingTime);
			pw.println(usernameText.getText());
			pw.println(this.selectedChatIndex);
			for (int i = 0; i<chatDir.size(); i++) {
				pw.println(chatDir.get(i).getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.flush();
				fw.close();
				
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	public static void hideFile(File f) {
		try {
			Process p = Runtime.getRuntime().exec("attrib +h " + f.getPath());
			p.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	public class UpdateTask extends TimerTask {
		private TimeComparator timeComparator = new TimeComparator();
		int updatingTime = 500;

		public UpdateTask(int time) {
			super();
			updatingTime = time;
		}
		
		public UpdateTask() {
			super();
		}
		

		public void run() {
			if (chatsList.getSelectedIndex() != -1) {
				if (chatDir.get(selectedChatIndex).exists() && chatDir.get(selectedChatIndex).isDirectory()) {
					File[] fileList = chatDir.get(selectedChatIndex ).listFiles();
			    	Arrays.sort(fileList, timeComparator);
					if (fileList.length > savedLength) {
						int difference = fileList.length - savedLength;
						
						BufferedReader in = null;
						for (int i = 1; i <= difference; i++) {
							if (fileList[difference - i].getName().endsWith(MSG_SUFFIX)) {
								try {
									in = new BufferedReader(new FileReader(fileList[difference - i]));
									String name = in.readLine();
									String text = in.readLine();
									final String message = "<".concat(name).concat(">: ").concat(text);
									javax.swing.SwingUtilities.invokeLater(new Runnable() {
					                    public void run() {
					                        msgListModel.addElement(message);
					                    }
					                });
									
									if (notificationsOn && (savedLength != 0) && (!name.equals(usernameText.getText()))) {
										notification.setNotificationText(name, text);
									}
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										in.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
						savedLength = fileList.length;
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
		                    public void run() {
		                    	if (msgListModel.size() > savedLength) {
		                    		updateMsgList();
		                    	}
		                        msgList.ensureIndexIsVisible(msgListModel.size() - 1);
		                    }
		                });
						
					} else if (fileList.length < savedLength) {
						savedLength = 0;
						msgListModel.removeAllElements();
						run();
					} 
				} else {
					chatDir.remove(selectedChatIndex);
					updateChatsList();
					JOptionPane.showMessageDialog(frmChat, "This chat does not exist any more. Someone has deleted the directory.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
	    	
	    }
	}
}
