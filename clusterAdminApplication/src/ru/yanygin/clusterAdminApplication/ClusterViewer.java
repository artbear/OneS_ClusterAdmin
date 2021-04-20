package ru.yanygin.clusterAdminApplication;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.InfoBaseInfo;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config.Server;
//import ru.yanygin.clusterAdminLibraryUI.ServersTree;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ClusterViewer extends ApplicationWindow {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;
	Image infobaseIcon;

	Tree serversTree;
	Composite mainForm;
	
	ClusterProvider clusterProvider = new ClusterProvider();
	private Table tableOfSessions;

	/**
	 * Create the application window.
	 */
	public ClusterViewer() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		addItems();
		
//		String configPath = "C:\\1C_EDT_WS\\clusterAdmin\\clusteradmin.config";
//		String configPath = "C:\\git\\OneS_ClusterAdmin\\config.json";
//
//		clusterProvider.readSavedKnownServers(configPath);
//		clusterProvider.checkConnectToServers();

	}

	@Override
	public boolean close() {
		
		clusterProvider.getServers().forEach((server, config) -> {
			if (config.clusterConnector.isConnected())
				config.clusterConnector.disconnect();
		});
		
		return super.close();
	}
	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		this.mainForm = parent;
		
		//ViewerArea container = new ViewerArea(parent, SWT.NONE, this, clusterProvider);
		
		Composite container = alternativeInit(parent);
		
		
		return container;
	}

	private Composite alternativeInit(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		// Toolbar
//		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
//		toolBar.setBounds(0, 0, 500, 23);
		ToolBar toolBar = getToolBarManager().createControl(container);
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
//				List<String> newServers = clusterProvider.findNewServers();
//				if (!newServers.isEmpty()) {
//					fillServersList();
//				}
			}
		});

		ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectToServers.setText("Connect to servers");		
		toolBarItemConnectToServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
//				clusterProvider.connectToServers();
//				List<String> connectedServers = clusterProvider.getConnectedServers();
//				
//				if (connectedServers.isEmpty())
//					return;
//				
//				// смена иконки сервера на вкл/выкл
//				TreeItem[] serversItem = serversTree.getItems();
//				for (int i = 0; i < serversItem.length; i++) {
//					TreeItem serverItem = serversItem[i];
//					
//					String serverKey = (String) serverItem.getData("ServerKey"); // serverItem.getText();
//					if (connectedServers.contains(serverKey)) {
//						serverItem.setImage(serverIconUp);
//						
//						// заполнение списка баз у подключенных серверов
//						Server serverConfig = (Server) serverItem.getData("ServerConfig");
//						if (serverConfig.clusterConnector.isConnected()) {
//							List<IInfoBaseInfo> infoBaseInfoList = clusterProvider.getInfobases(serverConfig);
//							for (IInfoBaseInfo infoBaseInfo : infoBaseInfoList) {
////								addInfobaseItemInServersTree(serverItem, infoBaseInfo);
//							}
//						}
//
//					}
//					else {
//						serversItem[i].setImage(serverIconDown);
//					}
//				}
				
			}
		});
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(container, SWT.NONE);
		
		
		serversTree = new Tree(sashForm, SWT.BORDER);

//		serversTree = new Tree(sashForm, SWT.BORDER);
//		serversTree.addSelectionListener(new SelectionAdapter() {
////			@Override
////			public void widgetDefaultSelected(SelectionEvent e) {
////				int i = 0;
////			}
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				tableOfSessions.removeAll();
//
//				TreeItem[] item = serversTree.getSelection();
//				if (item.length == 0)
//					return;
//				TreeItem serverItem = item[0];
//				Server serverConfig;
//				List<ISessionInfo> sessions;
//
//				switch ((String) serverItem.getData("Type")) {
//				case "Server":
//					serverConfig = (Server) serverItem.getData("ServerConfig");
//					sessions = serverConfig.getSessions();
//					break;
//				case "Infobase":
//					InfoBaseInfo infoBaseInfo = (InfoBaseInfo) serverItem.getData("InfoBaseInfo");
//
//					serverConfig = (Server) serverItem.getParentItem().getData("ServerConfig");
//					sessions = serverConfig.getInfoBaseSessions(infoBaseInfo);
//					break;
//				default:
//					return;
////					break;
//				}
//				
////				List<IInfoBaseInfo> infobases = serverConfig.getInfoBases();
//				
//				sessions.forEach(session -> {
//					
//					TableItem sessionItem = new TableItem(tableOfSessions, SWT.NONE);
//					
//					String infobaseName = serverConfig.getInfoBaseName(session.getInfoBaseId());
//					
//					String[] itemText = { session.getAppId(),
//										session.getConnectionId().toString(),
//										session.getHost(),
//										infobaseName,
//										session.getLastActiveAt().toString(),
//										Integer.toString(session.getSessionId()),
//										session.getStartedAt().toString(),
//										session.getUserName(),
//										session.getWorkingProcessId().toString()};
//					
//					sessionItem.setText(itemText);
//					sessionItem.setChecked(false);
//					
//				});
//				
//			}
//		});
//		
		serversTree.setHeaderVisible(true);
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Cluster/Infobase");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
		columnPing.setText("RAS port");
		columnPing.setWidth(60);
