package br.net.fabiozumbi12.UltimateChat.Sponge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.net.fabiozumbi12.UltimateChat.Sponge.UCLogger.timingType;

/**Represents a chat channel use by UltimateChat to control from where/to send/receive messages.
 * 
 * @author FabioZumbi12
 *
 */
public class UCChannel {
	private List<String> ignoring = new ArrayList<String>();
	private List<String> mutes = new ArrayList<String>();
	private List<CommandSource> members = new ArrayList<CommandSource>();	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	private void addDefaults() {
		properties.put("name", "");
		properties.put("alias", "");
		properties.put("color", "&b");
		properties.put("across-worlds", true);
		properties.put("distance", 0);
		properties.put("use-this-builder", false);
		properties.put("tag-builder", "world,ch-tags,prefix,nickname,suffix,message");
		properties.put("need-focus", false);
		properties.put("canLock", true);
		properties.put("receivers-message", true);
		properties.put("cost", 0.0);
		properties.put("bungee", false);
		properties.put("channelAlias.enable", false);
		properties.put("channelAlias.sendAs", "player");
		properties.put("channelAlias.cmd", "");
		properties.put("available-worlds", new ArrayList<String>());
		properties.put("discord.channelID", "");
		properties.put("discord.mode", "none");
		properties.put("discord.hover", "&3Discord Channel: &a{dd-channel}\n&3Role Name: {dd-rolecolor}{dd-rolename}");
		properties.put("discord.allow-server-cmds", false);
		properties.put("discord.format-to-mc", "{ch-color}[{ch-alias}]&b{dd-rolecolor}[{dd-rolename}]{sender}&r: ");
		properties.put("discord.format-to-dd", ":regional_indicator_g: **{sender}**: {message}");
	}
	
	@Deprecated()
	public UCChannel(String name, String alias, boolean worlds, int dist, String color, String builder, boolean focus, boolean receiversMsg, double cost, boolean isbungee, boolean ownBuilder, boolean isAlias, String aliasSender, String aliasCmd, List<String> availableWorlds, String ddchannel, String ddmode, String ddmcformat, String mcddformat, String ddhover, boolean ddallowcmds, boolean lock) {
		addDefaults();
		properties.put("name", name);
		properties.put("alias", alias);
		properties.put("color", color);
		properties.put("across-worlds", worlds);
		properties.put("distance", dist);
		properties.put("use-this-builder", ownBuilder);
		properties.put("tag-builder", builder);
		properties.put("need-focus", focus);
		properties.put("canLock", lock);
		properties.put("receivers-message", receiversMsg);
		properties.put("cost", cost);
		properties.put("bungee", isbungee);
		properties.put("channelAlias.enable", isAlias);
		properties.put("channelAlias.sendAs", aliasSender);
		properties.put("channelAlias.cmd", aliasCmd);
		properties.put("available-worlds", availableWorlds);
		properties.put("discord.channelID", ddchannel);
		properties.put("discord.mode", ddmode);
		properties.put("discord.hover", ddhover);
		properties.put("discord.allow-server-cmds", ddallowcmds);
		properties.put("discord.format-to-mc", ddmcformat);
		properties.put("discord.format-to-dd", mcddformat);
	}
	
	public UCChannel(String name, String alias, String color) {
		addDefaults();
		properties.put("name", name);
		properties.put("alias", alias);
		properties.put("color", color);
	}
	
	public UCChannel(String name) {
		addDefaults();
		properties.put("name", name);
		properties.put("alias", name.substring(0, 1).toLowerCase());
	}
	
	public UCChannel(Map<String, Object> props) {
		addDefaults();
		properties.keySet().stream().filter((key)->props.containsKey(key)).forEach((nkey)->{
			properties.put(nkey, props.get(nkey));
		});
	}

	public Map<String, Object> getProperties(){
		return properties;
	}
	
	public void setProperty(String key, Object value){
		properties.put(key, value);
	}
	
	public boolean getDiscordAllowCmds(){		
		return (boolean) properties.get("discord.allow-server-cmds");
	}
	
