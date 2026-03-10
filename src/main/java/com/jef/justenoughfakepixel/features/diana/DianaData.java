package com.jef.justenoughfakepixel.features.diana;

/**
 * Gson-serializable data object for all Diana tracking stats.
 * Saved to diana_stats.json in the JEF config directory.
 */
public class DianaData {


    public int  totalBurrows   = 0;
    public long sessionStartMs = -1L;   // -1 = no session started yet
    public int  totalMobs      = 0;

    // Inquisitor
    public int mobsSinceInq     = 0;
    public int inqsSinceChimera = 0;
    public int totalInqs        = 0;

    // Minotaur
    public int minotaursSinceStick = 0;
    public int totalMinotaurs      = 0;

    // Minos Champion
    public int champsSinceRelic = 0;
    public int totalChamps      = 0;

    // burrow drops
    public int  griffinFeathers = 0;
    public int  souvenirs       = 0;
    public int  crownsOfGreed   = 0;
    public long totalCoins      = 0L;

    // diana mob drops
    public int dwarfTurtleShelmets   = 0;
    public int antiqueRemedies       = 0;
    public int crochetTigerPlushies  = 0;
}