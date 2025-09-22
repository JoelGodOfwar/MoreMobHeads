package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.command.GiveHeadCommand;
import com.github.joelgodofwar.mmh.command.ViewHeadsCommand;

public interface MMHEventHandler {
	void loadHeadsAndRecipes();
	GiveHeadCommand getGiveHeadCommand();
	ViewHeadsCommand getViewHeadsCommand();
	void loadMobHeads();
}