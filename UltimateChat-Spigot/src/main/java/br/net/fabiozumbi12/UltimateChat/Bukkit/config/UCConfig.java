package br.net.fabiozumbi12.UltimateChat.Bukkit.config;

import br.net.fabiozumbi12.UltimateChat.Bukkit.UCChannel;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UCUtil;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UChat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class UCConfig {

    private static UCCommentedConfig comConfig;
	private static YamlConfiguration Prots = new YamlConfiguration();
	private UChat plugin;
		
	public UCConfig(UChat plugin) throws IOException, InvalidConfigurationException {
	    this.plugin = plugin;
        File main = UChat.get().getDataFolder();
        File protections = new File(UChat.get().getDataFolder(),"protections.yml");

        if (!main.exists()) {
            main.mkdir();
            plugin.getUCLogger().info("Created folder: " +UChat.get().getDataFolder());
        }

        if (!protections.exists()) {
            UCUtil.saveResource("/assets/ultimatechat/protections.yml", new File(UChat.get().getDataFolder(),"protections.yml"));
            plugin.getUCLogger().info("Created protections file: " + protections.getPath());
        }

        //------------------------------ Add default Values ----------------------------//

        comConfig = new UCCommentedConfig();
        comConfig.addDefaults();

        /*------------------------------------------------------------------------------------*/

        //load protections file
        Prots = updateFile(protections, "assets/ultimatechat/protections.yml");

        /* Load Channels */
        loadChannels();

        /*------------------------------------------------------------------------------------*/

        //----------------------------------------------------------------------------------------//
        save();
        plugin.getUCLogger().info("All configurations loaded!");
	}
    
	/* Channels */
	private void loadChannels() throws IOException{
		File chfolder = new File(UChat.get().getDataFolder(),"channels");
		
		if (!chfolder.exists()) {
        	chfolder.mkdir();
        	UChat.get().getUCLogger().info("Created folder: " +chfolder.getPath());
        } 
		
		if (UChat.get().getChannels() == null){
			UChat.get().setChannels(new HashMap<List<String>,UCChannel>());
		}
		
        File[] listOfFiles = chfolder.listFiles();
        
        YamlConfiguration channel = new YamlConfiguration();
        
        if (listOfFiles.length == 0){
        	//create default channels
        	File g = new File(chfolder, "global.yml"); 
        	channel = YamlConfiguration.loadConfiguration(g);
        	channel.set("name", "Global");
        	channel.set("alias", "g");
        	channel.set("color", "&2");
        	channel.set("jedis", true);
        	channel.save(g);
        	
        	File l = new File(chfolder, "local.yml");
        	channel = YamlConfiguration.loadConfiguration(l);
        	channel.set("name", "Local");
        	channel.set("alias", "l");
        	channel.set("color", "&e");
        	channel.set("across-worlds", false);
        	channel.set("distance", 40);
        	channel.save(l);
        	
        	File ad = new File(chfolder, "admin.yml");
        	channel = YamlConfiguration.loadConfiguration(ad);
        	channel.set("name", "Admin");
        	channel.set("alias", "ad");
        	channel.set("color", "&b");
        	channel.set("jedis", true);
        	channel.save(ad);
        	
        	listOfFiles = chfolder.listFiles();
        }
        
		for (File file:listOfFiles){
			if (file.getName().endsWith(".yml")){
				channel = YamlConfiguration.loadConfiguration(file);            				
				UCChannel ch = new UCChannel(channel.getValues(true));
				
				try {
					addChannel(ch);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void delChannel(UCChannel ch) {
		for (Entry<List<String>, UCChannel> ch0:UChat.get().getChannels().entrySet()){
			if (ch0.getValue().equals(ch)){
				UChat.get().getChannels().remove(ch0.getKey());
				break;
			}
		}
		File defch = new File(UChat.get().getDataFolder(),"channels"+File.separator+ch.getName().toLowerCase()+".yml");	
		if (defch.exists()){
			defch.delete();
		}
	}
	
	public void addChannel(UCChannel ch) throws IOException{	
		File defch = new File(UChat.get().getDataFolder(),"channels"+File.separator+ch.getName().toLowerCase()+".yml");		
		YamlConfiguration chFile = YamlConfiguration.loadConfiguration(defch);
		chFile.options().header(""
				+ "###################################################\n"
				+ "############## Channel Configuration ##############\n"
				+ "###################################################\n"
				+ "\n"
				+ "This is the channel configuration.\n"
				+ "You can change and copy this file to create as many channels you want.\n"
				+ "This is the default options:\n"
				+ "\n"
				+ "name: Global - The name of channel.\n"
				+ "alias: g - The alias to use the channel\n"
				+ "across-worlds: true - Send messages of this channel to all worlds?\n"
				+ "distance: 0 - If across worlds is false, distance to receive this messages.\n"
				+ "color: &b - The color of channel\n"
				+ "tag-builder: ch-tags,world,clan-tag,marry-tag,group-prefix,nickname,group-suffix,message - Tags of this channel\n"
				+ "need-focus: false - Player can use the alias or need to use '/ch g' to use this channel?\n"
				+ "canLock: true - Change if the player can use /<channel> to lock on channel."
				+ "receivers-message: true - Send chat messages like if 'no players near to receive the message'?\n"
				+ "cost: 0.0 - Cost to player use this channel.\n"
				+ "use-this-builder: false - Use this tag builder or use the 'config.yml' tag-builder?\n"
				+ "channelAlias - Use this channel as a command alias.\n"
				+ "  enable: true - Enable this execute a command alias?\n"
				+ "  sendAs: player - Send the command alias as 'player' or 'console'?\n"
				+ "  cmd: '' - Command to send on every message send by this channel.\n"
				+ "available-worlds - Worlds and only this world where this chat can be used and messages sent/received.\n"
				+ "discord:\n"
				+ "  mode: NONE - The options are NONE, SEND, LISTEN, BOTH. If enabled and token code set and the channel ID matches with one discord channel, will react according the choosen mode.\n"
				+ "  hover: &3Discord Channel: &a{dd-channel}\\n&3Role Name: {dd-rolecolor}{dd-rolename}\n"
				+ "  format-to-mc: {ch-color}[{ch-alias}]&b{dd-rolecolor}[{dd-rolename}]{sender}&r: \n"
				+ "  format-to-dd: :thought_balloon: **{sender}**: {message} \n"
				+ "  allow-server-cmds: false - Use this channel to send commands from discord > minecraft.\n"
				+ "  channelID: '' - The ID of your Discord Channel. Enable debug on your discord to get the channel ID.\n");
		
		ch.getProperties().entrySet().forEach((map)->{
			chFile.set((String) map.getKey(), map.getValue());			
		});
		chFile.save(defch);		
		
		if (UChat.get().getChannel(ch.getName()) != null){
			ch.setMembers(UChat.get().getChannel(ch.getName()).getMembers());
			UChat.get().getChannels().remove(Arrays.asList(ch.getName().toLowerCase(), ch.getAlias().toLowerCase()));
		}
		UChat.get().getChannels().put(Arrays.asList(ch.getName().toLowerCase(), ch.getAlias().toLowerCase()), ch);
	}
	
	public List<String> getTagList(){
		List<String> tags = new ArrayList<String>();
		for (String key:plugin.getConfig().getKeys(true)){
			if (key.startsWith("tags.") && key.split("\\.").length == 2){
				tags.add(key.replace("tags.", ""));
			}			
		}
		for (String str:getStringList("general.custom-tags")){
			tags.add(str);
		}
		return tags;
	}
		
	public String[] getDefBuilder(){
		return getString("general.default-tag-builder").replace(" ", "").split(",");
	}
		
	public List<String> getBroadcastAliases() {
		return Arrays.asList(plugin.getConfig().getString("broadcast.aliases").replace(" ", "").split(","));
	}
	
	public List<String> getTellAliases() {
		return Arrays.asList(plugin.getConfig().getString("tell.cmd-aliases").replace(" ", "").split(","));
	}
	
	public List<String> getMsgAliases() {
		return Arrays.asList(plugin.getConfig().getString("general.umsg-cmd-aliases").replace(" ", "").split(","));
	}
	
    public boolean getBoolean(String key){		
		return plugin.getConfig().getBoolean(key, false);
	}
    
    public void setConfig(String key, Object value){
    	plugin.getConfig().set(key, value);
    }
    
    public String getString(String key){		
		return plugin.getConfig().getString(key);
	}
    
    public int getInt(String key){		
		return plugin.getConfig().getInt(key);
	}
    
    public List<String> getStringList(String key){		
		return plugin.getConfig().getStringList(key);
	}
    
    public Material getMaterial(String key){
    	return Material.getMaterial(plugin.getConfig().getString(key));
    }
    
    public void save(){
    	try {
            comConfig.saveConfig();
			Prots.save(new File(UChat.get().getDataFolder(),"protections.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //protection methods
    public ConfigurationSection getProtReplecements() {
		return Prots.getConfigurationSection("chat-protection.censor.replace-words");
	}
    
	public int getProtInt(String key){
		return Prots.getInt(key);
	}
	
	public boolean getProtBool(String key){
		return Prots.getBoolean(key);
	}
	
	public List<String> getProtStringList(String key){
		return Prots.getStringList(key);
	}
	
    public boolean containsProtKey(String key){		
		return Prots.contains(key);
	}
    
	public String getProtString(String key){
		return Prots.getString(key);
	}
	
	public String getProtMsg(String key){
		return ChatColor.translateAlternateColorCodes('&',Prots.getString(key));
	}
	
	public String getColorStr(String key){
		return ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString(key));
	}
	
	private static YamlConfiguration updateFile(File saved, String resource){
		YamlConfiguration finalyml = new YamlConfiguration();   
		YamlConfiguration tempProts = new YamlConfiguration();
        try {
        	finalyml.load(saved);
        		      
        	tempProts.load(new InputStreamReader(UChat.get().getResource(resource), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
        
        for (String key:tempProts.getKeys(true)){    
        	Object obj = tempProts.get(key);
        	if (finalyml.get(key) != null){
        		obj = finalyml.get(key);
        	}
        	finalyml.set(key, obj);    	            	   	            	
        }  
        return finalyml;
	}

	public String getURLTemplate() {
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("general.URL-template"));
	}
}
   