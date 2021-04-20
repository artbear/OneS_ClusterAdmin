package ru.yanygin.clusterAdminLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.yanygin.clusterAdminLibrary.Config.Server;

public class ClusterProvider {
	File configFile;
	Config commonConfig;
	
	public ClusterProvider() {
		
		
	}

	public void readSavedKnownServers(String configPath) {
		
		if (configPath.isBlank())
		{
			commonConfig = new Config();
			return;
		}
		
		configFile = new File(configPath);
		if (!configFile.exists())
		{
			commonConfig = new Config();
			return;
		}
		
		JsonReader jsonReader;

		try {
			jsonReader = new JsonReader(
					new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation()
			    .create();
		
		commonConfig = gson.fromJson(jsonReader, Config.class);

		if (commonConfig == null) {
			commonConfig = new Config();
		}
		else {
			commonConfig.servers.forEach((server, config) -> {
				AgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
				config.clusterConnector = new ClusterConnector(factory);
				if (config.autoconnect) {
					config.connect(false);
				}
			});
		}
	}
	
	public void saveKnownServers() {//String configPath) {
		
//		configFile = new File(configPath);

		JsonWriter jsonWriter;
		try {
			jsonWriter = new JsonWriter(
					new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation()
			    .setPrettyPrinting() // не работает
			    .create();
		gson.toJson(commonConfig, commonConfig.getClass(), jsonWriter);
		
		try {
			jsonWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Server CreateNewServer() {
		return commonConfig.CreateNewServer();
	}
	
	public void addNewServerInList(Server server) {
		commonConfig.servers.put(server.getServerKey(), server);
		saveKnownServers();
	}
	
	public void removeServerInList(Server server) {
		commonConfig.servers.remove(server.getServerKey(), server);
		saveKnownServers();
	}
	
	public Map<String, Server> getServers() {
		return commonConfig.servers;
	}
	
	public List<String> findNewServers() {
				
		List<String> addedServers = new ArrayList<>();
		
		
		return addedServers;
	}

	public void connectToServers() {
		
		commonConfig.connectAllServers();
		
//		clusterConfig.serversMap.forEach((server, config) -> {
//			config.connect(false);
//		});
		
	}

	public List<String> getConnectedServers() {
		
		List<String> connectedServers = new ArrayList<>();
		
		commonConfig.servers.forEach((server, config) -> {
			if (config.clusterConnector.isConnected())
				connectedServers.add(config.getServerKey());
		});
		
		return connectedServers;
	}
	
	public void checkConnectToServers() {
		
		commonConfig.checkConnectionAllServers();
		
	}

	public List<IInfoBaseInfo> getInfobases(Server server){
		
		List<IInfoBaseInfo> infobases = new ArrayList<>();

		if (server.clusterConnector.isConnected()) {
			
			List<IClusterInfo> clusterInfoList = server.clusterConnector.getClusterInfoList();
			
			UUID uuid = clusterInfoList.get(0).getClusterId();
			
			infobases = server.clusterConnector.getInfoBases(uuid);
		
		}
		
		return infobases;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