	public boolean isTell(){
		return properties.get("name").toString().equals("tell");		
	}
	
	public String getDiscordChannelID(){
		return properties.get("discord.channelID").toString();
	}
	
	public String getDiscordMode(){
		return properties.get("discord.mode").toString();
	}
	
	public boolean matchDiscordID(String id){
		return getDiscordChannelID().equals(id);
	}
	
	public boolean isSendingDiscord(){
		return !getDiscordChannelID().isEmpty() && (getDiscordMode().equalsIgnoreCase("both") || getDiscordMode().equalsIgnoreCase("send"));
	}
	
	public boolean isListenDiscord(){
		return !getDiscordChannelID().isEmpty() && (getDiscordMode().equalsIgnoreCase("both") || getDiscordMode().equalsIgnoreCase("listen"));
	}
	
	public String getDiscordHover(){
		return properties.get("discord.hover").toString();
	}
	
	public String getDiscordtoMCFormat(){
		return properties.get("discord.format-to-mc").toString();
	}
	
	public String getMCtoDiscordFormat(){
		return properties.get("discord.format-to-dd").toString();
	}
	
	public List<CommandSource> getMembers(){
		return this.members;
	}
	
	public void clearMembers(){
		this.members.clear();
	}
	
	public boolean addMember(CommandSource p){
		for (UCChannel ch:UChat.get().getConfig().getChannels()){
			ch.removeMember(p);
		}
		return this.members.add(p);
	}
	
	public boolean removeMember(CommandSource p){
		return this.members.remove(p);
	}
	
	public boolean isMember(CommandSource p){
		return this.members.contains(p);
	}
		
	public boolean canLock(){
		return (boolean) properties.get("canLock");
	}
	
