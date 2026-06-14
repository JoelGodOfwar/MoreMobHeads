package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.commands.GiveHeadCommand;
import com.github.joelgodofwar.mmh.commands.ViewHeadsCommand;

public interface MMHEventHandler {
	void loadHeadsAndRecipes();
	GiveHeadCommand getGiveHeadCommand();
	ViewHeadsCommand getViewHeadsCommand();
	void loadMobHeads();
}