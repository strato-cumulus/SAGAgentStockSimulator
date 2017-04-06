package view;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SagView {

    Map<String, Long> entries = new LinkedHashMap<>();

    public Long put(String s, Long o) {
        return entries.put(s, o);
    }

    public abstract void publish();
}
