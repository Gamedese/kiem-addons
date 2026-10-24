package com.example.client.pet;

import com.example.client.KiemConfig;

public class ShoulderPetManager {

    public static ShoulderPetType getSelectedPet() {
        return ShoulderPetType.fromName(KiemConfig.shoulderPetType);
    }

    public static void setSelectedPet(ShoulderPetType type) {
        if (type == null) type = ShoulderPetType.OFF;
        KiemConfig.shoulderPetType = type.name();
        KiemConfig.save();
    }

    public static void togglePet(ShoulderPetType type) {
        if (getSelectedPet() == type) {
            setSelectedPet(ShoulderPetType.OFF);
        } else {
            setSelectedPet(type);
        }
    }

    public static boolean isEquipped(ShoulderPetType type) {
        return getSelectedPet() == type && type != ShoulderPetType.OFF;
    }

    public static boolean isLeftShoulder() {
        return "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide);
    }

    public static void setLeftShoulder(boolean left) {
        KiemConfig.shoulderPetSide = left ? "LEFT" : "RIGHT";
        KiemConfig.save();
    }

    public static void toggleSide() {
        setLeftShoulder(!isLeftShoulder());
    }
}
