package ru.yanygin.clusterAdminApplication;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ClusterViewer2 extends ApplicationWindow {
	
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;

	Tree serversTree;
	Composite mainForm;
	
	ClusterProvider clusterProvider = new ClusterProvider();

	/**
	 * Create the application window.
	 */
	public ClusterViewer2() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		addItems();
		
		String configPath = "C:\\1C_EDT_WS\\clusterAdmin\\clusteradmin.config";

		clusterProvider.readSavedKnownServers(configPath);
//		clusterProvider.checkConnectToServers();

	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		this.mainForm = parent;
		Composite container = new Composite(parent, SWT.NONE);
		

//		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		// Toolbar
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 500, 23);
		
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

		ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectToServers.setText("Connect to servers");		
		toolBarItemConnectToServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				clusterProvider.connectToServers();
				List<String> connectedServers = clusterProvider.getConnectedServers();
				
				if (connectedServers.isEmpty())
					return;
				
				TreeItem[] serversItem = serversTree.getItems();
				
				for (int i = 0; i < serversItem.length; i++) {
					
					String serverNameInTree = serversItem[i].getText(); // (String) serversItem[i].getData("ServerName");
					
					if (connectedServers.contains(serverNameInTree)) {
						serversItem[i].setImage(serverIconUp);
					}
					else {
						serversItem[i].setImage(serverIconDown);
					}
				}
				
			}
		});
		

		serversTree = new Tree(container, SWT.BORDER);
		serversTree.setLocation(0, 30);
		serversTree.setSize(500, 300);
		serversTree.setHeaderVisible(true);
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Cluster name");
		columnServer.setWidth(200);
		
//		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
//		columnPing.setText("RAS port");
//		columnPing.setWidth(60);
		
		TreeColumn columnBase = new TreeColumn(serversTree, SWT.LEFT);
		columnBase.setText("Base name");
		columnBase.setWidth(200);
		
		Menu menu = new Menu(serversTree);
		serversTree.setMenu(menu);
		
		MenuItem menuItemEditServer = new MenuItem(menu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				Server serverConfig = (Server) item[0].getData("ServerConfig");
				EditServerConnectionDialog connectionDialog;
//				try {
					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), serverConfig);
//				} catch (Exception e1) {
//					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
//					return;
//				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult == 0) {
					// перерисовать в дереве
					item[0].setText(new String[] { serverConfig.getServerPresent()});
				}

			}
		});
		
		MenuItem menuItemAddNewServer = new MenuItem(menu, SWT.NONE);
		menuItemAddNewServer.setText("Add new Server");
		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Server newServerParams = clusterProvider.CreateNewServer();
				EditServerConnectionDialog connectionDialog;
//				try {
					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), newServerParams);
//				} catch (Exception e1) {
//					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
//					return;
//				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					newServerParams = null;
				}
				else {
					clusterProvider.addNewServerInList(newServerParams);
					addServerItemInServersTree(newServerParams);
				}
			}
		});
		
		MenuItem menuItemDeleteServer = new MenuItem(menu, SWT.NONE);
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
		
		
		
		initIcon();
		
		clusterProvider.getServers().forEach((server, config) -> {
			addServerItemInServersTree(config);
		});
		
		
		return container;
	}

	private void addServerItemInServersTree(Server config) {
		TreeItem item = new TreeItem(serversTree, SWT.NONE);
		item.setText(new String[] { config.getServerPresent()});//, config.getRemoteRasPortAsString() });
		item.setData("ServerName", config.getServerPresent()); // del
//		item.setData("RASPort", config.remoteRasPort); // del
		item.setData("ServerConfig", config);
		item.setImage(serverIcon);
		item.setChecked(false);
	}
	
	protected void addItems() {


	}

	protected void fillServersList() {
		// TODO Auto-generated method stub
		
	}

	private void initIcon() {
		serverIcon = getImage(mainForm.getDisplay(), "/server_24.png");
		serverIconUp = getImage(mainForm.getDisplay(), "/server_up_24.png");
		serverIconDown = getImage(mainForm.getDisplay(), "/server_down_24.png");
//		serverIcon = getImage(mainForm.getDisplay(), "/icons/server_24.png");
//		serverIconUp = getImage(mainForm.getDisplay(), "/icons/server_up_24.png");
//		serverIconDown = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");
	}
	
	private Image getImage(Device device, String name) {
		return new Image(device, this.getClass().getResourceAsStream(name));
	}

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
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 400);
	}
}
