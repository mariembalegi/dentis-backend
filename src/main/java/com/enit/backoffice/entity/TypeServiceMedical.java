package com.enit.backoffice.entity;

public enum TypeServiceMedical {
    // Medical service types
    DIAGNOSTIC_SOINS_COURANTS("Diagnostic et soins courants"),
    PARODONTOLOGIE("Parodontologie"),
    RADIOLOGIE_IMAGERIE("Radiologie et imagerie dentaire"),
    ENDODONTIE("Endodontie"),
    ESTHETIQUE_DENTAIRE("Esthétique dentaire"),
    IMPLANTOLOGIE("Implantologie");

    private final String label;

    TypeServiceMedical(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypeServiceMedical fromLabel(String text) {
        for (TypeServiceMedical type : TypeServiceMedical.values()) {
            if (type.label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TypeServiceMedical: " + text);
    }
}
