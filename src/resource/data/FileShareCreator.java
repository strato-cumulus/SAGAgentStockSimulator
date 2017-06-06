package resource.data;

import model.Share;
import model.Stock;
import resource.ResourceCreationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class FileShareCreator extends ShareCreator {

    private final String file;

    public FileShareCreator(String file) throws ResourceCreationException {
        this.file = file;
    }

    @Override
    public Map<Stock, List<Share>> createShares() throws ResourceCreationException {
        Properties fileStockDefinitions = new Properties();
        try {
            fileStockDefinitions.load(new InputStreamReader(new FileInputStream(file)));
        }
        catch (IOException e) {
            throw new ResourceCreationException((e instanceof FileNotFoundException)?"File not found":"Malformed input");
        }
        Set<String> tickerCodes = fileStockDefinitions.stringPropertyNames();
        Map<Stock, List<Share>> sharesPerStock = new TreeMap<>(Comparator.comparing(Stock::getTickerCode));
        for(String tickerCode: tickerCodes) {
            String[] shareData = fileStockDefinitions.getProperty(tickerCode).split(";");
            try {
                int amount = Integer.parseInt(shareData[0]);
                int price = Integer.parseInt(shareData[1]);
                Stock stock = new Stock(tickerCode);
                List<Share> shares = IntStream
                        .range(0, amount)
                        .mapToObj(i -> new Share(stock, price))
                        .collect(Collectors.toList());
                sharesPerStock.put(stock, shares);
            }
            catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new ResourceCreationException("Malformed input");
            }
        }
        return sharesPerStock;
    }
}
