package peer.backend.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import peer.backend.mongo.entity.ActivityTracking;
import peer.backend.mongo.entity.TeamTracking;
import peer.backend.mongo.entity.UserTracking;
import peer.backend.mongo.repository.ActivityTrackingRepository;
import peer.backend.mongo.repository.TeamTrackingRepository;
import peer.backend.mongo.repository.UserTrackingRepository;

@RequiredArgsConstructor
@Service
public class ExcelService {

    private final UserTrackingRepository userTrackingRepository;
    private final TeamTrackingRepository teamTrackingRepository;
    private final ActivityTrackingRepository activityTrackingRepository;

    public ByteArrayInputStream getTrackingExcel() throws IOException {
        List<UserTracking> userTrackingList = this.userTrackingRepository.findAll();
        List<TeamTracking> teamTrackingList = this.teamTrackingRepository.findAll();
        List<ActivityTracking> activityTrackingList = this.activityTrackingRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Sheet userTrackingSheet = workbook.createSheet("UserTracking");
        createUserTrackingSheet(userTrackingList, userTrackingSheet, headerCellStyle);

        Sheet teamTrackingSheet = workbook.createSheet("TeamTracking");
        createTeamTrackingSheet(teamTrackingList, teamTrackingSheet, headerCellStyle);

        Sheet activityTrackingSheet = workbook.createSheet("ActivityTracking");
        createActivityTrackingSheet(activityTrackingList, activityTrackingSheet, headerCellStyle);

        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void createUserTrackingSheet(List<UserTracking> list, Sheet sheet,
        CellStyle headerCellStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"userId", "userEmail", "registrationDate", "unRegistrationDate",
            "intraId", "ftOAuthRegistered", "peerMemberDate", "accumulatedWallet",
            "monthlyAccumulatedWallet", "status", "reportCount", "createdAt", "updatedAt"};

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerCellStyle);
        }

        int idx = 1;
        for (UserTracking userTracking : list) {
            int cellIndex = 1;
            Row bodyRow = sheet.createRow(idx++);
            Cell bodyCell = bodyRow.createCell(0);
            bodyCell.setCellValue(userTracking.getUserId());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getUserEmail());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getRegistrationDate().toString());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (userTracking.getUnRegistrationDate() != null) {
                bodyCell.setCellValue(userTracking.getUnRegistrationDate().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getIntraId());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.isFtOAuthRegistered());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getPeerMemberDate());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getAccumulatedWallet());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getMonthlyAccumulatedWallet());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getStatus().getValue());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(userTracking.getReportCount());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (userTracking.getCreatedAt() != null) {
                bodyCell.setCellValue(userTracking.getCreatedAt().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (userTracking.getUpdatedAt() != null) {
                bodyCell.setCellValue(userTracking.getUpdatedAt().toString());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (sheet.getColumnWidth(i) + 1024));
        }
    }

    private void createTeamTrackingSheet(List<TeamTracking> list, Sheet sheet,
        CellStyle headerCellStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"teamId", "teamName", "actionDate", "actionType",
            "tag", "actionFinishedDate", "actionUnproperFinishedDate", "teamStatus",
            "42Subject", "In-42", "createdAt", "updatedAt"};

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerCellStyle);
        }

        int idx = 1;
        for (TeamTracking teamTracking : list) {
            int cellIndex = 1;
            Row bodyRow = sheet.createRow(idx++);
            Cell bodyCell = bodyRow.createCell(0);
            bodyCell.setCellValue(teamTracking.getTeamId());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getTeamName());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getActionDate().toString());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getActionType().getValue());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getTag());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (teamTracking.getActionFinishedDate() != null) {
                bodyCell.setCellValue(teamTracking.getActionFinishedDate().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (teamTracking.getActionUnproperFinishedDate() != null) {
                bodyCell.setCellValue(teamTracking.getActionUnproperFinishedDate().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getTeamStatus().getValue());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.isFtSubject());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(teamTracking.getIn42());
            if (teamTracking.getCreatedAt() != null) {
                bodyCell.setCellValue(teamTracking.getCreatedAt().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (teamTracking.getUpdatedAt() != null) {
                bodyCell.setCellValue(teamTracking.getUpdatedAt().toString());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (sheet.getColumnWidth(i) + 1024));
        }
    }

    private void createActivityTrackingSheet(List<ActivityTracking> list, Sheet sheet,
        CellStyle headerCellStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"actId", "userId", "intraId", "registeredTeamId",
            "teamType", "actionType", "toolboxSubKey", "actDate",
            "wallet", "handled", "createdAt", "updatedAt"};

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerCellStyle);
        }

        int idx = 1;
        for (ActivityTracking activityTracking : list) {
            int cellIndex = 1;
            Row bodyRow = sheet.createRow(idx++);
            Cell bodyCell = bodyRow.createCell(0);
            bodyCell.setCellValue(activityTracking.getActId());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(activityTracking.getUserId());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(activityTracking.getIntraId());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getRegisteredTeamId() != null) {
                bodyCell.setCellValue(activityTracking.getRegisteredTeamId());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getTeamType() != null) {
                bodyCell.setCellValue(activityTracking.getTeamType().getValue());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getActionType() != null) {
                bodyCell.setCellValue(activityTracking.getActionType().getValue());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(activityTracking.getToolboxSubKey());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getActDate() != null) {
                bodyCell.setCellValue(activityTracking.getActDate().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(activityTracking.getWallet());
            bodyCell = bodyRow.createCell(cellIndex++);
            bodyCell.setCellValue(activityTracking.isHandled());
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getCreatedAt() != null) {
                bodyCell.setCellValue(activityTracking.getCreatedAt().toString());
            }
            bodyCell = bodyRow.createCell(cellIndex++);
            if (activityTracking.getUpdatedAt() != null) {
                bodyCell.setCellValue(activityTracking.getUpdatedAt().toString());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (sheet.getColumnWidth(i) + 1024));
        }
    }
}
