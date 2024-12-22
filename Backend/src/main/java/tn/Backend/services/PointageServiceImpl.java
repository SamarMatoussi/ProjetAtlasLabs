package tn.Backend.services;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.Backend.entites.Pointage;
import tn.Backend.repository.PointageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointageServiceImpl implements PointageService {

    private final PointageRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(PointageServiceImpl.class);

    public static boolean isValidExcelFile(MultipartFile file) {
        return file != null && Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<Pointage> getPointageDataFromExcel(InputStream inputStream) {
        List<Pointage> pointages = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0); // Assuming the first sheet
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) { // Skip header row
                    rowIndex++;
                    continue;
                }
                Pointage pointage = new Pointage();
                for (Cell cell : row) {
                    int cellIndex = cell.getColumnIndex();
                    switch (cellIndex) {
                        case 0 -> pointage.setNom(getCellValueAsString(cell));
                        case 1 -> {
                            String cellValue = getCellValueAsString(cell);
                            try {
                                pointage.setDate(LocalDateTime.parse(cellValue, formatter));
                            } catch (DateTimeParseException e) {
                                logger.error("Invalid date format in row {}: {}", rowIndex, cellValue, e);
                                throw new IllegalArgumentException("Invalid date format in row " + rowIndex + ": " + cellValue, e);
                            }
                        }
                        case 2 -> pointage.setStatus(getCellValueAsString(cell));
                        case 3 -> pointage.setCin(Long.parseLong(getCellValueAsString(cell)));
                        default -> {
                        }
                    }
                }
                pointages.add(pointage);
                rowIndex++;
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file", e);
            throw new IllegalArgumentException("Failed to process the file", e);
        }
        return pointages;
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue() % 1 == 0 ? String.valueOf((int) cell.getNumericCellValue()) : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    @Override
    public void savePointageToDatabase(MultipartFile file) {
        if (isValidExcelFile(file)) {
            try (InputStream inputStream = file.getInputStream()) {
                List<Pointage> pointages = getPointageDataFromExcel(inputStream);
                repository.saveAll(pointages);
            } catch (IOException e) {
                logger.error("Error processing Excel file", e);
                throw new IllegalArgumentException("Failed to process the file", e);
            }
        } else {
            logger.error("Invalid file type: {}", file.getContentType());
            throw new IllegalArgumentException("The file is not a valid Excel file");
        }
    }

    @Override
    public Map<Long, Map<String, Long>> getDaysByMonthForAllPersons() {
        List<Pointage> pointages = repository.findAll();

        // Group by CIN and then by year-month
        return pointages.stream()
                .collect(Collectors.groupingBy(
                        Pointage::getCin,
                        Collectors.groupingBy(
                                p -> p.getDate().getYear() + "-" + String.format("%02d", p.getDate().getMonthValue()),
                                Collectors.counting()
                        )
                ));
    }

    // New method to count present and absent days
    @Override
    public Map<Long, Map<String, Map<String, Long>>> getPresenceAbsenceByMonthForAllPersons() {
        List<Pointage> pointages = repository.findAll();

        // Group by CIN, then by year-month, then count present/absent days
        return pointages.stream()
                .collect(Collectors.groupingBy(
                        Pointage::getCin,
                        Collectors.groupingBy(
                                p -> p.getDate().getYear() + "-" + String.format("%02d", p.getDate().getMonthValue()),
                                Collectors.groupingBy(
                                        Pointage::getStatus,
                                        Collectors.counting()
                                )
                        )
                ));
    }
}
