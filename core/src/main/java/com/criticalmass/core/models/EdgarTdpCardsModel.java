package com.criticalmass.core.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EdgarTdpCardsModel {
    private String name;
    private String manaCost;
    private double cmc;
    private List<String> colors;
    private List<String> colorIdentity;
    private String type;
    private List<String> types;
    private List<String> subtypes;
    private String rarity;
    private String set;
    private String setName;
    private String text;
    private String flavor;
    private String artist;
    private String number;
    private String power;
    private String toughness;
    private String layout;
    private String multiverseid;
    private String imageUrl;
    private List<String> variations;
    private List<Ruling> rulings;
    private List<String> printings;
    private String originalText;
    private String originalType;
    private List<Legality> legalities;
    private String id;

    @Data
    public static class Ruling {
        private String date;
        private String text;
    }

    @Data
    public static class ForeignName {
        private String name;
        private String text;
        private String type;
        private String flavor;
        private String imageUrl;
        private String language;
        private Map<String, String> identifiers;
        private String multiverseid;
    }

    @Data
    public static class Legality {
        private String format;
        private String legality;
    }
}