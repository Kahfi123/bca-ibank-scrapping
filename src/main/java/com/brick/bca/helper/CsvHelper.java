package com.brick.bca.helper;

import com.brick.bca.service.BCATransactionServiceImpl;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileWriter;
import java.io.IOException;

import java.sql.Timestamp;
public class CsvHelper {
    private static final String LINE_SEPARATOR = "\n";
    static Logger log = LoggerFactory.getLogger(BCATransactionServiceImpl.class);
    public static void saveDataToCSV(Document transactionDocument){
        log.info("Saving data to csv");
        try {

            Element table = transactionDocument.select("table").get(3);
            Elements rows = table.select("tr");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            FileWriter csvWriter = new FileWriter("csv\\"+rows.get(1).select("td").get(2).text()+"-"+timestamp.getTime()+".csv");
            for(int i = 1 ; i < rows.size()-1;i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                for(int j = 0; j < 3; j++){
                    csvWriter.append(cols.get(j).text());
                }
                csvWriter.append(LINE_SEPARATOR);
            }
            Element balanceTable = transactionDocument.select("table").get(5);
            csvWriter.append("Current Balance");
            csvWriter.append(":");
            csvWriter.append(balanceTable.select("tr").get(4).select("td").get(2).text());
            csvWriter.append("\n");
            csvWriter.append("\n");
            csvWriter.append("Date,Description,Branch,Amount,Type,Balance");
            Element transactionTable = transactionDocument.select("table").get(4);
            Elements transactionRows = transactionTable.select("tr");
            csvWriter.append("\n");
            for (int i = 1; i < transactionRows.size(); i++) {
                Element row = transactionRows.get(i);
                Elements cols = row.select("td");
                for(int j = 0; j  < 6; j++){
                    String transactionValue = cols.get(j).text();
                    if(j == 3 || j == 5){
                        transactionValue = transactionValue.replace(",","");
                    }
                    csvWriter.append(transactionValue);
                    if(j!=5) csvWriter.append(",");

                }
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
            log.info("Successfully Saving data to csv");
        } catch (IOException e) {
            log.error("Error occured: {}",e.toString());
        } catch (IndexOutOfBoundsException e){
            log.error("Error occured, most likely due to wrong username or password");
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user_id not found");
        }

    }
}