//		
////		TreeColumn columnBase = new TreeColumn(serversTree, SWT.LEFT);
////		columnBase.setText("Base name");
////		columnBase.setWidth(200);
//		
		Menu menu = new Menu(serversTree);
		serversTree.setMenu(menu);
//		
		MenuItem menuItemEditServer = new MenuItem(menu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
//				TreeItem[] item = serversTree.getSelection();
//				if (item.length == 0)
//					return;
//				TreeItem serverItem = item[0];
//				Server serverConfig = (Server) serverItem.getData("ServerConfig");
//				EditServerConnectionDialog connectionDialog;
////				try {
//					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), serverConfig);
////				} catch (Exception e1) {
////					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
////					return;
////				}
//				
//				int dialogResult = connectionDialog.open();
//				if (dialogResult == 0) {
//					// перерисовать в дереве
//					serverItem.setText(new String[] { serverConfig.getServerPresent() });
//					clusterProvider.saveKnownServers();
//					
//					if (serverConfig.autoconnect && serverConfig.connect(false)) {
//						serverItem.setImage(serverIconUp);
//
//						serverConfig.getInfoBases().forEach(infoBaseInfo-> {
//							addInfobaseItemInServersTree(serverItem, infoBaseInfo);
//						});
//						serverItem.setExpanded(true);
//					} else {
//						serverItem.setImage(serverIconDown);
//					}
//					
//				}

			}
		});
		
//		MenuItem menuItemAddNewServer = new MenuItem(menu, SWT.NONE);
//		menuItemAddNewServer.setText("Add new Server");
//		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				Server newServerConfig = clusterProvider.CreateNewServer();
//				EditServerConnectionDialog connectionDialog;
////				try {
//					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), newServerConfig);
////				} catch (Exception e1) {
////					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
////					return;
////				}
//				
//				int dialogResult = connectionDialog.open();
//				if (dialogResult != 0) {
//					newServerConfig = null;
//				}
//				else {
//					clusterProvider.addNewServerInList(newServerConfig);
//					TreeItem newServerItem = addServerItemInServersTree(newServerConfig);
//					
//					if (newServerConfig.autoconnect && newServerConfig.connect(false)) {
//						newServerItem.setImage(serverIconUp);
//
//						newServerConfig.getInfoBases().forEach(infoBaseInfo-> {
//							addInfobaseItemInServersTree(newServerItem, infoBaseInfo);
//						});
//						newServerItem.setExpanded(true);
//					} else {
//						newServerItem.setImage(serverIconDown);
//					}
//
//				}
//			}
//		});
		
//		MenuItem menuItemDeleteServer = new MenuItem(menu, SWT.NONE);
//		menuItemDeleteServer.setText("Delete Server");
//		menuItemDeleteServer.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				TreeItem[] item = serversTree.getSelection();
//				if (item.length == 0)
//					return;
//				
//				Server serverConfig = (Server) item[0].getData("ServerConfig");
//				
//				clusterProvider.removeServerInList(serverConfig);
//				
//				item[0].dispose();
//			}
//		});
		
		tableOfSessions = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		tableOfSessions.setHeaderVisible(true);
		tableOfSessions.setLinesVisible(true);
		
		Menu menu2 = new Menu(tableOfSessions);
		tableOfSessions.setMenu(menu2);
		
		MenuItem menuItemEditServer_1 = new MenuItem(menu2, SWT.NONE);
		menuItemEditServer_1.setText("Edit Server");
//		
		TableColumn tblclmnAppID = new TableColumn(tableOfSessions, SWT.NONE);
		tblclmnAppID.setWidth(100);
		tblclmnAppID.setText("Application");
