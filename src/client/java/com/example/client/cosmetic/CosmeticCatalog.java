package com.example.client.cosmetic;

/**
 * Tum genel kozmetik kategorilerinin ve trail efektlerinin enum tanimlari.
 * Her kategori bagimsiz bir slot olarak ayni anda takilabilir.
 */
public final class CosmeticCatalog {
    private CosmeticCatalog() {}

    public enum WingType {
        OFF("OFF"),
        SERAPHIM("SERAPHIM"),
        VALKYRIE("VALKYRIE");

        public final String id;
        WingType(String id) { this.id = id; }

        public static WingType fromName(String s) {
            if (s == null) return OFF;
            for (WingType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum HatType {
        OFF("OFF"),
        RONIN("RONIN"),
        SHINOBI_HEADBAND("SHINOBI_HEADBAND"),
        WITCH("WITCH"),
        KITSUNE_EARS("KITSUNE_EARS"),
        CELESTIAL_HALO("CELESTIAL_HALO"),
        ONI_HORNS("ONI_HORNS"),
        KITSUNE_MASK("KITSUNE_MASK"),
        BROKEN_HALO("BROKEN_HALO"),
        CROWN("CROWN"),
        STRAW_PIRATE("STRAW_PIRATE"),
        USHANKA_FUR("USHANKA_FUR"),
        TRICORN_CAPTAIN("TRICORN_CAPTAIN"),
        NAPOLEON_BICORNE("NAPOLEON_BICORNE"),
        EGIRL_HEADPHONES("EGIRL_HEADPHONES");

        public final String id;
        HatType(String id) { this.id = id; }

        public static HatType fromName(String s) {
            if (s == null) return OFF;
            if ("ROYAL_CROWN".equalsIgnoreCase(s)) return CROWN;
            for (HatType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum NeckType {
        OFF("OFF"),
        CRIMSON_SCARF("CRIMSON_SCARF"),
        GOLD_CHAIN("GOLD_CHAIN"),
        SHADOW_NINJA_MASK("SHADOW_NINJA_MASK"),
        ONI_MENPO("ONI_MENPO"),
        COOL_SUNGLASSES("COOL_SUNGLASSES"),
        SNOW_SCARF("SNOW_SCARF"),
        RUBY_CHOKER("RUBY_CHOKER");

        public final String id;
        NeckType(String id) { this.id = id; }

        public static NeckType fromName(String s) {
            if (s == null) return OFF;
            for (NeckType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum HandType {
        OFF("OFF"),
        FIGHTER_WRAPS("FIGHTER_WRAPS"),
        SYLAS_CHAINS("SYLAS_CHAINS"),
        COMBAT_GLOVES("COMBAT_GLOVES");

        public final String id;
        HandType(String id) { this.id = id; }

        public static HandType fromName(String s) {
            if (s == null) return OFF;
            for (HandType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum WaistType {
        OFF("OFF"),
        KATANA_SASH("KATANA_SASH"),
        KITSUNE_TAIL("KITSUNE_TAIL"),
        DEMON_TAIL("DEMON_TAIL"),
        TWIN_DAGGERS("TWIN_DAGGERS"),
        CAT_TAIL("CAT_TAIL"),
        WOLF_TAIL("WOLF_TAIL");

        public final String id;
        WaistType(String id) { this.id = id; }

        public boolean isOff() {
            return this == OFF;
        }

        public static WaistType fromName(String s) {
            if (s == null) return OFF;
            if ("NEKOMATA_TAILS".equalsIgnoreCase(s)) return CAT_TAIL;
            for (WaistType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum FootType {
        OFF("OFF"),
        HERMES_BOOTS("HERMES_BOOTS"),
        SHINOBI_WRAPS("SHINOBI_WRAPS"),
        FIGHTER_LEG_WRAPS("FIGHTER_LEG_WRAPS"),
        BARE_FEET("BARE_FEET"),
        COMBAT_BOOTS("COMBAT_BOOTS"),
        CLASSIC_SNEAKERS("CLASSIC_SNEAKERS");

        public final String id;
        public final String key;
        FootType(String id) { this(id, id.toLowerCase(java.util.Locale.ROOT)); }
        FootType(String id, String key) { this.id = id; this.key = key; }

        public static FootType fromName(String s) {
            if (s == null) return OFF;
            for (FootType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }

    public enum TrailEffectType {
        OFF("OFF"),
        JUMP_MAGIC_CIRCLE("JUMP_MAGIC_CIRCLE"),
        SHADOW_SMOKE("SHADOW_SMOKE"),
        SNOWFLAKE_AURA("SNOWFLAKE_AURA"),
        CRIMSON_PETALS("CRIMSON_PETALS");

        public final String id;
        TrailEffectType(String id) { this.id = id; }

        public static TrailEffectType fromName(String s) {
            if (s == null) return OFF;
            for (TrailEffectType t : values()) {
                if (t.id.equalsIgnoreCase(s)) return t;
            }
            return OFF;
        }
    }
}
