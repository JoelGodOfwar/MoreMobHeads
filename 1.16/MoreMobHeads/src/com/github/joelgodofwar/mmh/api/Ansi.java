package com.github.joelgodofwar.mmh.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Usage:
 * <li>String msg = Ansi.Red.and(Ansi.BgYellow).format("Hello %s", name)</li>
 * <li>String msg = Ansi.Blink.colorize("BOOM!")</li>
 * 
 * Or, if you are adverse to that, you can use the constants directly:
 * <li>String msg = new Ansi(Ansi.ITALIC, Ansi.GREEN).format("Green money")</li>
 * Or, even:
 * <li>String msg = Ansi.BLUE + "scientific"</li>
 * 
 * NOTE: Nothing stops you from combining multiple FG colors or BG colors, 
 *       but only the last one will display.
 * 
 * @author dain
 *
 */
public final class Ansi {

	// Color code strings from:
	// http://www.topmudsites.com/forums/mud-coding/413-java-ansi.html
	public static final String	RESET				= "\u001B[0m";

	public static final String	HIGH_INTENSITY		= "\u001B[1m";
	public static final String	LOW_INTENSITY		= "\u001B[2m";
	
	public static final String HighIntensity = HIGH_INTENSITY;
	public static final String BOLD = HighIntensity;
	public static final Ansi LowIntensity = new Ansi(LOW_INTENSITY);
	public static final Ansi Normal = LowIntensity;

	public static final String	ITALIC				= "\u001B[3m";
	public static final String	UNDERLINE			= "\u001B[4m";
	public static final String	BLINK				= "\u001B[5m";
	public static final String	RAPID_BLINK			= "\u001B[6m";
	public static final String	REVERSE_VIDEO		= "\u001B[7m";
	public static final String	INVISIBLE_TEXT		= "\u001B[8m";

	public static final String	BLACK				= BOLD + "\u001B[30m";
	public static final String	RED					= BOLD + "\u001B[31m";
	public static final String	GREEN				= BOLD + "\u001B[32m";
	public static final String	YELLOW				= BOLD + "\u001B[33m";
	public static final String	BLUE				= BOLD + "\u001B[34m";
	public static final String	MAGENTA				= BOLD + "\u001B[35m";
	public static final String	CYAN				= BOLD + "\u001B[36m";
	public static final String	WHITE				= BOLD + "\u001B[37m";
	public static final String	DARK_BLACK			= "\u001B[30m";
	public static final String	DARK_RED			= "\u001B[31m";
	public static final String	DARK_GREEN			= "\u001B[32m";
	public static final String	DARK_YELLOW			= "\u001B[33m";
	public static final String	DARK_BLUE			= "\u001B[34m";
	public static final String	DARK_MAGENTA		= "\u001B[35m";
	public static final String	DARK_CYAN			= "\u001B[36m";
	public static final String	DARK_WHITE			= "\u001B[37m";

	public static final String	BACKGROUND_BLACK	= "\u001B[40m";
	public static final String	BACKGROUND_RED		= "\u001B[41m";
	public static final String	BACKGROUND_GREEN	= "\u001B[42m";
	public static final String	BACKGROUND_YELLOW	= "\u001B[43m";
	public static final String	BACKGROUND_BLUE		= "\u001B[44m";
	public static final String	BACKGROUND_MAGENTA	= "\u001B[45m";
	public static final String	BACKGROUND_CYAN		= "\u001B[46m";
	public static final String	BACKGROUND_WHITE	= "\u001B[47m";

	
	
	final private String[] codes;
	final private String codes_str; 
	public Ansi(String... codes) {
		this.codes = codes;
		String _codes_str = "";
		for (String code : codes) {
			_codes_str += code;
		}
		codes_str = _codes_str;
	}
	
	public Ansi and(Ansi other) {
		List<String> both = new ArrayList<String>();
	    Collections.addAll(both, codes);
	    Collections.addAll(both, other.codes);
		return new Ansi(both.toArray(new String[] {}));
	}

	public String colorize(String original) {
		return codes_str + original + RESET;
	}
	
	public String format(String template, Object... args) {
		return colorize(String.format(template, args));
	}
}