package com.github.joelgodofwar.mmh.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker2 {
    private static int project;
    private URL checkURL;
    private String newVersion;
    private JavaPlugin plugin;
    /**private int Major; // 1
	private int Minor; // 16
	private int Patch; // 1
	private int Build; // ?*/
	String[] strVersionNew; // [0]=1.14 [1]=1.0.0.?
	String[] strVersionNewMinMCV; // [0]=1 [1]=14
	String[] strVersionNewVers; // [0]=1 [1]=0 [2]=0 [3]=?
	int[] intVersionNewMinMCV; // [0]=1 [1]=14
	int[] intVersionNewVers; // [0]=1 [1]=0 [2]=0 [3]=?
	String[] strVersionCurrent; // [0]=1.14 [1]=1.0.0.?
	String[] strVersionCurMinMCV; // [0]=1 [1]=14
	String[] strVersionCurVers; // [0]=1 [1]=0 [2]=0 [3]=?
	int[] intVersionCurMinMCV; // [0]=1 [1]=14
	int[] intVersionCurVers; // [0]=1 [1]=0 [2]=0 [3]=?

    public UpdateChecker2(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/" + newVersion.substring(0, 4) + "/version.txt");
        }catch(MalformedURLException e) {
            Bukkit.getLogger().warning(Ansi.RED + "Could not connect to update server.");
            //Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
    public static String getResourceUrl() {return "https://spigotmc.org/resources/" + project;}
    public boolean checkForUpdates() throws Exception {
    	boolean isOutdated = false;
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        strVersionNew = newVersion.split("_"); // Split into minimum MC version and version
        /**strVersionNewMinMCV = strVersionNew[0].split("."); // Split Minimum MC Version into Major, Minor, Patch, and Build
        for(int i = 0; i < strVersionNewMinMCV.length; i++){
        	intVersionNewMinMCV[i] = NumberUtils.toInt(strVersionNewMinMCV[i]); // Add Minimum MC version to int array
    	}*/
        Version newVers = new Version(strVersionNew[1]);
        
        /**strVersionNewVers = strVersionNew[1].split("."); // Split Version into Major, Minor, Patch, and Build
        for(int i = 0; i < strVersionNewVers.length; i++){
        	intVersionNewVers[i] = NumberUtils.toInt(strVersionNewVers[i]); // Add Version to int array
    	}*/
        
        strVersionCurrent = plugin.getDescription().getVersion().split("_");
       /** strVersionCurMinMCV = strVersionCurrent[0].split(".");
        for(int i = 0; i < strVersionCurMinMCV.length; i++){
        	intVersionCurMinMCV[i] = NumberUtils.toInt(strVersionCurMinMCV[i]);
    	}*/
        Version oldVers = new Version(strVersionCurrent[1]);
        /**strVersionCurVers = strVersionCurrent[1].split(".");
        for(int i = 0; i < strVersionCurVers.length; i++){
        	intVersionCurVers[i] = NumberUtils.toInt(strVersionCurVers[i]);
    	}*/
        if(oldVers.isDev()){ /** If currently on Dev version, ignore build -1 from Patch */
        	if(newVers.Major() == oldVers.Major()){/** same do nothing */}
			if(newVers.Minor() > oldVers.Minor()){isOutdated = true;}
			else if(newVers.Minor() == oldVers.Minor()){/** same do nothing */}
        	if(newVers.Patch() > (oldVers.Patch() - 1)){isOutdated = true;}
        	else if(newVers.Patch() == (oldVers.Patch() - 1)){/** same do nothing */}
        	/**if(newVers.Build() > oldVers.Build()){isOutdated = true;}
        	else if(newVers.Build() == oldVers.Build()){/** same do nothing }*/
        }else {
        	if(newVers.Major() == oldVers.Major()){/** same do nothing */}
			if(newVers.Minor() > oldVers.Minor()){isOutdated = true;}
			else if(newVers.Minor() == oldVers.Minor()){/** same do nothing */}
        	if(newVers.Patch() > oldVers.Patch()){isOutdated = true;}
        	else if(newVers.Patch() == oldVers.Patch()){/** same do nothing */}
        	if(newVers.Build() > oldVers.Build()){isOutdated = true;}
        	else if(newVers.Build() == oldVers.Build()){/** same do nothing */}
        }
        return !isOutdated;//plugin.getDescription().getVersion().equals(newVersion);
    }
    @SuppressWarnings("unused")
	public boolean isOutdated(String curVersion, String newVersion){
    	boolean isDev = false;
    	if(curVersion.contains(".D")){
    		curVersion = curVersion.replace("D", "");
    		isDev = true;
    	}
    	int[] curVersionInt = getVersion(curVersion);
    	int[] newVersionInt = getVersion(newVersion);
    	if(verLeft(curVersion).equals(verLeft(newVersion))){
    		if(curVersionInt.length > newVersionInt.length){
    			//for(int i = 0; i < curVersionInt.length; i++){
    				switch(curVersionInt.length){
    				case 0: //Major only
    					
    				case 1: //Major.Minor only
    				
    				case 2: //Major.Minor.Patch only
    					if(curVersionInt[0] == newVersionInt[0]&&curVersionInt[1] == newVersionInt[1]&&curVersionInt[2] == newVersionInt[2]){
    						return false;
    					}else if(curVersionInt[0] == newVersionInt[0]&&curVersionInt[1] == newVersionInt[1]&&curVersionInt[2] < newVersionInt[2]){
    						return true;
    					}else if(curVersionInt[0] == newVersionInt[0]&&curVersionInt[1] < newVersionInt[1]&&curVersionInt[2] >= newVersionInt[2]){
    						return true;
    					}else if(curVersionInt[0] < newVersionInt[0]&&curVersionInt[1] >= newVersionInt[1]&&curVersionInt[2] >= newVersionInt[2]){
    						return true;
    					}
    					
    				case 3: // Major.Minor.Patch.Build
    					
    				}
    			//}
    		}else{
    			
    		}
    	}else{
    		return true;
    	}
    	return false;
    }
    public String verLeft(String string){
    	String[] splitstring = string.split("_");
    	Version a = new Version("2.13.44.D5");
    	Version b = new Version("2.13.45");
    	if(a.Major() > b.Major()){
    		
    	}
    	return splitstring[0];
    }
    public String verRight(String string){
    	String[] splitstring = string.split("_");
    	return splitstring[1];
    }
    public  int[] getMinimumVersion(String string){
    	if(string.contains("_")){
	    	strVersionNew = string.split("_"); // Split into minimum MC version and version
	        strVersionNewMinMCV = strVersionNew[0].split("."); // Split Minimum MC Version into Major, Minor, Patch, and Build
	        for(int i = 0; i < strVersionNewMinMCV.length; i++){
	        	intVersionNewMinMCV[i] = NumberUtils.toInt(strVersionNewMinMCV[i]); // Add Minimum MC version to int array
	    	}
	        return intVersionNewMinMCV;
    	}else{
    		int[] arr={0,0,0,0};
    		return arr;
    	}
    }
    public int[] getVersion(String string){
    	if(string.contains("_")){
    		strVersionNew = string.split("_"); // Split into minimum MC version and version
    		strVersionNewVers = strVersionNew[1].split("."); // Split Version into Major, Minor, Patch, and Build
            for(int i = 0; i < strVersionNewVers.length; i++){
            	intVersionNewVers[i] = NumberUtils.toInt(strVersionNewVers[i]); // Add Version to int array
        	}
            return intVersionNewVers;
    	}else{
    		int[] arr={0,0,0,0};
    		return arr;
    	}
    }
    public class Version{
	    private int Major; // 1
		private int Minor; // 16
		private int Patch; // 1
		private int Build; // ?
		private boolean isDev = false;
		public Version(String string){
			String[] string2 = string.split(".");
			this.Major = NumberUtils.toInt(string2[0]);
			this.Minor = NumberUtils.toInt(string2[1]);
			this.Patch = NumberUtils.toInt(string2[2]);
			if(string2[3] != null){
				if(string2[3].toUpperCase().contains("D")){
					isDev =  true;
					string2[3] = string2[3].toUpperCase().replace("D", "");
				}
				this.Build = NumberUtils.toInt(string2[3]);
			}else{
				this.Build = 0;
			}
		}
		public int Major(){return Major;}
		public int Minor(){return Minor;}
		public int Patch(){return Patch;}
		public int Build(){return Build;}
		public boolean isDev(){return isDev;}
    }
}