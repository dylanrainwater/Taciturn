package net.chemicalstudios.spam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandHandler;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.gui.GuiCheckbox;
import com.mumfrey.liteloader.gui.GuiHoverLabel;
import com.mumfrey.liteloader.util.ModUtilities;

public class LiteModSpam implements ChatFilter, Tickable {

	String prevMessage;

	CommandHandler handler;

	private boolean sending = false;

	
	GuiHoverLabel test;

	
	
	String[] namesArray;
	
	
	KeyBinding mKey = new KeyBinding("key.guispam.toggle", Keyboard.KEY_M, "key.categories.litemods");
	
	@Override
	public String getName() {
		return "Spam Protection Mod";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public void init(File configPath) {
		
		LiteLoader.getInput().registerKeyBinding(mKey);
		
		prevMessage = new String();
		
		// Create folder
		String pathToFolder = "../mod_data_ChemicalStudios";
		
		File folder = new File(pathToFolder);
		folder.mkdirs();
		// Create text file
		String pathToTxt = "/muteList.txt";
		File muteTxt = new File(pathToFolder + pathToTxt);
		try {
			muteTxt.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
	}

	@Override
	public boolean onChat(S02PacketChat chatPacket, IChatComponent chat,
			String message) {
			
		/* This is here to block repeating messages */
		if (prevMessage.equals(message)) {
			return false;
		}
		
		/* This is here to block users on the blocklist */
		String preUsername = new String();
		
		String[] words =  message.split(" ");
		preUsername = words[0];
		String username = new String();
		for(int i = 0; i < preUsername.length(); i++) {
			if(!(preUsername.charAt(i) == '§' || ((i > 0) && preUsername.charAt(i-1) == '§')) && !((preUsername.charAt(i) == '<') || preUsername.charAt(i) == '>')&& !((preUsername.charAt(i) == '[') || preUsername.charAt(i) == ']')) {
				username += preUsername.charAt(i);
			}
		}
		
		username = username.toLowerCase();
		
		if(namesArray != null) {
			for(int i = 0; i < namesArray.length; i++) {
				if(namesArray[i] != null && username.equals(namesArray[i].toLowerCase())) {
					return false;
				}
			}
		} else {
			return true;
		}
		prevMessage = message;
		return true;
	}

	public void sendMessageToPlayer(String message) {
		sending = true;
	}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame,
			boolean clock) {
		if(mKey.isPressed()) {
			minecraft.displayGuiScreen(new GuiSpam(this));
		}
	}
	
	public void sendNames(String names) {
		if(names != null) {
			namesArray = names.split(" ");
		}
	}
}
