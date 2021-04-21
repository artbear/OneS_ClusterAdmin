package ru.yanygin.clusterAdminLibraryUI;

import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.InfoBaseInfo;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ViewerArea extends Composite {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;
	Image infobaseIcon;
	
	Tree serversTree;
	Menu serversTreeMenu;
	
	Table tableSessions;
//	Menu tableSessionsMenu;
	
	Table tableConnections;
//	Menu tableSessionsMenu;

	
	ClusterProvider clusterProvider;

	public ViewerArea(Composite parent, int style, ApplicationWindow applicationWindow, ClusterProvider clusterProvider) {
		super(parent, style);
		
		this.clusterProvider = clusterProvider;
		String configPath = "C:\\git\\OneS_ClusterAdmin\\config.json";
		this.clusterProvider.readSavedKnownServers(configPath);

		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		initIcon();
		
		initToolbar(parent, applicationWindow, clusterProvider);
		
		initServersTree(sashForm);
		
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		initSessionTable(tabFolder);//sashForm);
		initConnectionsTable(tabFolder);//sashForm);
		
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		// Заполнение списка серверов
		clusterProvider.getServers().forEach((server, serverConfig) -> {
			TreeItem serverItem = addServerItemInServersTree(serverConfig);
			
			if (serverConfig.clusterConnector.isConnected()) {
				// Заполнение списка инфобаз
				List<IInfoBaseInfo> infoBaseInfoList = clusterProvider.getInfobases(serverConfig);
				for (IInfoBaseInfo infoBaseInfo : infoBaseInfoList) {
					addInfobaseItemInServersTree(serverItem, infoBaseInfo);
				}
			}
		});

		
		// Пропорции областей
		sashForm.setWeights(new int[] {1, 4});

	}

	private void initToolbar(Composite parent, ApplicationWindow applicationWindow, ClusterProvider clusterProvider) {
		ToolBar toolBar = applicationWindow.getToolBarManager().createControl(parent);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolBar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolBar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (handCursor.isDisposed() == false) {
					handCursor.dispose();
				}
			}
		});
		
		ToolItem toolBarItemFindNewServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemFindNewServers.setText("Find new Servers");
		toolBarItemFindNewServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> newServers = clusterProvider.findNewServers();
				if (!newServers.isEmpty()) {
					fillServersList();
				}
			}
		});

		ToolItem toolBarItemConnectAllServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectAllServers.setText("Connect all servers");		
		toolBarItemConnectAllServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				clusterProvider.connectToServers();
				List<String> connectedServers = clusterProvider.getConnectedServers();
				
				if (connectedServers.isEmpty())
					return;
				
				// смена иконки сервера на вкл/выкл
				TreeItem[] serversItem = serversTree.getItems();
				for (int i = 0; i < serversItem.length; i++) {
					TreeItem serverItem = serversItem[i];
					
					String serverKey = (String) serverItem.getData("ServerKey"); // serverItem.getText();
					if (connectedServers.contains(serverKey)) {
						serverItem.setImage(serverIconUp);
						
						// заполнение списка баз у подключенных серверов
						Server serverConfig = (Server) serverItem.getData("ServerConfig");
						if (serverConfig.clusterConnector.isConnected()) {
							List<IInfoBaseInfo> infoBaseInfoList = clusterProvider.getInfobases(serverConfig);
							for (IInfoBaseInfo infoBaseInfo : infoBaseInfoList) {
								addInfobaseItemInServersTree(serverItem, infoBaseInfo);
							}
						}

					}
					else {
						serversItem[i].setImage(serverIconDown);
					}
				}
				
			}
		});
	}

	private void initServersTree(SashForm sashForm) {
	
		serversTree = new Tree(sashForm, SWT.BORDER);
		serversTree.setHeaderVisible(true);
		
		serversTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// нужно сделать, что бы была реакция только на левый клик мышью!

				tableSessions.removeAll();
				tableConnections.removeAll();

				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
//				TreeItem serverItem = item[0];
				TreeItem serverItem = (TreeItem) e.item;
				Server serverConfig;
				InfoBaseInfo infoBaseInfo;
				List<ISessionInfo> sessions;
				List<IInfoBaseConnectionShort> connections;

				switch ((String) serverItem.getData("Type")) {
				case "Server":
					serversTree.setMenu(serversTreeMenu);

					serverConfig = (Server) serverItem.getData("ServerConfig");
					infoBaseInfo = null;
					
					sessions = serverConfig.getSessions();
					connections = serverConfig.getConnections();
					break;
				case "Infobase":
					serversTree.setMenu(null);

					serverConfig = (Server) serverItem.getParentItem().getData("ServerConfig");
					infoBaseInfo = (InfoBaseInfo) serverItem.getData("InfoBaseInfo");
					
					sessions = serverConfig.getInfoBaseSessions(infoBaseInfo);
					connections = null;//serverConfig.getConnections();
					break;
				default:
					return;
//					break;
				}

				sessions.forEach(session -> {
					addSessionInTable(serverConfig, infoBaseInfo, session);
				});

				connections.forEach(connection -> {
					addConnectionInTable(serverConfig, infoBaseInfo, connection);
				});
			}

			private void addSessionInTable(Server serverConfig, InfoBaseInfo infoBaseInfo, ISessionInfo session) {
				TableItem sessionItem = new TableItem(tableSessions, SWT.NONE);

				String infobaseName = serverConfig.getInfoBaseName(session.getInfoBaseId());

				String[] itemText = { session.getAppId(),
									session.getConnectionId().toString(),
									session.getHost(),
									infobaseName,
									session.getLastActiveAt().toString(),
									Integer.toString(session.getSessionId()),
									session.getStartedAt().toString(),
									session.getUserName(),
									session.getWorkingProcessId().toString() };

				sessionItem.setText(itemText);
				sessionItem.setData("SessionInfo", session);
				sessionItem.setData("ServerConfig", serverConfig);
				sessionItem.setData("InfoBaseInfo", infoBaseInfo);
				sessionItem.setChecked(false);
			}
			
			private void addConnectionInTable(Server serverConfig, InfoBaseInfo infoBaseInfo, IInfoBaseConnectionShort connection) {
				TableItem connectionItem = new TableItem(tableConnections, SWT.NONE);

				String infobaseName = serverConfig.getInfoBaseName(connection.getInfoBaseId());

				String[] itemText = { connection.getApplication(),
									Integer.toString(connection.getConnId()),
									connection.getHost(),
									infobaseName,
									connection.getInfoBaseConnectionId().toString(),
									connection.getConnectedAt().toString(),
									Integer.toString(connection.getSessionNumber()),
									connection.getWorkingProcessId().toString() };

				connectionItem.setText(itemText);
				connectionItem.setData("Connection", connection);
				connectionItem.setData("ServerConfig", serverConfig);
				connectionItem.setData("InfoBaseInfo", infoBaseInfo);
				connectionItem.setChecked(false);
			}

		});
		
		initServersTreeContextMenu();
		
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Cluster/Infobase");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
		columnPing.setText("RAS port");
		columnPing.setWidth(60);
	}
	
	
	private void initServersTreeContextMenu() {
		serversTreeMenu = new Menu(serversTree);
		serversTree.setMenu(serversTreeMenu);

		MenuItem menuItemAddNewServer = new MenuItem(serversTreeMenu, SWT.NONE);
		menuItemAddNewServer.setText("Add Server");
		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Server newServerConfig = clusterProvider.CreateNewServer();
				EditServerDialog connectionDialog;
//				try {
					connectionDialog = new EditServerDialog(getParent().getDisplay().getActiveShell(), newServerConfig);
//				} catch (Exception e1) {
//					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
//					return;
//				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					newServerConfig = null;
				}
				else {
					clusterProvider.addNewServerInList(newServerConfig);
					TreeItem newServerItem = addServerItemInServersTree(newServerConfig);
					
					if (newServerConfig.autoconnect && newServerConfig.connect(false)) {
						newServerItem.setImage(serverIconUp);

						newServerConfig.getInfoBases().forEach(infoBaseInfo-> {
							addInfobaseItemInServersTree(newServerItem, infoBaseInfo);
						});
						newServerItem.setExpanded(true);
					} else {
						newServerItem.setImage(serverIconDown);
					}

				}
			}
		});
		
		MenuItem menuItemEditServer = new MenuItem(serversTreeMenu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				TreeItem serverItem = item[0];
				Server serverConfig = (Server) serverItem.getData("ServerConfig");
				EditServerDialog connectionDialog;
//				try {
					connectionDialog = new EditServerDialog(getParent().getDisplay().getActiveShell(), serverConfig);
//				} catch (Exception e1) {
//					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
//					return;
//				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult == 0) {
					// перерисовать в дереве
					serverItem.setText(new String[] { serverConfig.getServerPresent() });
					clusterProvider.saveKnownServers();
					
					if (serverConfig.autoconnect && serverConfig.connect(false)) {
						serverItem.setImage(serverIconUp);

						serverConfig.getInfoBases().forEach(infoBaseInfo-> {
							addInfobaseItemInServersTree(serverItem, infoBaseInfo);
						});
						serverItem.setExpanded(true);
					} else {
						serverItem.setImage(serverIconDown);
					}
					
				}

			}
		});

		
		MenuItem menuItemDeleteServer = new MenuItem(serversTreeMenu, SWT.NONE);
		menuItemDeleteServer.setText("Delete Server");
		menuItemDeleteServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server serverConfig = (Server) item[0].getData("ServerConfig");
				
				clusterProvider.removeServerInList(serverConfig);
				
				item[0].dispose();
			}
		});
	}
	

	private TreeItem addServerItemInServersTree(Server config) {
		TreeItem item = new TreeItem(serversTree, SWT.NONE);
		
		item.setText(new String[] { config.getServerPresent()});//, config.getRemoteRasPortAsString() });
		item.setData("Type", "Server");
		item.setData("ServerKey", config.getServerKey()); // del
//		item.setData("RASPort", config.remoteRasPort); // del
		item.setData("ServerConfig", config);
		
		if (config.clusterConnector.isConnected()) {
			item.setImage(serverIconUp);
		} else {
			item.setImage(serverIconDown);
		}
		item.setChecked(false);
		
		return item;
	}
	

	private void addInfobaseItemInServersTree(TreeItem serverItem, IInfoBaseInfo ibs) {
		TreeItem item = new TreeItem(serverItem, SWT.NONE);
		
		item.setText(new String[] { ibs.getDbName()});//, config.getRemoteRasPortAsString() });
		item.setData("Type", "Infobase");
		item.setData("BaseName", ibs.getDbName()); // del
//		item.setData("RASPort", config.remoteRasPort); // del
		item.setData("InfoBaseInfo", ibs);
		item.setImage(infobaseIcon);
		item.setChecked(false);
	}
	
	

	private void initSessionTable(TabFolder tabFolder) {

		TabItem tabSessions = new TabItem(tabFolder, SWT.NONE);
		tabSessions.setText("Sessions");

		tableSessions = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		tabSessions.setControl(tableSessions);
		tableSessions.setHeaderVisible(true);
		tableSessions.setLinesVisible(true);
		
		addSessionsTableContextMenu();
		
		TableColumn tblclmnAppID = new TableColumn(tableSessions, SWT.NONE);
		tblclmnAppID.setWidth(100);
		tblclmnAppID.setText("Application");
		
		TableColumn tblclmnConnectionID = new TableColumn(tableSessions, SWT.NONE);
		tblclmnConnectionID.setWidth(100);
		tblclmnConnectionID.setText("ConnectionID");
		
		TableColumn tblclmnHostname = new TableColumn(tableSessions, SWT.NONE);
		tblclmnHostname.setWidth(100);
		tblclmnHostname.setText("Hostname");
		
		TableColumn tblclmnInfobaseID = new TableColumn(tableSessions, SWT.NONE);
		tblclmnInfobaseID.setWidth(100);
		tblclmnInfobaseID.setText("Infobase ID");
		
		TableColumn tblclmnLastActive = new TableColumn(tableSessions, SWT.NONE);
		tblclmnLastActive.setWidth(100);
		tblclmnLastActive.setText("Last active at");
		
		TableColumn tblclmnSessionID = new TableColumn(tableSessions, SWT.NONE);
		tblclmnSessionID.setWidth(100);
		tblclmnSessionID.setText("SessionID");
		
		TableColumn tblclmnStartedAt = new TableColumn(tableSessions, SWT.NONE);
		tblclmnStartedAt.setWidth(100);
		tblclmnStartedAt.setText("Started At");
		
		TableColumn tblclmnUserName = new TableColumn(tableSessions, SWT.NONE);
		tblclmnUserName.setWidth(100);
		tblclmnUserName.setText("Username");
		
		TableColumn tblclmnWorkingProcessID = new TableColumn(tableSessions, SWT.NONE);
		tblclmnWorkingProcessID.setWidth(100);
		tblclmnWorkingProcessID.setText("rphost ID");
		
	}

	private void addSessionsTableContextMenu() {
		
		Menu tableSessionsMenu = new Menu(tableSessions);
		tableSessions.setMenu(tableSessionsMenu);
		
		MenuItem menuItemKillSession = new MenuItem(tableSessionsMenu, SWT.NONE);
		menuItemKillSession.setText("Kill session");
		menuItemKillSession.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selectedItems = tableSessions.getSelection();
				if (selectedItems.length == 0)
					return;
				
				for (TableItem item : selectedItems) {
					ISessionInfo sessionInfo = (ISessionInfo) item.getData("SessionInfo");
					Server server = (Server) item.getData("ServerConfig");
					server.terminateSession(sessionInfo.getSid());
				}
				
			}
		});
	}
	
	private void initConnectionsTable(TabFolder tabFolder) {

		TabItem tabConnections = new TabItem(tabFolder, SWT.NONE);
		tabConnections.setText("Connections");
		
		tableConnections = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		tabConnections.setControl(tableConnections);
		tableConnections.setHeaderVisible(true);
		tableConnections.setLinesVisible(true);
		
		addConnectionsTableContextMenu();
		
		TableColumn tblclmnAppID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnAppID.setWidth(100);
		tblclmnAppID.setText("Application");
		
		TableColumn tblclmnConnectionID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnConnectionID.setWidth(100);
		tblclmnConnectionID.setText("ConnectionID");
		
		TableColumn tblclmnHostname = new TableColumn(tableConnections, SWT.NONE);
		tblclmnHostname.setWidth(100);
		tblclmnHostname.setText("Hostname");
		
		TableColumn tblclmnInfobaseID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnInfobaseID.setWidth(100);
		tblclmnInfobaseID.setText("Infobase ID");
		
		TableColumn tblclmnInfobaseConnectionID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnInfobaseConnectionID.setWidth(100);
		tblclmnInfobaseConnectionID.setText("Infobase connection ID");
		
		TableColumn tblclmnLastActive = new TableColumn(tableConnections, SWT.NONE);
		tblclmnLastActive.setWidth(100);
		tblclmnLastActive.setText("Connected at");
		
		TableColumn tblclmnSessionID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnSessionID.setWidth(100);
		tblclmnSessionID.setText("SessionNumber"); // SessionID
		
//		TableColumn tblclmnStartedAt = new TableColumn(tableConnections, SWT.NONE);
//		tblclmnStartedAt.setWidth(100);
//		tblclmnStartedAt.setText("Started At");
		
//		TableColumn tblclmnUserName = new TableColumn(tableConnections, SWT.NONE);
//		tblclmnUserName.setWidth(100);
//		tblclmnUserName.setText("Username");
		
		TableColumn tblclmnWorkingProcessID = new TableColumn(tableConnections, SWT.NONE);
		tblclmnWorkingProcessID.setWidth(100);
		tblclmnWorkingProcessID.setText("rphost ID");
		
	}

	private void addConnectionsTableContextMenu() {
		
		// Пока не понятен остав меню
		
//		Menu tableConnectionsMenu = new Menu(tableConnections);
//		tableConnections.setMenu(tableConnectionsMenu);
//		
//		MenuItem menuItemKillSession = new MenuItem(tableConnectionsMenu, SWT.NONE);
//		menuItemKillSession.setText("Kill session");
//		menuItemKillSession.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				TableItem[] selectedItems = tableConnections.getSelection();
//				if (selectedItems.length == 0)
//					return;
//				
////				for (TableItem item : selectedItems) {
////					ISessionInfo sessionInfo = (ISessionInfo) item.getData("SessionInfo");
////					Server server = (Server) item.getData("ServerConfig");
////					server.terminateSession(sessionInfo.getSid());
////				}
//				
//			}
//		});
	}
		

	private void initIcon() {
		serverIcon = getImage(getParent().getDisplay(), "/server_24.png");
		serverIconUp = getImage(getParent().getDisplay(), "/server_up_24.png");
		serverIconDown = getImage(getParent().getDisplay(), "/server_down_24.png");
		infobaseIcon = getImage(getParent().getDisplay(), "/infobase_24.png");
//		serverIcon = getImage(mainForm.getDisplay(), "/icons/server_24.png");
//		serverIconUp = getImage(mainForm.getDisplay(), "/icons/server_up_24.png");
//		serverIconDown = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");
	}
	
	
	private Image getImage(Device device, String name) {
		return new Image(device, this.getClass().getResourceAsStream(name));
	}
	

	protected void fillServersList() {
		// TODO Auto-generated method stub
		
	}

}
