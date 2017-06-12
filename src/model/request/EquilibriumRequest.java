package model.request;

import java.util.HashMap;
import java.util.LinkedList;

public class EquilibriumRequest {
    public final HashMap<String, Integer> equilibriumPrice = new HashMap<String, Integer>();
    public final HashMap<String, LinkedList<Integer>> historicalEquilibriumPrice = new HashMap<String, LinkedList<Integer>>();
}