//		
//		TableColumn tblclmnConnectionID = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnConnectionID.setWidth(100);
//		tblclmnConnectionID.setText("ConnectionID");
//		
//		TableColumn tblclmnHostname = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnHostname.setWidth(100);
//		tblclmnHostname.setText("Hostname");
//		
//		TableColumn tblclmnInfobaseID = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnInfobaseID.setWidth(100);
//		tblclmnInfobaseID.setText("Infobase ID");
//		
//		TableColumn tblclmnLastActive = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnLastActive.setWidth(100);
//		tblclmnLastActive.setText("Last active at");
//		
//		TableColumn tblclmnSessionID = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnSessionID.setWidth(100);
//		tblclmnSessionID.setText("SessionID");
//		
//		TableColumn tblclmnStartedAt = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnStartedAt.setWidth(100);
//		tblclmnStartedAt.setText("Started At");
//		
//		TableColumn tblclmnUserName = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnUserName.setWidth(100);
//		tblclmnUserName.setText("Username");
//		
//		TableColumn tblclmnWorkingProcessID = new TableColumn(tableOfSessions, SWT.NONE);
//		tblclmnWorkingProcessID.setWidth(100);
//		tblclmnWorkingProcessID.setText("rphost ID");

		sashForm.setWeights(new int[] {1, 2});
	
//		initIcon();
		
//		clusterProvider.getServers().forEach((server, serverConfig) -> {
//			TreeItem serverItem = addServerItemInServersTree(serverConfig);
//			
//			if (serverConfig.clusterConnector.isConnected()) {
//				List<IInfoBaseInfo> infoBaseInfoList = clusterProvider.getInfobases(serverConfig);
//				for (IInfoBaseInfo infoBaseInfo : infoBaseInfoList) {
//					addInfobaseItemInServersTree(serverItem, infoBaseInfo);
//				}
//			}
//		});
		return container;
	}

//	private TreeItem addServerItemInServersTree(Server config) {
//		TreeItem item = new TreeItem(serversTree, SWT.NONE);
//		
//		item.setText(new String[] { config.getServerPresent()});//, config.getRemoteRasPortAsString() });
//		item.setData("Type", "Server");
//		item.setData("ServerKey", config.getServerKey()); // del
////		item.setData("RASPort", config.remoteRasPort); // del
//		item.setData("ServerConfig", config);
//		
//		if (config.clusterConnector.isConnected()) {
//			item.setImage(serverIconUp);
//		} else {
//			item.setImage(serverIconDown);
//		}
//		item.setChecked(false);
//		
//		return item;
//	}
//
//	private void addInfobaseItemInServersTree(TreeItem serverItem, IInfoBaseInfo ibs) {
//		TreeItem item = new TreeItem(serverItem, SWT.NONE);
//		
//		item.setText(new String[] { ibs.getDbName()});//, config.getRemoteRasPortAsString() });
//		item.setData("Type", "Infobase");
//		item.setData("BaseName", ibs.getDbName()); // del
////		item.setData("RASPort", config.remoteRasPort); // del
//		item.setData("InfoBaseInfo", ibs);
//		item.setImage(infobaseIcon);
//		item.setChecked(false);
//	}
	
	protected void addItems() {


	}

	protected void fillServersList() {
		// TODO Auto-generated method stub
		
	}

//	private void initIcon() {
//		serverIcon = getImage(mainForm.getDisplay(), "/server_24.png");
//		serverIconUp = getImage(mainForm.getDisplay(), "/server_up_24.png");
//		serverIconDown = getImage(mainForm.getDisplay(), "/server_down_24.png");
//		infobaseIcon = getImage(mainForm.getDisplay(), "/infobase_24.png");
////		serverIcon = getImage(mainForm.getDisplay(), "/icons/server_24.png");
////		serverIconUp = getImage(mainForm.getDisplay(), "/icons/server_up_24.png");
////		serverIconDown = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");
//	}
//	
//	private Image getImage(Device device, String name) {
//		return new Image(device, this.getClass().getResourceAsStream(name));
//	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

//	/**
//	 * Launch the application.
//	 * @param args
//	 */
//	public static void main(String args[]) {
//		try {
//			ClusterViewer window = new ClusterViewer();
//			window.setBlockOnOpen(true);
//			window.open();
//			Display.getCurrent().dispose();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Cluster Administrating");
		// icon
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1000, 600);
	}
}
