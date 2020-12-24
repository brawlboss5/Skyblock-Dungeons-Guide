package kr.syeyoung.dungeonsguide;

import kr.syeyoung.dungeonsguide.commands.*;
import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoomInfoRegistry;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class e implements c {

    private SkyblockStatus skyblockStatus;

    private static e dungeonsGuide;


    @Getter
    private b authenticator;

    public e(b authenticator) {
        this.authenticator = authenticator;
    }

    public static void sendDebugChat(IChatComponent iChatComponent) {
        if (Config.DEBUG)
            Minecraft.getMinecraft().thePlayer.addChatMessage(iChatComponent);
    }

    public void init(FMLInitializationEvent event)
    {
        dungeonsGuide = this;
        skyblockStatus = new SkyblockStatus();

        MinecraftForge.EVENT_BUS.register(new EventListener());
        CommandDungeonsGuide commandDungeonsGuide;
        ClientCommandHandler.instance.registerCommand(commandDungeonsGuide = new CommandDungeonsGuide());
        MinecraftForge.EVENT_BUS.register(commandDungeonsGuide);

        try {
            DungeonRoomInfoRegistry.loadAll(configDir);
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

        Keybinds.register();
    }
    public void pre(FMLPreInitializationEvent event) {
        configDir = new File(event.getModConfigurationDirectory(),"dungeonsguide");
        File configFile = new File(configDir, "config.conf");
        if (!configFile.exists()) {
            configDir.mkdirs();
        }
        Config.configuration = new Configuration(configFile);
        Config.syncConfig( true );
    }
    private void copy(InputStream inputStream, File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        IOUtils.copy(inputStream, fos);
        fos.flush();
        fos.close();
        inputStream.close();
    }

    private void combineConfig(Configuration saved, Configuration newest) {
    }

    @Getter
    private File configDir;


    public SkyblockStatus getSkyblockStatus() {
        return (SkyblockStatus) skyblockStatus;
    }

    public static e getDungeonsGuide() {
        return dungeonsGuide;
    }
}