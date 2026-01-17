package com.enit.backoffice.entity;

public enum TypeRecouvrement {
    MEDECIN_FAMILLE("Médecin de la famille"),
    REMBOURSEMENT("Remboursement"),
    SANTE_PUBLIQUE("Santé publique");

    private final String label;

    TypeRecouvrement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypeRecouvrement fromLabel(String text) {
        for (TypeRecouvrement type : TypeRecouvrement.values()) {
            if (type.label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TypeRecouvrement: " + text);
    }
}
