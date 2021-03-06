package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Topitems {
	Topitems(String args[], Player player, CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		EconomyManager em = hc.getEconomyManager();
		try {
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.limit-info-commands-to-shops");
			if (args.length > 1) {
				sender.sendMessage(L.get("TOPITEMS_INVALID"));
				return;
			}
			Shop s = null;
			if (player != null) {
				if (em.inAnyShop(player)) {
					s = em.getShop(player);
				} 
				if (requireShop && em.getShop(player) == null && !player.hasPermission("hyperconomy.admin")) {
					sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
					return;
				}
			}
			int page;
			if (args.length == 0) {
				page = 1;
			} else {
				page = Integer.parseInt(args[0]);
			}
			SortedMap<Double, HyperObject> itemstocks = new TreeMap<Double, HyperObject>();
			ArrayList<HyperObject> objects = null;
			if (s != null) {
				objects = he.getHyperObjects(s);
			} else {
				objects = he.getHyperObjects();
			}
			for (HyperObject ho:objects) {
				boolean stocked = false;
				if (ho.getStock() > 0.0) {stocked = true;}
				boolean banned = false;
				boolean allowed = false;
				if (s != null) {
					banned = s.isBanned(ho);
					if (ho instanceof PlayerShopObject) {
						if (s instanceof PlayerShop) {
							PlayerShop ps = (PlayerShop)s;
							allowed = ps.isAllowed(em.getHyperPlayer(player));
						}
					}
					if ((!banned && stocked) || (allowed && stocked)) {
						double samount = ho.getStock();
						while (itemstocks.containsKey(samount)) {
							samount = samount + .0000001;
						}
						itemstocks.put(samount, ho);
					}
				} else {
					double samount = ho.getStock();
					if (samount > 0) {
						while (itemstocks.containsKey(samount)) {
							samount = samount + .0000001;
						}
						itemstocks.put(samount, ho);
					}
				}
			}
			int numberpage = page * 10;
			int count = 0;
			int le = itemstocks.size();
			double maxpages = le / 10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int) maxpages + 1;
			sender.sendMessage(L.f(L.get("PAGE_NUMBER"), page, maxpi));
			try {
				while (count < numberpage) {
					double lk = itemstocks.lastKey();
					if (count > ((page * 10) - 11)) {
						HyperObject ho = itemstocks.get(lk);
						if (ho instanceof PlayerShopObject) {
							PlayerShopObject pso = (PlayerShopObject)ho;
							sender.sendMessage(L.applyColor("&f"+pso.getDisplayName() + ": &a" + hc.gCF().twoDecimals(pso.getStock()) + " &f(&e" + pso.getStatus().toString() + "&f)" ));
						} else {
							sender.sendMessage(ChatColor.WHITE + ho.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.AQUA + "" + hc.gCF().twoDecimals(ho.getStock()));
						}
					}
					itemstocks.remove(lk);
					count++;
				}
			} catch (Exception e) {
				sender.sendMessage(L.get("YOU_HAVE_REACHED_THE_END"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOPITEMS_INVALID"));
		}
	}
}
