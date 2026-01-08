package ovh.mythmc.bancobankextension.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import me.dablakbandit.bank.player.handler.BankItemsHandler;
import me.dablakbandit.bank.player.info.BankItemsInfo;
import me.dablakbandit.bank.player.info.item.BankItem;
import me.dablakbandit.core.players.CorePlayerManager;
import me.dablakbandit.core.players.CorePlayers;
import ovh.mythmc.banco.api.storage.BancoContainer;

public final class BankContainer extends BancoContainer {

    @Override
    protected ItemStack addItem(UUID uuid, ItemStack itemStack) {
        final CorePlayers player = CorePlayerManager.getInstance().getPlayer(uuid);
        if (player == null) return itemStack;

        final BankItemsInfo bankItemsInfo = player.getInfo(BankItemsInfo.class);
        final BankItemsHandler bankItemsHandler = bankItemsInfo.getBankItemsHandler();

        return bankItemsHandler.addBankItem(player.getPlayer(), itemStack, false);
    }

    @Override
    protected Collection<ItemStack> get(UUID uuid) {
        List<ItemStack> items = new ArrayList<>();

        CorePlayers player = CorePlayerManager.getInstance().getPlayer(uuid);
        if (player == null)
            return List.of();

        BankItemsInfo bankItemsInfo = player.getInfo(BankItemsInfo.class);
        if (bankItemsInfo == null)
            return List.of();
        int maxTabs = bankItemsInfo.getMaxTabNotEmpty();
        for (int i = 0; i <= maxTabs; i++) {
            bankItemsInfo.getTabBankItemsMap(i).values().stream()
                .filter(Objects::nonNull)
                .forEach(bankItem -> {
                    ItemStack itemStack = bankItem.getItemStack();
                    itemStack.setAmount(bankItem.getAmount());
                    items.add(itemStack);
                });
        }

        return items;
    }

    @Override
    protected ItemStack removeItem(UUID uuid, ItemStack itemStack) {
        final CorePlayers player = CorePlayerManager.getInstance().getPlayer(uuid);
        if (player == null) return itemStack;

        final BankItemsInfo bankItemsInfo = player.getInfo(BankItemsInfo.class);

        boolean removed = false;
        for (int i = 0; i <= bankItemsInfo.getTotalTabCount(); i++) {
            final Map<Integer, BankItem> tabBankItemMap = bankItemsInfo.getTabBankItemsMap(i);
            Iterator<BankItem> iterator = tabBankItemMap.values().iterator();
            while (iterator.hasNext()) {
                final BankItem bankItem = iterator.next();

                if (removed) break;

                if (bankItem.getItemStack().equals(itemStack)) {
                    iterator.remove();
                    removed = true;
                }
            }
        }

        if (removed)
            return null;

        return itemStack;
    }
    
}
