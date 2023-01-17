/*
 * Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 * Copyright (C) 2021  cyoung06
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.mod.cosmetics;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import kr.syeyoung.dungeonsguide.mod.DungeonsGuide;
import kr.syeyoung.dungeonsguide.mod.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.List;

// Reimplement this using ASM. Removing reference to this is too painful.
public class CustomNetworkPlayerInfo extends NetworkPlayerInfo {
    public CustomNetworkPlayerInfo(GameProfile gameProfile) {
        super(gameProfile);
    }

    public CustomNetworkPlayerInfo(S38PacketPlayerListItem.AddPlayerData playerData) {
        super(playerData);
        setDisplayName(super.getDisplayName());
    }


    private IChatComponent displayName;
    private String unformattedDisplayText;
    private String playerNameWithoutColor;

    public IChatComponent getOriginalDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(IChatComponent displayNameIn) {
        displayName = displayNameIn;
        if (displayName == null) {
            unformattedDisplayText = null;
            return;
        }

        unformattedDisplayText = displayName.getUnformattedTextForChat();


        playerNameWithoutColor = "";
        for (String s : unformattedDisplayText.split(" ")) {
            String strippped = TextUtils.stripColor(s);
            if (strippped.startsWith("[")) {
                continue;
            }
            playerNameWithoutColor = strippped;
            break;
        }
    }


    public IChatComponent getDisplayName()
    {

        String rawPlayerString;
        String actualName;
        List<ActiveCosmetic> activeCosmetics;

        // in case that the set player name is not called we do this
        if (playerNameWithoutColor != null) {
            activeCosmetics = DungeonsGuide.getDungeonsGuide().getCosmeticsManager().getActiveCosmeticByPlayerNameLowerCase().get(playerNameWithoutColor.toLowerCase());
            rawPlayerString = unformattedDisplayText;
            actualName = this.playerNameWithoutColor;
        } else {
            rawPlayerString = ScorePlayerTeam.formatPlayerName(super.getPlayerTeam(), super.getGameProfile().getName());
            actualName = "";
            for (String s : rawPlayerString.split(" ")) {
                String strippped = TextUtils.stripColor(s);
                if (strippped.startsWith("[")) continue;
                actualName = strippped;
                break;
            }
            activeCosmetics = DungeonsGuide.getDungeonsGuide().getCosmeticsManager().getActiveCosmeticByPlayerNameLowerCase().get(actualName.toLowerCase());
        }


        if (activeCosmetics == null) return displayName;
        CosmeticData color=null;
        for (ActiveCosmetic activeCosmetic : activeCosmetics) {
            CosmeticData cosmeticData = DungeonsGuide.getDungeonsGuide().getCosmeticsManager().getCosmeticDataMap().get(activeCosmetic.getCosmeticData());
            if (cosmeticData.getCosmeticType().equals("color")) color = cosmeticData;
        }

        if (color != null) {
            String coloredName = color.getData() + actualName;
            return new ChatComponentText(rawPlayerString.replace(actualName, coloredName));
        } else {
            return new ChatComponentText(rawPlayerString);
        }
    }
}