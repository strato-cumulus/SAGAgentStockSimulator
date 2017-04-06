package view;

public class ConsoleSagView extends SagView {

    private static String format = "%3s %3d.%2d";

    @Override
    public void publish() {
        entries.forEach((k, v) -> System.out.println(String.format(format, k, v / 100, v % 100)));
    }
}
