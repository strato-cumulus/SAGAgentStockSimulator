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

    private Map<Stock, Integer> shareAmounts;

    public FileShareCreator(String file) throws ResourceCreationException {
        Properties fileStockDefinitions = new Properties();
        try {
            fileStockDefinitions.load(new InputStreamReader(new FileInputStream(file)));
        }
        catch (IOException e) {
            throw new ResourceCreationException((e instanceof FileNotFoundException)?"File not found":"Malformed input");
        }
        Set<String> tickerCodes = fileStockDefinitions.stringPropertyNames();
        try {
            this.shareAmounts = tickerCodes
                    .stream()
                    .collect(Collectors.toMap(Stock::new, Integer::parseInt));
        }
        catch (NumberFormatException e) {
            throw new ResourceCreationException("Malformed input");
        }
    }

    @Override
    public Map<Stock, List<Share>> createShares() {
        Map<Stock, List<Share>> sharesPerStock = new TreeMap<>(Comparator.comparing(Stock::getTickerCode));
        for(Map.Entry<Stock, Integer> shareAmount: shareAmounts.entrySet()) {
            List<Share> shares = IntStream
                    .range(0, shareAmount.getValue())
                    .mapToObj(i -> new Share(shareAmount.getKey(), 1))
                    .collect(Collectors.toList());
            sharesPerStock.put(shareAmount.getKey(), shares);
        }
        return sharesPerStock;
    }
}
