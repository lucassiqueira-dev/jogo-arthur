package microciv.society;

/*
    Cultures will aftect most of the civilization's aspects.
    They can be shaped and changed over time, with given time and enough population, 
    they can also diverge into new cultures, or merge with other cultures.
*/

public class Culture {
    
    public String name;
    public String description;
    public Traits[] traits; // Traits will be a list of strings that will be used to affect the civilization's aspects.

    public Culture() {
        this.name = "Base Culture";
        this.description = "The base culture is the starting point for all civilizations."
                         + "It has no special traits and is the default culture for all civilizations.";
        this.traits = new Traits[0];
    }
    

}
