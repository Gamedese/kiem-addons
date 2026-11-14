package com.example.client.pet;

/**
 * Omuz yoldasi turleri. {@code name()} config dosyasina ({@code pet.type}) yazilir.
 * titleKey / descKey -> {@link com.example.client.KiemLang} anahtarlari.
 */
public enum ShoulderPetType {
    OFF("pets.none", "pets.none"),
    HAMSTER("pets.hamster.title", "pets.hamster.desc"),
    HAMSTER_HD("pets.hamster_hd.title", "pets.hamster_hd.desc"),
    OCTOPUS("pets.octopus.title", "pets.octopus.desc"),
    DRAGON("pets.dragon.title", "pets.dragon.desc"),
    INVENTORY_CURSE("pets.inventory_curse.title", "pets.inventory_curse.desc"),
    VOID_GARGOYLE("pets.void_gargoyle.title", "pets.void_gargoyle.desc");

    public final String titleKey;
    public final String descKey;

    ShoulderPetType(String titleKey, String descKey) {
        this.titleKey = titleKey;
        this.descKey = descKey;
    }

    public static ShoulderPetType fromName(String name) {
        if (name == null) return OFF;
        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OFF;
        }
    }
}
