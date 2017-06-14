package model.messagecontent;

import java.time.LocalDateTime;

public class Information {
    public final String stock;
    public final int positivity;
    public final LocalDateTime localDateTime;

    public Information(String stock, int positivity, LocalDateTime localDateTime) {
        this.stock = stock;
        this.positivity = positivity;
        this.localDateTime = localDateTime;
    }
}
