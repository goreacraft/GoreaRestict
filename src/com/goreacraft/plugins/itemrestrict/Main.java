package com.goreacraft.plugins.itemrestrict;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	
	public final Logger logger = Logger.getLogger("minecraft");
	
	public YamlConfiguration Data=new YamlConfiguration();
	public File banlistfile;
	public static Main plugin;
	//public static Set<String> placelist;
	//public static Set<String> itemuse;
	private List<String> aliases;
	List<String> arguments;
	HashMap<String, Object> AAA = new HashMap<String, Object>();
	
	public void onEnable()
	
    {
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled! " + pdfFile.getWebsite());
		getConfig().options().copyDefaults(true);
      	getConfig().options().header("If you need help with this plugin you can contact goreacraft on teamspeak ip: goreacraft.com\n Website http://www.goreacraft.com");
      	saveConfig();
      	//Bukkit.getServer().getPluginManager().registerEvents(this, this);
      	Bukkit.getServer().getPluginManager().registerEvents(this, this);
      //	Bukkit.getServer().getPluginManager().registerEvents(new ChunksListener(), this);
      	banlistfile = new File(getDataFolder(), "BanList.yml");
      	//spawnPointsFile = new File(getDataFolder(), "SpawnPoints.yml");
      	loadconfigs();
      	arguments = Arrays.asList("Min", "Max", "W","M","Item", "Name");
      	aliases = Bukkit.getPluginCommand("gorearestrict").getAliases();
      	//FirstRange = new HashMap<String,Integer>();
      //	placelist = Data.getConfigurationSection("Place").getKeys(false);
      	
      	/*for (String iii: Data.getConfigurationSection("Restrict.FirstRange").getKeys(false))
		{
			int ccc = Data.getInt("Restrict.FirstRange." + iii);
			FirstRange.put(iii, ccc);
		}*/
      //====================================== METRICS STUFF =====================================================
      	 try {
      		    Metrics metrics = new Metrics(this);
      		    metrics.start();
      		} catch (IOException e) {
      		    // Failed to submit the stats :-(
      		}
      	 
      	if(getConfig().getBoolean("ChechUpdates"))
   		new Updater(79646);
       
      	
    }
	void loadconfigs(){
		if(!banlistfile.exists())
	    {
	    	try {
	    		banlistfile.createNewFile();
	    		
            } catch (IOException e) {
                e.printStackTrace();
            }
	    }
	    	Data = YamlConfiguration.loadConfiguration(banlistfile);
	    	if(!Data.isConfigurationSection("Items"))
	    	{
	    		Data.createSection("Items");
	    	
	    	} 
	    	//itemuse = Data.getConfigurationSection("Items").getKeys(false);
	    	
	    
		
		
	}
	public void onDisable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been disabled!" + pdfFile.getWebsite());
    }
	
	
	static Player findPlayerByString(String name) 
	{
		for ( Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(player.getName().equals(name)) 
			{
				return player;
			}
		}
		
		return null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
		if (aliases.contains(label))
		{
			if(args.length==1)
			{
				if (args[0].equals("debug"))
				{
					File debugfile = new File(getDataFolder(), "Debug.yml");
					Map<String,Set<String>> iii = new HashMap<String,Set<String>>();
					iii.put("Itemuse",Data.getConfigurationSection("Items").getKeys(false));
					Data.createSection("Debug", iii);
					try {
						Data.save(debugfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				if (args[0].equals("help") || args[0].equals("?"))
				{
					if ( sender instanceof Player)
						{
						Player player =  ((Player) sender).getPlayer();
						
						showplayerhelp(player);
						
						
						} else { 
							//notplayer
							
						}
				}
				if (args[0].equals("reload"))
				{
					if ( sender instanceof Player)
						{
						Player player =  ((Player) sender).getPlayer();
						if(player.hasPermission("gr.reload") || player.isOp())
							{
							loadconfigs();
								player.sendMessage("GoreaRestrict reloaded configs!");
								
							} 
							else{
								player.sendMessage("You dont have the permission 'gr.reload'");
								return false;
								}
						
						} else {
							System.out.println("GoreaRestrict reloaded configs!");
						}
					loadconfigs();
					plugin.reloadConfig();
					//Data=(YamlConfiguration) getConfig();
					//itemuse = Data.getConfigurationSection("Items").getKeys(false);
					return true;
				}
				
				
				if (args[0].equals("ban"))
				{
					
					if ( sender instanceof Player)
					{
						Player player =  ((Player) sender).getPlayer();
						
						
						
						if(!player.hasPermission("gr.ban") || !player.isOp())
						{
						player.sendMessage("You dont have the permission to ban items: 'gr.ban'");
						return false;
						}					
						
					
						if(player.getItemInHand() != null && player.getItemInHand().getTypeId()!=0)
						{
							//player.sendMessage(" " + player.getItemInHand().getTypeId());
							String itemid = "" + player.getItemInHand().getTypeId();
							 String meta = "" + player.getItemInHand().getDurability();
							 String item = (itemid+":" + meta);
							 
							 
								Data.createSection("Items."+ itemid + ":" + meta);
								player.sendMessage("Item [" + ChatColor.RED + item +ChatColor.RESET + "] has been banned in all dimensions.");
								//itemuse = Data.getConfigurationSection("Items").getKeys(false);
								savetofile();
								return true;
						} else {player.sendMessage(ChatColor.RED+"You need to hold the item in your hand or type the 'id:meta'"); return false;}
					//add the banned items
					
					
					} else {
						System.out.println("Type the 'itemid:meta'/'item:*' id as well.");
						return false;
					}
					
					
					
				}
				
			}
			
			
			if(args.length>1)
			{
				if (args[0].equals("ban"))
				{
					if ( sender instanceof Player)
					{
						Player player =  ((Player) sender).getPlayer();
						
						
						
						
						if (args[1].equals("help") || args[0].equals("?"))
						{						
							showplayerbanhelp(player);
								
						}
						if (args[1].equals("list"))
						{
							player.sendMessage(ChatColor.GREEN + "Items restricted with plugin "+ ChatColor.RESET + "[" + ChatColor.GOLD + "GoreaRestrict"+ ChatColor.RESET + "]");
							for ( String item : Data.getConfigurationSection("Items").getKeys(false))
							{
								player.sendMessage(item);
								
							}
							
						}
						
						
						if(!player.hasPermission("gr.ban") || !player.isOp())
							{
							player.sendMessage("You dont have the permission to ban items: 'gr.ban'");
							return false;
							}
						
						if(args.length==2)
						if (args[1].equals("remove"))
						{
							if(player.getItemInHand() != null && player.getItemInHand().getTypeId()!=0)
							{
								String itemid = "" + player.getItemInHand().getTypeId();
								 String meta = "" + player.getItemInHand().getDurability();
								 String item = (itemid+":" + meta);
								
							
								if(Data.getConfigurationSection("Items").getKeys(false).contains(item))
								{
									Data.set("Items." + item, null);
									savetofile();
									player.sendMessage(ChatColor.YELLOW + "Item: " + item + " removed from the ban list.");
									
									return true;
									
								} else {player.sendMessage(ChatColor.RED + "There is no item: " + item + " in the ban list.");}
									
									
									
									
							} else {player.sendMessage(ChatColor.RED + "You need to hold the item in your hand");}
						}
						if(args.length==3)
							if (args[1].equals("remove"))
							{
								String[] itemsplit = args[2].split(":");
								if(itemsplit.length==2)
									if(isInteger(itemsplit[0]) && isInteger(itemsplit[0]))
									{	
										// String item = (itemsplit[0]+":" + meta);
											
										
											if(Data.getConfigurationSection("Items").getKeys(false).contains(args[2]))
											{
												Data.set("Items." + args[2], null);
												player.sendMessage(ChatColor.YELLOW + "Item: " + args[2] + " removed from the ban list.");
												
												savetofile();
												return true;
												
											} else {player.sendMessage(ChatColor.RED + "There is no item: " + args[2] + " in the ban list.");}
												
												
												
												
									} else {player.sendMessage(ChatColor.RED + "Something wrong with your item argument. Use '/gr ban remove <Id:Meta>'");}
									
								
							}
						//HashMap<String, Object> arg = HashMap
						AAA = new HashMap<String, Object>();
						AAA = stringtoHashMap(args,player);
						/*for(String dd:AAA.keySet())
						{
						player.sendMessage(" "+ AAA.get(dd) );
						}*/
						if (!AAA.containsKey("Item"))
						{
							if(player.getItemInHand() != null)
							{
								String itemid = "" + player.getItemInHand().getTypeId();
								 String meta = "" + player.getItemInHand().getDurability();
								
								AAA.put("Item",itemid + ":" + meta);
								AAA.put("Id", itemid);	
								AAA.put("Meta", meta);
							} else {player.sendMessage(ChatColor.RED+"You need to hold the item in your hand or type the item 'id:meta'"); return false;}
							
						}
						
						if ((!AAA.containsKey("W") && AAA.containsKey("Max")))
						{
							AAA.put("W", "All");
							player.sendMessage("No 'World' argument found but 'Max' argument detected. Setting as global ban");
						}
						if ((!AAA.containsKey("W") && AAA.containsKey("Min")))
						{
							AAA.put("W", "All");
							player.sendMessage("No world name found but 'Min' argument detected. Setting as global ban");
						}

						
						
							
							HashMap<String,Integer> limits =new HashMap<String,Integer>();
							if(Data.isConfigurationSection("Items." + AAA.get("Item") + ".Dimensions"))
							{

								// = new Map<String,Integer> ();
								if(Data.getConfigurationSection("Items." + AAA.get("Item") + ".Dimensions").getKeys(false).contains("All"))
								{
									Data.set("Items." + AAA.get("Item") + ".Dimensions.All",null);

								}
							}	
							
							if ((AAA.containsKey("Min")))							
								if(isInteger(AAA.get("Min").toString())){
								limits.put("Min", Integer.parseInt(AAA.get("Min").toString()));
								} else {
									player.sendMessage("Wrong Min value");
									//Min = 0
									return false;
								}
							if ((AAA.containsKey("Max")))							
								if(isInteger(AAA.get("Max").toString()))
								{
								limits.put("Max", Integer.parseInt(AAA.get("Max").toString()));
								}
								else {
									player.sendMessage("Wrong Max value");
									return false;
									//Max =0
								}
							if ((!AAA.containsKey("W")))
							{
								AAA.put("W", "All");
								Data.set("Items." + AAA.get("Item") + ".Dimensions", null);
								Data.createSection("Items." + AAA.get("Item") + ".Dimensions");
								//Data.createSection("Items." + AAA.get("Item") + ".Dimensions." + AAA.get("W").toString());
											
							} else{
								if(AAA.get("W").equals("All"))
									Data.set("Items." + AAA.get("Item") + ".Dimensions", null);
								Data.createSection("Items." + AAA.get("Item") + ".Dimensions");
							}
							if (AAA.containsKey("Name"))
							{
								Data.createSection("Items." + AAA.get("Item") + ".Name");
								String name = AAA.get("Name").toString();
								Data.set("Items." + AAA.get("Item") + ".Name", name);
							} else {
								Data.createSection("Items." + AAA.get("Item") + ".Name");
								int id = Integer.parseInt(AAA.get("Id").toString());
								short meta;
								if(AAA.get("Meta").equals("*")){
									meta = 0;								 
								} else {
									meta = Short.parseShort(AAA.get("Meta").toString());
									}
								String name =(new ItemStack(id,
										0,
										meta).getType()
								
								.toString()) 
								+ ":" 
								+ AAA.get("Meta");
								Data.set("Items." + AAA.get("Item") + ".Name", name);
							}
							Data.createSection("Items." + AAA.get("Item") + ".Dimensions." + AAA.get("W").toString());
								Data.set("Items." + AAA.get("Item") + ".Dimensions." + AAA.get("W"), limits);
						
						
						//player.sendMessage("======" + AAA.get("W").toString());
						if(AAA.containsKey("M"))
							{
							Data.createSection("Items." + AAA.get("Item") + ".Message");
							String mesaj = AAA.get("M").toString();
							Data.set("Items." + AAA.get("Item") + ".Message", mesaj);
							}
						
						
						//itemuse = Data.getConfigurationSection("Items").getKeys(false);
						savetofile();
						player.sendMessage(ChatColor.GREEN + "Item added to banned list");
						return true;
							//not player
					}
					else{	
							System.out.println("Type the 'itemid:meta'/'item:*' id as well.");
							return false;
						}
					}
			}

			
		}
		
		if(label.equalsIgnoreCase("banneditems") && sender instanceof Player)
		{
			Player player = (Player) sender;
			
			player.sendMessage(ChatColor.GREEN + "Items restricted with plugin "+ ChatColor.RESET + "[" + ChatColor.GOLD + "GoreaRestrict"+ ChatColor.RESET + "]");
			for ( String item : Data.getConfigurationSection("Items").getKeys(false))
			{
				
				String output = ChatColor.RED + item ;
				if( Data.getConfigurationSection("Items." +  item).getKeys(false).contains("Name"))
				{
					output= ChatColor.RED + Data.getString("Items." + item + ".Name");
				}
				
				if( Data.getConfigurationSection("Items." +  item).getKeys(false).contains("Message"))
				{
					//player.sendMessage("Concatam: " + Data.getString("Items." + item + ".Message"));
					output+=(ChatColor.YELLOW +" --> " + ChatColor.AQUA +"" + ChatColor.ITALIC + Data.getString("Items." + item + ".Message"));
				}
				
				player.sendMessage(output);	
			}
			if(getConfig().getKeys(false).contains("Extra info for banneditems") && getConfig().getStringList("Extra info for banneditems")!=null)
			{
				for(String info: this.getConfig().getStringList("Extra info for banneditems"))
				{
					player.sendMessage("" + ChatColor.AQUA + info);
				}
			}
			
		}
		return false;
		
    }
	
	
	private void showplayerhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
     	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
     	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
     	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
     	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
     	
     	player.sendMessage("");
     	player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );
     	player.sendMessage( ChatColor.YELLOW + "/gr ?/help :" + ChatColor.RESET + " Shows this.");
     	player.sendMessage( ChatColor.YELLOW + "/gr reload :" + ChatColor.RESET + " Reloads Configs.");
     	player.sendMessage( ChatColor.YELLOW + "/gr ban list:" + ChatColor.RESET + " Shows all restricted items( will be more details soon)" );
     	//player.sendMessage( ChatColor.YELLOW + "/gr ban help:" + ChatColor.RESET + " Shows advanced help information for how to ban items" );
     	player.sendMessage( ChatColor.YELLOW + "/gr ban" + ChatColor.RESET + " Bans the item in your hand globally" );
     	player.sendMessage( ChatColor.YELLOW + "/gr ban remove <id:meta>" + ChatColor.RESET + " Removes from the ban list the item in your hand or the given item if provided." );
     	
     	player.sendMessage( ChatColor.RED + "/gr ban help" + ChatColor.RESET + " To see a more advanced help for banning items");
		
	}
	private void showplayerbanhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );
		player.sendMessage( ChatColor.YELLOW + "/gr center" + ChatColor.RESET + " Will set the center of the world you are in at your location (work in progress)");
		player.sendMessage( ChatColor.RED + "/gr ban" + ChatColor.RESET + " Bans the item in your hand globally (all dimensions).");
		player.sendMessage( ChatColor.GREEN + "Available arguments:" + ChatColor.RESET + " Add the arguments for more advanced options.");
		player.sendMessage( ChatColor.RED + "ItemID:ItemDamageValue" + ChatColor.RESET + " To ban that specific item and not the one in yor hand");
		player.sendMessage("Use "+ ChatColor.RED +"*"+ ChatColor.RESET +" as ItemDamageValue to ban all item variations.");
		player.sendMessage( ChatColor.RED + "W:<dimension_name>" + ChatColor.RESET + " To ban the item in that specific dimension");
		player.sendMessage( ChatColor.RED + "Min:<range>" + ChatColor.RESET + " Players needs to be at that minimum range from the word center_point to be able to use it");
		player.sendMessage( ChatColor.RED + "Max:<range>" + ChatColor.RESET + " Players needs to be at that maximum range from the word center_point to be able to use it");
		player.sendMessage( ChatColor.RED + "Name:<Name_you_want>" + ChatColor.RESET + " Sets the name to display in /banneditems list for this item");
		player.sendMessage( ChatColor.ITALIC + "Example 1: " + ChatColor.GREEN + "/gr ban 391:* W:world This will make you sick if you eat it.");
		player.sendMessage( ChatColor.ITALIC + "Example 2: " + ChatColor.GREEN + "/gr ban 394:0 W:world_nether Min:100 Only awesome players can eat this here. Try after 100 blocks from spawn.");
		player.sendMessage( ChatColor.ITALIC + "Example 3: " + ChatColor.GREEN + "/gr ban 511:0 W:DIM7 Min:200 Max:300 Quarries can be used only between coordinates 200 and 300 in Twilight");
		player.sendMessage( ChatColor.ITALIC + "Example 4: " + ChatColor.GREEN + "/gr ban 25256:3 Min:500 Max:1000 This wand can be used in all worlds but between coordinates 500 and 1000");
	}
	private HashMap<String,Object> stringtoHashMap(String[] args,Player player) 
	{
		
		HashMap<String,Object> map = new HashMap<String,Object>();	
		
		String mes = "";
		int i=1;
		
		
		while(i<args.length)
		{
			//boolean metaisint = false;
			//boolean idisint = false;
			
			String[] c = args[i].split(":");
			
			if(c.length>1)
			{
				//player.sendMessage("Checking " + args[i] );
				
				if(isInteger(c[0]))
				{
					String id = c[0];
					
					//String meta = "*";
					if(c.length>1)
					{
						if(isInteger(c[1]))
							{				
							//player.sendMessage("Adding item: "+ id + ":" + c[1]);
							map.put("Item", (id + ":" + c[1]).toString());
							map.put("Id", id);	
							map.put("Meta", c[1]);
							} 
						else
							if(c[1].equals("*")){
								String sss= (id.toString() + ":" + (c[1]).toString());
								map.put("Item", sss);
								map.put("Id", id);	
								map.put("Meta", c[1]);
								//player.sendMessage("Adding item: "+ sss);
							}
							else {
						//	player.sendMessage("Concat not ints or * in: " + args[i]);
							mes = mes.concat(args[i]+" ");
						}
					
					
					} else {
						//player.sendMessage("Is whole numer " + args[i]);
						mes = mes.concat(args[i]+" ");
					}
					
					
				} 
				else 
				{	
					boolean concat = true;	
					for (String d:arguments)
					{
						if(c[0].equalsIgnoreCase(d))
						{
							//player.sendMessage("Adding data: " + d + "=" + c[1]); 
							//if(arguments.contains(c[0]))
							map.put(d, (c[1]).toString());
							concat=false;
						}
					}
					if(concat) {
						//player.sendMessage("Concat not ints " + args[i]);
						mes = mes.concat(args[i]+" ");
					}
				}		
			} else {
				//player.sendMessage("Concat " + args[i]);
				mes = mes.concat(args[i]+" ");
			}
			i++;
		}
		
		if(!mes.isEmpty())
		{
			map.put("M", mes.substring(0, mes.length()-1));
		}

		return map;
		
	}
	private void savetofile(){
		try {
			Data.save(banlistfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	   public void onItemPlace(BlockPlaceEvent e)
	   {
		Player player = e.getPlayer();
		
			 Location loc = e.getPlayer().getLocation();
			 String item = Integer.toString(e.getItemInHand().getTypeId());
			 String meta = Integer.toString(e.getItemInHand().getDurability());	
			 if(!check(item,meta , player, loc))
			 	{
					e.setCancelled(true);	
					return;
				}
		
		
	   }
	@EventHandler(priority=EventPriority.HIGHEST)
	   public void onItemUse(PlayerInteractEvent e)
	   {
		Player player = e.getPlayer();
		
		
		if(e.getItem() != null )
		{
			 Location loc = e.getPlayer().getLocation();
			 String item = Integer.toString(e.getItem().getTypeId());
			 String meta = Integer.toString(e.getItem().getDurability());	
			 if(!check(item,meta , player, loc))
			 	{
					e.setCancelled(true);	
					return;
				}
		}
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
		 {
			 Location loc = e.getClickedBlock().getLocation();
			 String item =  Integer.toString(e.getClickedBlock().getTypeId());
			 String meta = Integer.toString(e.getClickedBlock().getData());	
			 if(!player.hasPermission("gi.bypass." + item + "."+meta))
				 if(!check(item,meta , player, loc))
				 	{
						e.setCancelled(true);
						//return;
					}
		 }
		
		
		/*if(Data.getKeys(false).contains("Border") || !player.hasPermission("gr.bypass.border"))
		{
		Location loc = e.getPlayer().getLocation();
		if(!border(player, loc))
			{
			e.setCancelled(true);								
			return;
			}							
		}*/
				
	   }
	
private boolean check(String itemid, String meta, Player player, Location loc)
{
	if(!player.hasPermission("gr.buypass." + itemid + "." + meta) || !player.hasPermission("gr.buypass." + itemid + ".*")) 
	{		
		if (Data.getConfigurationSection("Items").getKeys(false).contains(itemid + ":*") ) 
		{
			meta = "*";
		}		
		//findPlayerByString("gorea01").sendMessage("0");
		if (Data.getConfigurationSection("Items").getKeys(false).contains(itemid + ":" + meta) ) 
			{//findPlayerByString("gorea01").sendMessage("0.1");
				if(Data.isConfigurationSection("Items." + itemid + ":" + meta +".Dimensions"))
					{//findPlayerByString("gorea01").sendMessage("0.2");
					String world;
					if(Data.getConfigurationSection("Items." + itemid + ":" + meta + ".Dimensions").getKeys(false).contains("All"))
						{
							world = "All"; 
						} else { 
							world = loc.getWorld().getName();
								}		
					if(Data.getConfigurationSection("Items." + itemid + ":" + meta + ".Dimensions").getKeys(false).contains(world))
						{		//findPlayerByString("gorea01").sendMessage("0.3"+ Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+ world).getKeys(false).contains("Min") + " " + Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Max"));					
							if	(Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+ world).getKeys(false).contains("Min") || Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Max"))
								{//findPlayerByString("gorea01").sendMessage("0.4 " );
								//findPlayerByString("gorea01").sendMessage("true false" );
									if ( Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+ world).getKeys(false).contains("Min") && !Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Max"))
										{
											double min = Data.getDouble("Items." + itemid + ":" + meta+".Dimensions."+ world + ".Min");
											double x = Math.abs(loc.getX());
											double z = Math.abs(loc.getZ());
								
											if(x > min 	|| z > min)
												{
													return true;
												} else {
													if (Data.getConfigurationSection("Items." + itemid + ":" + meta).getKeys(false).contains("Message") )
													{
														player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD+ Data.getString("Items." + itemid + ":" + meta + ".Message"));
													}
													return false;
														}
								
										} 
									//findPlayerByString("gorea01").sendMessage("false true" );
									if ( !Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Min") && Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Max"))
										{//findPlayerByString("gorea01").sendMessage("2");
											double max = Data.getDouble("Items." + itemid + ":" + meta+".Dimensions."+world + ".Max");
											double x = Math.abs(loc.getX());
											double z = Math.abs(loc.getZ());								
											
											if(x < max && z < max)
												{
													return true;
												} else
													{
													if (Data.getConfigurationSection("Items." + itemid + ":" + meta).getKeys(false).contains("Message") )
													{
														player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD+ Data.getString("Items." + itemid + ":" + meta + ".Message"));
													}
													return false;
													}
								
								
										} 
									//findPlayerByString("gorea01").sendMessage("true true" );
									if ( Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Min") && Data.getConfigurationSection("Items." + itemid + ":" + meta+".Dimensions."+world).getKeys(false).contains("Max"))
										{//findPlayerByString("gorea01").sendMessage("3");
											double min = Data.getDouble("Items." + itemid + ":" + meta+".Dimensions."+world + ".Min");
											double max = Data.getDouble("Items." + itemid + ":" + meta+".Dimensions."+world + ".Max");
											double x = Math.abs(loc.getX());
											double z = Math.abs(loc.getZ());
							
											if((x > min	&& x < max &&  z < max) || (z > min && z < max &&  x < max))	
												{								
													return true;	
												} else
													{
													if (Data.getConfigurationSection("Items." + itemid + ":" + meta).getKeys(false).contains("Message") )
														{
															player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD+ Data.getString("Items." + itemid + ":" + meta + ".Message"));
														}
													return false;
													}
										}
									//findPlayerByString("gorea01").sendMessage("none found" );
								} else {
										if (Data.getConfigurationSection("Items." + itemid + ":" + meta).getKeys(false).contains("Message") )
											{
												player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD + Data.getString("Items." + itemid + ":" + meta + ".Message"));
											}							
										return false;
							
										}
						}
					} else {
						if (Data.getConfigurationSection("Items." + itemid + ":" + meta).getKeys(false).contains("Message") )
						{
							player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD + Data.getString("Items." + itemid + ":" + meta + ".Message"));
						}
						
					}
			return false;
			}
	}
	return true;
}
/*private boolean border(Player player, Location loc)
	{
		if(!player.isOp() || !player.hasPermission("gi.bypass.border"))
		{
			if(Data.isConfigurationSection("Border.Dimensions"))
				{
					if(Data.getConfigurationSection("Border.Dimensions").getKeys(false).contains(loc.getWorld().getName()))
					{
						if(Math.abs(loc.getX()) > Data.getInt("Border.Dimensions."+loc.getWorld().getName()) ||
								Math.abs(loc.getZ()) > Data.getInt("Border.Dimensions."+loc.getWorld().getName()))				
							{
							if (Data.getConfigurationSection("Border").getKeys(false).contains("Message") )
								player.sendMessage("["+ChatColor.RED + "GoreaRestrict" + ChatColor.RESET + "] "+ChatColor.GOLD + Data.getString("Border.Message"));
							return false;
							}
					}
				} 
		}
		return true;
	}*/


}