	@SuppressWarnings("unchecked")
	public boolean availableInWorld(World w){
		return ((List<String>)properties.get("available-worlds")).contains(w.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> availableWorlds(){
		return ((List<String>)properties.get("available-worlds"));
	}
	
	public String getAliasCmd(){
		return properties.get("channelAlias.cmd").toString();
	}
	
	public String getAliasSender(){		
		return properties.get("channelAlias.sendAs").toString();
	}
	
	public boolean isCmdAlias(){
		return (boolean) properties.get("channelAlias.enable");
	}
	
	public boolean useOwnBuilder(){
		return (boolean) properties.get("use-this-builder");
	}
	
	public double getCost(){
		return (double) properties.get("cost");
	}
	
	public void setCost(double cost){
		properties.put("cost", cost);
	}
	
	public void setReceiversMsg(boolean show){
		properties.put("cost", show);
	}
	
	public boolean getReceiversMsg(){
		return (boolean) properties.get("receivers-message");
	}
	
	public void muteThis(String player){
		if (!this.mutes.contains(player)){
			this.mutes.add(player);
		}		
	}
	
	public void unMuteThis(String player){
		if (this.mutes.contains(player)){
			this.mutes.remove(player);
		}		
	}
	
	public boolean isMuted(String player){
		return this.mutes.contains(player);
	}
	
	public void ignoreThis(String player){
		if (!this.ignoring.contains(player)){
			this.ignoring.add(player);
		}		
	}
	
	public void unIgnoreThis(String player){
		if (this.ignoring.contains(player)){
			this.ignoring.remove(player);
		}		
	}
	
	public boolean isIgnoring(String player){
		return this.ignoring.contains(player);
	}
	
	public String[] getBuilder(){
		return properties.get("tag-builder").toString().split(",");
	}
	
	public String getRawBuilder(){
		return properties.get("tag-builder").toString();
	}
	
	public boolean crossWorlds(){
		return (boolean) properties.get("across-worlds");
	}
	
	public int getDistance(){
		return (int) properties.get("distance");
	}
	
	public String getColor(){
		return properties.get("color").toString();
	}
	
	public String getName(){
		return properties.get("name").toString();
	}
	
	public String getAlias(){
		return properties.get("alias").toString();
	}

	public boolean neeFocus() {
		return (boolean) properties.get("need-focus");
	}
	
	public boolean matchChannel(String aliasOrName){
		return properties.get("alias").toString().equalsIgnoreCase(aliasOrName) || properties.get("name").toString().equalsIgnoreCase(aliasOrName);
	}

	public boolean isBungee() {		
		return (boolean) properties.get("bungee");
	}
	/** Send a message from a channel as player.<p>
	 * <i>Use {@code sendMessage(Player, Text)} as replecement for this method</i>
	 * @param src {@code Player}
	 * @param message {@code Text}
	 */
	@Deprecated
	public void sendMessage(Player src, String message){
		sendMessage(src, message);
	}
	
	/** Send a message from a channel as player.
	 * @param src {@code Player}
	 * @param message {@code Text} - Message to send.
	 * @param direct {@code boolean} - Send message direct to players on channel.
	 */
	public void sendMessage(Player src, Text message, boolean direct){	
		if (direct){
			for (Player p:Sponge.getServer().getOnlinePlayers()){
				UCChannel chp = UChat.get().getConfig().getPlayerChannel(p);
				if (UChat.get().getPerms().channelReadPerm(p, this) && !this.isIgnoring(p.getName()) && (this.neeFocus() && chp.equals(this) || !this.neeFocus())){
					UChat.get().getLogger().timings(timingType.START, "UCChannel#sendMessage()|Direct Message");
					p.sendMessage(message);					
				}
			}
			src.sendMessage(message);
		} else {
			MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(
					UChat.get().getVHelper().getCause(src), 
					src.getMessageChannel(), 
					Optional.of(src.getMessageChannel()), 				    							
					new MessageEvent.MessageFormatter(Text.builder("<" + src.getName() + "> ")
							.onShiftClick(TextActions.insertText(src.getName()))
							.onClick(TextActions.suggestCommand("/msg " + src.getName()))
							.build(), message),
							message,  
					false);
			UChat.get().getLogger().timings(timingType.START, "UCChannel#sendMessage()|Fire MessageChannelEvent");
			if (!Sponge.getEventManager().post(event)){
				UChat.tempChannels.put(src.getName(), this.getAlias());
			}
		}
	}
	
	/** Send a message from a channel as console.
	 * @param sender {@code ConsoleSource} - Console sender.
	 * @param message {@code Text} - Message to send.
	 * @param direct {@code boolean} - Send message direct to players on channel.
	 */
	public void sendMessage(ConsoleSource sender, Text message, boolean direct){
		if (direct){			
			for (Player p:Sponge.getServer().getOnlinePlayers()){
				UCChannel chp = UChat.get().getConfig().getPlayerChannel(p);
				if (UChat.get().getPerms().channelReadPerm(p, this) && !this.isIgnoring(p.getName()) && (this.neeFocus() && chp.equals(this) || !this.neeFocus())){
					UChat.get().getLogger().timings(timingType.START, "UCChannel#sendMessage()|Direct Message");
					p.sendMessage(message);					
				}
			}
			sender.sendMessage(message);
		} else {
			UChat.get().getLogger().timings(timingType.START, "UCChannel#sendMessage()|Fire MessageChannelEvent");
			UCMessages.sendFancyMessage(new String[0], message, this, sender, null);
		}
	}
	
	/** Send a message from a channel as console.<p>
	 * <i>Use {@code sendMessage(ConsoleSource, message, direct)} as replecement for this method</i>
	 * @param src {@code ConsoleSource} - Console sender.
	 * @param message {@code Text} - message to send.
	 */
	@Deprecated	
	public void sendMessage(ConsoleSource sender, String message){
		sendMessage(sender, Text.of(message), UChat.get().getConfig().getBool("api","format-console-messages"));		
	}
	
	@Override
	public String toString(){
		JsonArray array = new JsonArray();
		for (Entry<String, Object> prop:properties.entrySet()){
			JsonObject json = new JsonObject();
			json.addProperty(prop.getKey(),prop.getValue().toString());
			array.add(json);
		}
		return array.toString();		
	}
}
