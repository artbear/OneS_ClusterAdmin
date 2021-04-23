package ru.yanygin.clusterAdminLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//import ru.yanygin.clusterAdminLibrary.Config.Server;

public class Config {
	@SerializedName("servers")
	@Expose
	public Map<String, Server> servers = new HashMap<>();

	public Server CreateNewServer() {
		return new Server("newServerAddress:1541");
	}
	
	public List<String> addNewServers(List<String> servers) {

		List<String> addedServers = new ArrayList<>();

		// Имя сервера, которое приходит сюда не равно Представлению сервера, выаводимому в списке
		// Имя сервера. оно же Key в map и json, строка вида Server:1541, с обязательным указанием порта менеджера, к которому подключаемся
		// если порт менеджера не задан - ставим стандартный 1541
		// переделать
		for (String serverName : servers) {
			if (!this.servers.containsKey(serverName)) {
				Server serverConfig = new Server(serverName);
				this.servers.put(serverName, serverConfig);

				addedServers.add(serverName);
			}
		}

		return addedServers;
	}
	
	public void connectAllServers() {
		servers.forEach((server, config) -> {
			config.connect(false);
		});
	}
	
	public void checkConnectionAllServers() {
		servers.forEach((server, config) -> {
			config.connect(true);
		});
	}

	/**
	 * @author yanyg
	 *
	 */
	public class Server {
		
		@SerializedName("serverHost") // может быть переименовать в serverHostName
		@Expose
		public String serverHost;
		
		@SerializedName("managerPort")
		@Expose
		public int managerPort;
		
		@SerializedName("remoteRasPort")
		@Expose
		public int remoteRasPort;
		
		@SerializedName("useLocalRas")
		@Expose
		public boolean useLocalRas;
		
		@SerializedName("localRasPort")
		@Expose
		public int localRasPort;
		
		@SerializedName("localRasV8version")
		@Expose
		public String localRasV8version;
		
		@SerializedName("autoconnect")
		@Expose
		public boolean autoconnect;
		
		public boolean available;
		
		public String connectionError;	
		
		public ClusterConnector clusterConnector;
		
		public UUID clusterID; // private

		public Server(String serverName) {
			this.serverHost = calcHostName(serverName);
			this.managerPort = calcManagerPort(serverName);
			this.remoteRasPort = calcRemoteRASPort(serverName);
			this.useLocalRas = false;
			this.localRasPort = 0;
			this.localRasV8version = "";
			this.autoconnect = false;
			this.available = false;
			
			AgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
			this.clusterConnector = new ClusterConnector(factory);
			
//			List<IClusterInfo> clusterInfoList = this.clusterConnector.getClusterInfoList();
//			this.clusterID = clusterInfoList.get(0).getClusterId();

		}

		public String getServerKey() {
			return serverHost.concat(":").concat(Integer.toString(managerPort));
		}

		public String getServerPresent() {
			String rasPort = "";
			if (useLocalRas) {
				rasPort = "(*".concat(Integer.toString(remoteRasPort)).concat(")");
			}
			else {
				rasPort = Integer.toString(remoteRasPort);
			}
			
			return serverHost.concat(":")
					.concat(Integer.toString(managerPort))
					.concat("/")
					.concat(rasPort);
		}

		public String getManagerPortAsString() {
			return Integer.toString(managerPort);
		}

		public String getRemoteRasPortAsString() {
			return Integer.toString(remoteRasPort);
		}

		public String getLocalRasPortAsString() {
			return Integer.toString(localRasPort);
		}
		
