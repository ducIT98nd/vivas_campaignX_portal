package com.vivas.campaignx.export;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.CampaignManagerDto;
import com.vivas.campaignx.dto.CmsReportByCampTypeCampCodeDto;
import com.vivas.campaignx.dto.UserDTO;
import com.vivas.campaignx.entity.CmsReportByCampTypeCampCode;
import com.vivas.campaignx.entity.CmsReportByCampTypeCampCodePackage;
import com.vivas.campaignx.entity.TargetGroup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelFileExporter {
    public static ByteArrayInputStream tagetGroupListToExcelFile(List<TargetGroup> data) {

        String pattern = "dd/MM/yyyy";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh_sach_nhom_doi_tuong");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Tên nhóm đối tượng");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Mô tả");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Số lượng thuê bao");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Người tạo");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Ngày tạo");
            cell.setCellStyle(headerCellStyle);
            // Creating data rows for each customer
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(i + 1);
                dataRow.createCell(1).setCellValue(data.get(i).getName());
                dataRow.createCell(2).setCellValue(data.get(i).getDescription());
                if (data.get(i).getQuantityMsisdn() == null) {
                    dataRow.createCell(3).setCellValue("");
                } else {
                    dataRow.createCell(3).setCellValue(data.get(i).getQuantityMsisdn());
                }
                dataRow.createCell(4).setCellValue(data.get(i).getCreatedUser());
                if (data.get(i).getCreatedDate() != null) {
                    dataRow.createCell(5).setCellValue(AppUtils.convertDateToString(data.get(i).getCreatedDate(), pattern));
                } else {
                    dataRow.createCell(5).setCellValue("");
                }
            }
            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            sheet.setColumnWidth(0, 1000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(4, 7500);
            sheet.setColumnWidth(5, 3500);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ByteArrayInputStream userListToExcelFile(List<UserDTO> data) {

        String pattern = "dd/MM/yyyy";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh_sach_tai khoan");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Tên đăng nhập");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Tên người dùng");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Vai trò");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Email");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Trạng thái");
            cell.setCellStyle(headerCellStyle);
            // Creating data rows for each customer
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(i + 1);
                dataRow.createCell(1).setCellValue(data.get(i).getUsername());
                dataRow.createCell(2).setCellValue(data.get(i).getName());
                dataRow.createCell(3).setCellValue(data.get(i).getRoleName());
                dataRow.createCell(4).setCellValue(data.get(i).getEmail());
                if (data.get(i).getStatus() == 1) {
                    dataRow.createCell(5).setCellValue("Hoạt động");
                } else {
                    dataRow.createCell(5).setCellValue("Khóa");
                }

            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ByteArrayInputStream campaignListToExcelFile(List<CampaignManagerDto> data) {

        String pattern = "dd/MM/yyyy";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh_sach_chien_dich");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Tên chiến dịch");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Loại chiến dịch");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Trạng thái");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Ngày bắt đầu");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Ngày kết thúc");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Người tạo");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Ngày tạo");
            cell.setCellStyle(headerCellStyle);
            // Creating data rows for each customer
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(i + 1);
                dataRow.createCell(1).setCellValue(data.get(i).getName());
                if (data.get(i).getType() == 1) {
                    dataRow.createCell(2).setCellValue("Sự kiện");
                } else dataRow.createCell(2).setCellValue("Tấn suất");
                if (data.get(i).getStatus() == 0) {
                    dataRow.createCell(3).setCellValue("Đang chạy");
                } else if (data.get(i).getStatus() == 1) {
                    dataRow.createCell(3).setCellValue("Đang xử lý");
                } else if (data.get(i).getStatus() == 2) {
                    dataRow.createCell(3).setCellValue("Chờ phê duyệt");
                } else if (data.get(i).getStatus() == 3) {
                    dataRow.createCell(3).setCellValue("Phê duyệt");
                } else if (data.get(i).getStatus() == 4) {
                    dataRow.createCell(3).setCellValue("Kết thúc");
                } else if (data.get(i).getStatus() == 5) {
                    dataRow.createCell(3).setCellValue("Từ chối");
                } else if (data.get(i).getStatus() == 6) {
                    dataRow.createCell(3).setCellValue("Tạm dừng");
                } else if (data.get(i).getStatus() == 7) {
                    dataRow.createCell(3).setCellValue("Hoàn thành");
                }
                dataRow.createCell(4).setCellValue(data.get(i).getStartDate());
                dataRow.createCell(5).setCellValue(data.get(i).getEndDate());
                dataRow.createCell(6).setCellValue(data.get(i).getUserName());
                dataRow.createCell(7).setCellValue(data.get(i).getCreatedDate());
            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ByteArrayInputStream reportListToExcelFile(List<CmsReportByCampTypeCampCode> data, CmsReportByCampTypeCampCodeDto dataSum) {

        String pattern = "dd/MM/yyyy";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bao_cao_tong_quan");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("Ngày");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Số tin mời(1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Tin gửi thành công (2)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Tỷ lệ gửi tin thành công (3) (3=2/1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Số lượt đăng ký (4)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Tỷ lệ thuê bao tương tác (5) (5=4/1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Đăng ký thành công (6)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Tỷ lệ thuê bao đăng ký thành công (7) (7=6/4)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(8);
            cell.setCellValue("Doanh thu");
            cell.setCellStyle(headerCellStyle);
            // Creating data rows for each customer
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                if (i == 0) {
                    Float cell3 = (float) dataSum.getSUM_SUCCESS_MESSAGE() / dataSum.getSUM_INVITATION_MESSAGE();
                    Float cell5 = (float) dataSum.getSUM_REGISTER_MESSAGE() / dataSum.getSUM_INVITATION_MESSAGE();
                    Float cell7 = (float) dataSum.getSUM_SUCCESS_REGISTER_MESSAGE() / dataSum.getSUM_REGISTER_MESSAGE();
                    dataRow.createCell(0).setCellValue("Tổng");
                    dataRow.createCell(1).setCellValue(dataSum.getSUM_INVITATION_MESSAGE());
                    dataRow.createCell(2).setCellValue(dataSum.getSUM_SUCCESS_MESSAGE());
                    dataRow.createCell(3).setCellValue(cell3 * 100 +"%");
                    dataRow.createCell(4).setCellValue(dataSum.getSUM_REGISTER_MESSAGE());
                    dataRow.createCell(5).setCellValue(cell5 * 100 +"%");
                    dataRow.createCell(6).setCellValue(dataSum.getSUM_SUCCESS_REGISTER_MESSAGE());
                    dataRow.createCell(7).setCellValue(cell7 * 100 +"%");
                    dataRow.createCell(8).setCellValue(dataSum.getSUM_REVENUE());
                } else {
                    Float cell3 = 0F;
                    Float cell5 = 0F;
                    Float cell7 = 0F;
                    if (data.get(i).getSuccessMessage() != null && data.get(i).getInvitationMessage() != null) {
                        cell3 = (float) data.get(i).getSuccessMessage() / data.get(i).getInvitationMessage();
                    }
                    if (data.get(i).getRegisterMessage() != null && data.get(i).getInvitationMessage() != null) {
                        cell5 = (float) data.get(i).getRegisterMessage() / data.get(i).getInvitationMessage();
                    }
                    if (data.get(i).getSuccessRegisterMessage() != null && data.get(i).getRegisterMessage() != null) {
                        cell7 = (float) data.get(i).getSuccessRegisterMessage() / data.get(i).getRegisterMessage();
                    }
                    dataRow.createCell(0).setCellValue(AppUtils.convertDateToString(data.get(i).getReportedDate(), "dd-MM-yyyy"));
                    if (data.get(i).getInvitationMessage() != null) {
                        dataRow.createCell(1).setCellValue(data.get(i).getInvitationMessage());
                    } else dataRow.createCell(1).setCellValue(0);
                    if (data.get(i).getSuccessMessage() != null) {
                        dataRow.createCell(2).setCellValue(data.get(i).getSuccessMessage());
                    } else dataRow.createCell(2).setCellValue(0);
                    dataRow.createCell(3).setCellValue(cell3 * 100 +"%");
                    if (data.get(i).getRegisterMessage() != null) {
                        dataRow.createCell(4).setCellValue(data.get(i).getRegisterMessage());
                    } else dataRow.createCell(4).setCellValue(0);
                    dataRow.createCell(5).setCellValue(cell5 * 100 +"%");
                    if (data.get(i).getSuccessRegisterMessage() != null) {
                        dataRow.createCell(6).setCellValue(data.get(i).getSuccessRegisterMessage());
                    } else dataRow.createCell(6).setCellValue(0);
                    dataRow.createCell(7).setCellValue(cell7 * 100 +"%");
                    dataRow.createCell(8).setCellValue(data.get(i).getRevenue());
                    //               }
                }
            }
            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);

            sheet.setColumnWidth(0, 1000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(4, 7500);
            sheet.setColumnWidth(5, 3500);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ByteArrayInputStream reportPackageToExcelFile(List<CmsReportByCampTypeCampCodePackage> data, CmsReportByCampTypeCampCodeDto dataSum) {

        String pattern = "dd/MM/yyyy";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SMS");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("Ngày");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Số tin mời(1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Tin gửi thành công (2)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Tỷ lệ gửi tin thành công (3) (3=2/1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Số lượt đăng ký (4)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Tỷ lệ thuê bao tương tác (5) (5=4/1)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Đăng ký thành công (6)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Tỷ lệ thuê bao đăng ký thành công (7) (7=6/4)");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(8);
            cell.setCellValue("Doanh thu");
            cell.setCellStyle(headerCellStyle);
            // Creating data rows for each customer
            for (int i = 0; i < data.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                if (i == 0) {
                    Float cell3 = ((float) dataSum.getSUM_SUCCESS_MESSAGE() / dataSum.getSUM_INVITATION_MESSAGE()) * 100;
                    Float cell5 = ((float) dataSum.getSUM_REGISTER_MESSAGE() / dataSum.getSUM_INVITATION_MESSAGE()) * 100;
                    Float cell7 = ((float) dataSum.getSUM_SUCCESS_REGISTER_MESSAGE() / dataSum.getSUM_REGISTER_MESSAGE()) *100;
                    dataRow.createCell(0).setCellValue("Tổng");
                    dataRow.createCell(1).setCellValue(dataSum.getSUM_INVITATION_MESSAGE());
                    dataRow.createCell(2).setCellValue(dataSum.getSUM_SUCCESS_MESSAGE());
                    dataRow.createCell(3).setCellValue(cell3+"%");
                    dataRow.createCell(4).setCellValue(dataSum.getSUM_REGISTER_MESSAGE());
                    dataRow.createCell(5).setCellValue(cell5+"%");
                    dataRow.createCell(6).setCellValue(dataSum.getSUM_SUCCESS_REGISTER_MESSAGE());
                    dataRow.createCell(7).setCellValue(cell7+"%");
                    dataRow.createCell(8).setCellValue(0);
                } else {
                    Float cell3 = 0F;
                    Float cell5 = 0F;
                    Float cell7 = 0F;
                    if (data.get(i).getSuccessMessage() != null && data.get(i).getInvitationMessage() != null) {
                        cell3 = ((float) data.get(i).getSuccessMessage() / data.get(i).getInvitationMessage()) * 100;
                    }
                    if (data.get(i).getRegisterMessage() != null && data.get(i).getInvitationMessage() != null) {
                        cell5 = ((float) data.get(i).getRegisterMessage() / data.get(i).getInvitationMessage()) * 100;
                    }
                    if (data.get(i).getSuccessRegisterMessage() != null && data.get(i).getRegisterMessage() != null) {
                        cell7 = ((float) data.get(i).getSuccessRegisterMessage() / data.get(i).getRegisterMessage()) * 100;
                    }
                    dataRow.createCell(0).setCellValue(AppUtils.convertDateToString(data.get(i).getReportedDate(), "dd-MM-yyyy"));
                    if (data.get(i).getInvitationMessage() != null) {
                        dataRow.createCell(1).setCellValue(data.get(i).getInvitationMessage());
                    } else dataRow.createCell(1).setCellValue(0);
                    if (data.get(i).getSuccessMessage() != null) {
                        dataRow.createCell(2).setCellValue(data.get(i).getSuccessMessage());
                    } else dataRow.createCell(2).setCellValue(0);
                    dataRow.createCell(3).setCellValue(cell3+"%");
                    if (data.get(i).getRegisterMessage() != null) {
                        dataRow.createCell(4).setCellValue(data.get(i).getRegisterMessage());
                    } else dataRow.createCell(4).setCellValue(0);
                    dataRow.createCell(5).setCellValue(cell5 +"%");
                    if (data.get(i).getSuccessRegisterMessage() != null) {
                        dataRow.createCell(6).setCellValue(data.get(i).getSuccessRegisterMessage());
                    } else dataRow.createCell(6).setCellValue(0);
                    dataRow.createCell(7).setCellValue(cell7 +"%");
                    dataRow.createCell(8).setCellValue(0);
                    //               }
                }
            }
            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);

            sheet.setColumnWidth(0, 1000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(4, 7500);
            sheet.setColumnWidth(5, 3500);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
