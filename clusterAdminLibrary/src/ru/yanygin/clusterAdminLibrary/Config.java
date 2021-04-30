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
	@SerializedName("Servers")
	@Expose
	public Map<String, Server> servers = new HashMap<>(); // Надо определиться что должно являться ключем, агент (Server:1540) или менеджер (Server:1541)

	public Server CreateNewServer() {
		return new Server("newServerAddress:1541");
	}
	
	public List<String> addNewServers(List<String> servers) {
		// Пакетное добавление серверов в список, предполагается для механизма импорта из списка информационных баз

		List<String> addedServers = new ArrayList<>();

		// Имя сервера, которое приходит сюда не равно Представлению сервера, выводимому в списке
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
		
		@SerializedName("ManagerHost")
		@Expose
		public String managerHost;
		
		@SerializedName("AgentPort")
		@Expose
		public int agentPort;
		
		@SerializedName("ManagerPort")
		@Expose
		public int managerPort;
		
		@SerializedName("RasHost")
		@Expose
		public String rasHost;

		@SerializedName("RasPort")
		@Expose
		public int rasPort;
		
		@SerializedName("UseLocalRas")
		@Expose
		public boolean useLocalRas;
		
		@SerializedName("LocalRasPort")
		@Expose
		public int localRasPort;
		
		@SerializedName("LocalRasV8version")
		@Expose
		public String localRasV8version;
		
		@SerializedName("AgentUser")
		@Expose
		public String agentUser;
		
		@SerializedName("AgentPassword")
		@Expose
		public String agentPasswors;
		
		@SerializedName("Autoconnect")
		@Expose
		public boolean autoconnect;
		
		public boolean available;
		
		public String connectionError;	
		
		public ClusterConnector clusterConnector;
		
//		public UUID clusterID; // deprecated
		public List<IClusterInfo> clusterInfoList;
		public Map<UUID, List<IInfoBaseInfoShort>> clustersInfoBasesShortCashe;
		
//		public Map<UUID, Pair<String, String>> credentialsClustersCashe;
//		public Map<UUID, Pair<String, String>> credentialsInfobasesCashe;
		public Map<UUID, String[]> credentialsClustersCashe;
		public Map<UUID, String[]> credentialsInfobasesCashe;

		public Server(String serverName) {
//			this.managerHost = calcHostName(serverName);
//			this.managerPort = calcManagerPort(serverName);
//			this.rasPort = calcRemoteRASPort(serverName);
			calcServerParams(serverName);
			
			this.useLocalRas = false;
			this.localRasPort = 0;
			this.localRasV8version = "";
			this.autoconnect = false;
			this.available = false;
			this.agentUser = "";
			this.agentPasswors = "";
			
			init();

		}

		public void init() {
			AgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
			this.clusterConnector = new ClusterConnector(factory);
			this.clustersInfoBasesShortCashe = new HashMap<>();
		}
		
		// Надо определиться что должно являться ключем, агент (Server:1540) или менеджер (Server:1541)
		public String getServerKey() {
			return managerHost.concat(":").concat(Integer.toString(agentPort));
		}

		public String getServerDescription() {
			String rasPort = "";
			if (useLocalRas) {
				rasPort = "(*".concat(Integer.toString(localRasPort)).concat(")");
			}
			else {
				rasPort = Integer.toString(this.rasPort);
			}
			
			return managerHost.concat(":")
					.concat(Integer.toString(agentPort))
					.concat("-")
					.concat(rasPort);
		}

		public String getManagerPortAsString() {
			return Integer.toString(managerPort);
		}

		public String getAgentPortAsString() {
			return Integer.toString(agentPort);
		}
		public String getRasPortAsString() {
			return Integer.toString(rasPort);
		}

		public String getLocalRasPortAsString() {
			return Integer.toString(localRasPort);
		}
		
		public void setNewServerProperties(String managerHost,
											int managerPort,
											int agentPort,
											String rasHost,
											int rasPort,
											boolean useLocalRas,
											int localRasPort,
											String localRasV8version,
											boolean autoconnect,
											String agentUser,
											String agentPasswors) {
			
			this.managerHost = managerHost;
			this.managerPort = managerPort;
			this.agentPort = agentPort;
			this.rasHost	= rasHost;
			this.rasPort 	= rasPort;
			this.useLocalRas = useLocalRas;
			this.localRasPort = localRasPort;
			this.localRasV8version = localRasV8version;
			this.autoconnect = autoconnect;
			this.agentUser = agentUser;
			this.agentPasswors = agentPasswors;
			
			if (this.autoconnect) {
				connect(false);
			}
		}
		
		
		/** Вычисляет имя хоста, на котором запущены процессы кластера
		 * @param serverAddress - Имя инф.базы из списка баз. Может содержать номер порта менеджера кластера (по-умолчанию 1541).
		 *  Примеры: Server1c, Server1c:2541
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
		
		private void calcServerParams(String serverAddress) {
			
			String managerHost;
			String rasHost;
			int managerPort;
			int agentPort;
			int rasPort;
			
			serverAddress = serverAddress.strip();
			if (serverAddress.isBlank())
				serverAddress = "localhost";
			
			String[] ar = serverAddress.split(":");
			managerHost	= ar[0];
			rasHost		= ar[0];
			
			if (ar.length == 1) {
				managerPort = 1541;
				agentPort 	= 1540;
				rasPort 	= 1545;
			} else {
				managerPort = Integer.parseInt(ar[1]);
				agentPort = managerPort - 1;
				rasPort = managerPort + 4;
			}
			
			this.managerHost 	= managerHost;
			this.rasHost 		= rasHost;
			this.managerPort 	= managerPort;
			this.agentPort 		= agentPort;
			this.rasPort 		= rasPort;
			
		}
		
		public boolean connect(boolean disconnectAfter) {
			
			if (clusterConnector.isConnected())
				return true;
			
			String rasHost 	= useLocalRas ? "localhost" : this.rasHost;
			int rasPort 	= useLocalRas ? localRasPort : this.rasPort;

			try {
				clusterConnector.connect(rasHost, rasPort, 20);
				available = true;
				System.out.println("Server ".concat(getServerDescription()).concat(" is connected now"));
				
				if (disconnectAfter) {
					clusterConnector.disconnect();	
				}
				//auth agent
				try {
					clusterConnector.authenticateAgent("", "");
				} catch (Exception e) {
					clusterConnector.authenticateAgent(agentUser, agentPasswors);
				}
				//auth clusters
				clusterInfoList = clusterConnector.getClusterInfoList();
				clusterInfoList.forEach(clusterInfo -> {
					try {
						clusterConnector.authenticateCluster(clusterInfo.getClusterId(), "", "");
					} catch (Exception e) {
						clusterConnector.authenticateCluster(clusterInfo.getClusterId(), agentUser, agentPasswors);
					}

				});
				
			}
			catch (Exception excp) {
				available = false;
				
				System.out.println("Server ".concat(getServerDescription()).concat(" connect error"));
				System.out.println(excp.getLocalizedMessage());
				return false;
			}
			return true;

		}

		public boolean disconnect() {
			
			if (!clusterConnector.isConnected()) {
				System.out.println("Server ".concat(getServerDescription()).concat(" is not connected"));
				return true;
			}
			
			try {
				clusterConnector.disconnect();	
				System.out.println("Server ".concat(getServerDescription()).concat(" disconnected now"));
			}
			catch (Exception excp) {
				System.out.println("Server ".concat(getServerDescription()).concat(" disconnect error").concat(excp.getMessage()));
				return false;
			}
			return true;

		}

	    public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterID)
	    {
			try {
				clusterConnector.authenticateCluster(clusterID, "", "");
			} catch (Exception e) {
				clusterConnector.authenticateCluster(clusterID, agentUser, agentPasswors);
			}
			
	    	List<IInfoBaseInfoShort> clusterInfoBases = clusterConnector.getInfoBasesShort(clusterID);
	    	
	    	// кеширование списка инфобаз
	    	clustersInfoBasesShortCashe.put(clusterID, clusterInfoBases);
	    	return clusterInfoBases;
	        
	    }

	    public String getInfoBaseName(UUID clusterID, UUID infobaseID)
	    {

			String infobaseName = "";
	    	
			// Сперва достаем из кеша
	    	List<IInfoBaseInfoShort> clusterInfoBases = clustersInfoBasesShortCashe.get(clusterID);
			for (IInfoBaseInfoShort infobase : clusterInfoBases) {
				if (infobase.getInfoBaseId().equals(infobaseID)){
					infobaseName = infobase.getName();
					break;
				}
			}
			// В кеше не нашли, обновляем кеш списка инфобаз и снова ищем
			if (infobaseName.isBlank()) {
		    	clusterInfoBases = clusterConnector.getInfoBasesShort(clusterID);
		    	clustersInfoBasesShortCashe.put(clusterID, clusterInfoBases);
				for (IInfoBaseInfoShort infobase : clusterInfoBases) {
					if (infobase.getInfoBaseId().equals(infobaseID)){
						infobaseName = infobase.getName();
						break;
					}
				}
			}
			
			return infobaseName;
			
	    }
	    
	    public List<ISessionInfo> getInfoBaseSessions(UUID clusterID, UUID infobaseId)
	    {
	    	List<ISessionInfo> sessions = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return sessions;
			
//			UUID infobaseId = ibs.getInfoBaseId();
	        return clusterConnector.getInfoBaseSessions(clusterID, infobaseId);
	        
	    }

	    public List<ISessionInfo> getSessions(UUID clusterID)
	    {
	    	List<ISessionInfo> sessions = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return sessions;
			
	        return clusterConnector.getSessions(clusterID);
	        
	    }

	    public List<IInfoBaseConnectionShort> getConnections(UUID clusterID)
	    {
	    	List<IInfoBaseConnectionShort> connections = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return connections;
			
	        return clusterConnector.getConnectionsShort(clusterID);
	        
	    }

	    public List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(UUID clusterID, UUID infobaseId)
	    {
	    	List<IInfoBaseConnectionShort> connections = new ArrayList<>();
			if (!clusterConnector.isConnected())
				return connections;
			
	        return clusterConnector.getInfoBaseConnectionsShort(clusterID, infobaseId);
	        
	    }
		
		public void terminateSession(UUID clusterID, UUID sessionId) {
			
			clusterConnector.terminateSession(clusterID, sessionId);
			
		}
		
	}
}