		public void setNewServerProperties(String serverHost,
											int managerPort,
											int remoteRasPort,
											boolean useLocalRas,
											int localRasPort,
											String localRasV8version,
											boolean autoconnect) {
			this.serverHost = serverHost;
			this.managerPort = managerPort;
			this.remoteRasPort = remoteRasPort;
			this.useLocalRas = useLocalRas;
			this.localRasPort = localRasPort;
			this.localRasV8version = localRasV8version;
			this.autoconnect = autoconnect;
			
			if (this.autoconnect) {
				connect(false);
			}
		}
		
		
		/** Вычисляет имя хоста, на котором запущены процессы кластера
		 * @param serverAddress - Имя инф.базы из списка баз. Может содержать номер порта.
		 *  Примеры: Desktop, Desktop:2541
		 * @return Имя хоста, на котором запущены процессы кластера
		 */
		private String calcHostName(String serverAddress) {
			String serverName;
			String[] ar = serverAddress.split(":");
			if (ar.length > 0) {
				serverName = ar[0];
			} else {
				serverName = "localhost";
			}
			
			return serverName;
		}
		
		private int calcManagerPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1541;
			} else {
				port = Integer.parseInt(ar[1]);
			}
			return port;
		}
		
		private int calcRemoteRASPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1545;
			} else {
				port = Integer.parseInt(ar[1].substring(0, ar[1].length()-1).concat("5"));
			}
			return port;
		}

		public boolean connect(boolean disconnectAfter) {
			
			if (clusterConnector.isConnected())
				return true;
			
//			String serverAddress = serverAddress;
			int rasPort = useLocalRas ? localRasPort : remoteRasPort;

			try {
				clusterConnector.connect(serverHost, rasPort, 20);
				available = true;
				System.out.println("Server ".concat(getServerPresent()).concat(" is connected now"));
				
				if (disconnectAfter) {
					clusterConnector.disconnect();	
				}
				//auth
				List<IClusterInfo> clusterInfoList = clusterConnector.getClusterInfoList();
//				IClusterInfo cluster = clusterInfoList.get(0);
				clusterID = clusterInfoList.get(0).getClusterId();
				clusterConnector.authenticateCluster(clusterID, "", "");
				
			}
			catch (Exception e) {
				available = false;
				
				System.out.println("Server ".concat(getServerPresent()).concat(" connect error"));
				return false;
			}
			return true;

		}

		public boolean disconnect() {
			
			if (!clusterConnector.isConnected()) {
				System.out.println("Server ".concat(getServerPresent()).concat(" is not connected"));
				return true;
			}
			
			try {
				clusterConnector.disconnect();	
				System.out.println("Server ".concat(getServerPresent()).concat(" disconnected now"));
			}
			catch (Exception excp) {
				System.out.println("Server ".concat(getServerPresent()).concat(" disconnect error").concat(excp.getMessage()));
				return false;
			}
			return true;

		}

	    public List<IInfoBaseInfo> getInfoBases()
	    {
	    	// сделать кеширование списка инфобаз
	    	return clusterConnector.getInfoBases(clusterID);
	        
	    }

	    public String getInfoBaseName(UUID infobaseID)
	    {
	    	List<IInfoBaseInfo> infobases = this.getInfoBases();
	    	
			String infobaseName = "";
			
			for (IInfoBaseInfo infobase : infobases) {
				if (infobase.getInfoBaseId().equals(infobaseID)){
					infobaseName = infobase.getName();
					break;
				}
			}
			return infobaseName;
			
	    }
	    
	    public List<ISessionInfo> getInfoBaseSessions(IInfoBaseInfoShort ibs)
	    {
	    	List<ISessionInfo> sessions = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return sessions;
			
//			UUID infobaseId = ibs.getInfoBaseId();
	        return clusterConnector.getInfoBaseSessions(clusterID, ibs.getInfoBaseId());
	        
	    }

	    public List<ISessionInfo> getSessions()
	    {
	    	List<ISessionInfo> sessions = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return sessions;
			
	        return clusterConnector.getSessions(clusterID);
	        
	    }

	    public List<IInfoBaseConnectionShort> getConnections()
	    {
	    	List<IInfoBaseConnectionShort> connections = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return connections;
			
	        return clusterConnector.getConnectionsShort(clusterID);
	        
	    }
		
		public void terminateSession(UUID sessionId) {
			
			clusterConnector.terminateSession(clusterID, sessionId);
			
		}
		
	}
}


