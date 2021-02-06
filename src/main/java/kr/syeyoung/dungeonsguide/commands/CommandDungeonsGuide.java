package kr.syeyoung.dungeonsguide.commands;

import com.google.gson.JsonObject;
import kr.syeyoung.dungeonsguide.config.guiconfig.GuiConfig;
import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoomInfoRegistry;
import kr.syeyoung.dungeonsguide.e;
import kr.syeyoung.dungeonsguide.utils.AhUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandDungeonsGuide extends CommandBase {
    @Override
    public String getCommandName() {
        return "dg";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "dg";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            openConfig = true;
        } else if (args[0].equalsIgnoreCase("saverooms")) {
            DungeonRoomInfoRegistry.saveAll(e.getDungeonsGuide().getConfigDir());
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fSuccessfully saved user generated roomdata"));
        } else if (args[0].equalsIgnoreCase("loadrooms")) {
            try {
                DungeonRoomInfoRegistry.loadAll(e.getDungeonsGuide().getConfigDir());
                sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fSuccessfully loaded roomdatas"));
                return;
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §cAn error has occurred while loading roomdata"));
        } else if (args[0].equalsIgnoreCase("reloadah")) {
            try {
                AhUtils.loadAuctions();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fReloaded Ah data"));
        } else if (args[0].equalsIgnoreCase("brand")) {
            String serverBrand = Minecraft.getMinecraft().thePlayer.getClientBrand();
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e" + serverBrand));
        } else if (args[0].equalsIgnoreCase("reparty")) {
            e.getDungeonsGuide().getCommandReparty().requestReparty();
        } else if (args[0].equalsIgnoreCase("gui")) {
            openConfig = true;
        } else if (args[0].equalsIgnoreCase("info")) {
            JsonObject obj = e.getDungeonsGuide().getAuthenticator().a(e.getDungeonsGuide().getAuthenticator().c());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fCurrent Plan§7: §e" + obj.get("plan").getAsString()));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fBound to§7: §e" + obj.get("nickname").getAsString()));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fBound uuid§7: §e" + obj.get("uuid").getAsString()));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §fSession Expire§7: §e" + sdf.format(new Date(obj.get("exp").getAsLong() * 1000))));
        } else {
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg §7-§fOpens configuration gui"));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg gui §7-§fOpens configuration gui"));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg help §7-§fShows command help"));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg saverooms §7-§f Saves usergenerated dungeon roomdata."));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg loadrooms §7-§f Reloads dungeon roomdata."));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg reloadah §7-§f Reloads price data from server."));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg brand §7-§f View server brand."));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg reparty §7-§f Reparty."));
            sender.addChatMessage(new ChatComponentText("§eDungeons Guide §7:: §e/dg info §7-§f View Current DG User info."));
        }
    }

    private boolean openConfig = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        try {
            if (openConfig && e.phase == TickEvent.Phase.START ) {
                openConfig = false;
                Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
