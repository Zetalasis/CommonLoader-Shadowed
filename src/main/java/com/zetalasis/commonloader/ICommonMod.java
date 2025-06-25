package com.zetalasis.commonloader;

/** Interface for all mods that build against CommonLoader. Implements methods that will be called by CommonLoader. */
public interface ICommonMod {
    /** This method gets called whenever the mod is initialized on either the server or the client. */
    void init();

    /** Override this and return the display name of the mod. */
    String getDisplayName();
    /** Override this and return the Mod ID of the mod. */
    String getModId();
}