package resource.data;

import model.Portfolio;
import model.Stock;
import resource.ResourceCreationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class FileShareCreator extends ShareCreator {

    private final String file;

    public FileShareCreator(String file) throws ResourceCreationException {
        this.file = file;
    }

    @Override
    public Portfolio createShares() throws ResourceCreationException {
        Properties fileStockDefinitions = new Properties();
        try {
            fileStockDefinitions.load(new InputStreamReader(new FileInputStream(file)));
        }
        catch (IOException e) {
            throw new ResourceCreationException((e instanceof FileNotFoundException)?"File not found":"Malformed input");
        }
        Set<String> tickerCodes = fileStockDefinitions.stringPropertyNames();
        Portfolio portfolio = new Portfolio();
        for(String tickerCode: tickerCodes) {
            String[] shareData = fileStockDefinitions.getProperty(tickerCode).split(";");
            try {
                int amount = Integer.parseInt(shareData[0]);
                int price = Integer.parseInt(shareData[1]);
                Stock stock = new Stock(tickerCode);
                portfolio.addStock(stock, amount, price);
            }
            catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new ResourceCreationException("Malformed input");
            }
        }
        return portfolio;
    }
}
