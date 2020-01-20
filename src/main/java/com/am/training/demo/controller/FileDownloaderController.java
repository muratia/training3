package com.am.training.demo.controller;

import com.am.training.demo.DemoApplication;
import com.am.training.demo.dto.RowItem;
import com.am.training.demo.entity.DataItem;
import com.am.training.demo.ifs.FileSystemStorageService;
import com.am.training.demo.ifs.StorageService;
import com.am.training.demo.processor.CsvProcessor;
import com.am.training.demo.service.DataItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileDownloaderController {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloaderController.class);

    @Autowired
    private DataItemService dataItemService;
    private StorageService storageService;

    public FileDownloaderController() {
        try {

            this.storageService = new FileSystemStorageService();

        } catch (Exception ex) {

            logger.info("File Downloader Controller called" + ex.getMessage());
        }
    }


    //  url=url1&url=url2
    @GetMapping(value = "/evaluation{url}", produces = "application/json")

    public synchronized ModelAndView getURLs(@RequestParam(value = "url") String url) throws IOException {


        ModelAndView mav = new ModelAndView("evaluation");
        String[] urlString = null;
        List<RowItem> rowItems = new ArrayList<>();
        List<String> urlt = new ArrayList<>();
        if (url.contains(",")) {
            urlString = url.split("\\,");
            for (String sr : urlString) {
                if (sr.startsWith("http") || sr.startsWith("https")) {
                    urlt.add(sr);
                }
            }

        }
        logger.info("Urls size:  " + urlt.size());

        int index = 1;
        for (String url1 : urlt) {
            RowItem rowItem = new RowItem();
            URL FILE_URL = new URL(url1);
            logger.info("");
            String FILE_NAME = DemoApplication.rootLocation.toAbsolutePath().toString() + "\\Data" + index++ + ".csv";
            logger.info("The file name: " + FILE_NAME);
            InputStream in = FILE_URL.openStream();
            Files.copy(in, Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
            rowItem.setUrl(url1);
            rowItem.setNewFileName(FILE_NAME);
            rowItems.add(rowItem);

            logger.info("Starting to store in database");
            CsvProcessor csvProcessor = new CsvProcessor();

            List<DataItem> items = csvProcessor.loadData(FILE_NAME);
            this.dataItemService.save(items);

            logger.info("Data are saved");
        }
        mav.addObject("urls", rowItems);
        File[] files = new File(DemoApplication.rootLocation.toAbsolutePath().toString()).listFiles();
        for (File theFile : files) {
            theFile.delete();
        }


        return mav;
    }
}
