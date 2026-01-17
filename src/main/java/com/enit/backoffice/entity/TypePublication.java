package com.enit.backoffice.entity;

public enum TypePublication {
    ARTICLE_SCIENTIFIQUE("Article scientifique"),
    ETUDE_DE_CAS("Étude de cas"),
    LANCEMENT_PRODUIT_SERVICE("Lancement d'un produit ou service"),
    ACTUALITES_INNOVATION("Actualités/innovation");

    private final String label;

    TypePublication(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypePublication fromLabel(String text) {
        for (TypePublication type : TypePublication.values()) {
            if (type.label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TypePublication: " + text);
    }
}
