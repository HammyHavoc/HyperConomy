package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Ebuy {
	Ebuy(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		ETransaction ench =  hc.getETransaction();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					String name = args[0];
					String teststring = hc.testeString(name);
					if (teststring != null) {
						if (s.has(s.getShop(player), name)) {
							ench.buyEnchant(name, player);
						} else {
							player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
						}
					} else {
						player.sendMessage(ENCHANTMENT_NOT_IN_DATABASE);
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
			return;
		} catch (Exception e) {
			player.sendMessage(EBUY_INVALID);
		}
	}
}