package model.request;

import model.Stock;

import java.util.HashMap;
import java.util.LinkedList;

public class EquilibriumRequest {
    public final HashMap<Stock, Integer> equilibriumPrice = new HashMap<Stock, Integer>();
    public final HashMap<Stock, LinkedList<Integer>> historicalEquilibriumPrice = new HashMap<Stock, LinkedList<Integer>>();
}
