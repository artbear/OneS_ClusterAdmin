package ru.yanygin.clusterAdminApplication;

import org.eclipse.swt.widgets.Display;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;

public class ClusterAdmin {

	public static void main (String[] args) {
		
//		String configPath = "C:\\1C_EDT_WS\\test_clusterAdmin\\.metadata\\edtclusteradmin.config";
//		
//		System.out.println("Hello Cluster Admin");
//		System.out.println("Start Servers ping");
//		
//		ClusterProvider clusterProvider = new ClusterProvider();
//		clusterProvider.readSavedKnownServers(configPath);
//		clusterProvider.checkConnectToServers();
//		
//		System.out.println("End Servers ping");
		
		
		try {
			ClusterViewer window = new ClusterViewer();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
