package net.chemicalstudios.spam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;

public class GuiSpam extends GuiScreen {

	// All variables for file handling and what not
	private String fileName = "muteList.txt";
	private File muteList;
	private FileReader reader;
	private FileWriter writer;
	private String curLine = null;
	private int _nameCount = 0;
	private String names;
	private String[] mutedPlayers;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	private String _mutedPlayers;

	private LiteModSpam main;

	private GuiTextField _Player;

	public GuiSpam(LiteModSpam lite) {
		this.main = lite;
	}
	
	public void initGui() {
		this.buttonList.clear();
		byte var1 = -16;
		boolean var2 = true;
		
		// the center between the title and the "back to game" button
		int y = (100 + this.height / 2 + 24 + var1) / 2;
		
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height
				/ 2 + 24 + var1, I18n.format("Back to Game", new Object[0])));

		this.buttonList.add(new GuiButton(1, this.width / 2 - 200, y, 98, 20, I18n.format("Unmute", new Object[0])));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 100, y, 98, 20, I18n.format("Mute", new Object[0])));

		_Player = new GuiTextField(this.fontRendererObj, this.width / 2 - 78, y, 150, 20);
		_Player.setFocused(true);
		_Player.setText("");

		muteList = new File("../mod_data_ChemicalStudios/" + fileName);
		
		initList();
		
		_mutedPlayers = names;
	}

	protected void actionPerformed(GuiButton buttonClicked) {
		switch (buttonClicked.id) {
		case 0:
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
			break;

		case 1:
			unMutePlayer(_Player.getText());
			break;
		case 2:
			mutePlayer(_Player.getText());
		}
	}

	public void updateScreen() {
		super.updateScreen();
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		this._Player.mouseClicked(par1, par2, par3);
		this._Player.mouseClicked(par1, par2, par3);
	}

	protected void keyTyped(char par1, int par2) {
		this._Player.textboxKeyTyped(par1, par2);
	}

	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		this.drawString(this.fontRendererObj, "Mute Players Menu", this.width / 2 - (this.mc.fontRenderer.getStringWidth("Mute Players Menu") / 2), 100, 16777215);

		this._Player.drawTextBox();

		// This is here to keep mute list up to date
		updateList();
		
		super.drawScreen(par1, par2, par3);
	}
	public void initList() {
		try {
			reader = new FileReader(muteList);

			bufferedReader = new BufferedReader(reader);
			
	        while ((curLine = bufferedReader.readLine()) != null) {
				_nameCount++;
				
				if(!(curLine.equals(null)) && !(curLine.equals("null")) && !(curLine.equals("")) && !(curLine.equals(new String()))) {
					names += " " + curLine;
				}
			}
			
			bufferedReader.close();
			
			main.sendNames(names);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void updateList() {
		if(_mutedPlayers != null) {
			String[] _mutedPlayersArray = _mutedPlayers.split(" ");

			int count = 0;
			for(int i = 0; i < _mutedPlayersArray.length; i++) {
				if(!_mutedPlayersArray[i].equals(new String()) && !(_mutedPlayersArray[i].equals("null"))) {

					int x = 10;
					int y = 20 + (10 * count);
					
					if(y > (this.mc.displayHeight - 100)) {
						x *= count;
					}
					
					this.fontRendererObj.drawString("[" + _mutedPlayersArray[i] + "]", x, y, 0xFF1000);
					++count;
					this.fontRendererObj.drawString("Muted Players:", x, 10, 0xFFFFFF);
				}
			}
		}
		main.sendNames(names);
	}
	
	public void unMutePlayer(String unmute) {
		String preText = "";
		curLine = "";
		try {
			curLine = null;
			
			reader = new FileReader(muteList);

			bufferedReader = new BufferedReader(reader);
			
			while ((curLine = bufferedReader.readLine()) != null) {
				_nameCount++;
				
				preText += " " + curLine + "";
			}
			
			String[] words = preText.toLowerCase().split(" ");
			
			for(int i = 0; i < words.length; i++) {
				
				if(words[i].equals(unmute.toLowerCase())) {
					words[i] = "";
				}
			}
			
			// Remove from visible list instantly
			if(_mutedPlayers != null) {
				String[] mutedPlayersArray = _mutedPlayers.toLowerCase().split(" ");
				_mutedPlayers = "";
				for(int i = 0; i < mutedPlayersArray.length; i++) {
					if(mutedPlayersArray[i].equals(unmute.toLowerCase())) {
						mutedPlayersArray[i] = null;
					}
					_mutedPlayers += " " + mutedPlayersArray[i];
				}
			}
			
			names = _mutedPlayers;
			
			String finalCompilation = new String();
			
			for(int i = 0; i < words.length; i++) {
				finalCompilation += words[i] + "\n";
			}
			
			writer = new FileWriter(muteList);
			bufferedWriter = new BufferedWriter(writer);
			
			bufferedWriter.write(finalCompilation);
			
			bufferedWriter.close();
			bufferedReader.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mutePlayer(String mute) {
		String preText = "";
		try {
			curLine = null;
			
			reader = new FileReader(muteList);

			bufferedReader = new BufferedReader(reader);
			
			while ((curLine = bufferedReader.readLine()) != null) {
				_nameCount++;
				if(!(curLine.equals(null)) && !(curLine.equals("null")) && !(curLine.equals("")) && !(curLine.equals(new String()))) {
					preText += " " + curLine + "\n";
				}
			}
			
			String[] preTextArray = preText.split(" ");
			int count = 0;
			for(int i = 0; i < preTextArray.length; i++) {
				if(!preTextArray[i].equals(mute)) {
					count++;
				}
				if(count == preTextArray.length) {
					preText += " " + mute;
					_mutedPlayers += " " + mute;
				}
			}
						
			String[] words = preText.split(" ");
			
			
			String finalCompilation = new String();
			
			for(int i = 0; i < words.length; i++) {
				finalCompilation += words[i] + "\n";
			}
			
			writer = new FileWriter(muteList);
			bufferedWriter = new BufferedWriter(writer);
			
			bufferedWriter.write(finalCompilation);
			
			bufferedWriter.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initList();
	}
}
