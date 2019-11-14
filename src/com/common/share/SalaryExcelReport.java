package com.common.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Session;

public class SalaryExcelReport
{

	SessionBean sessionBean;
	LinkedHashMap<String, Object> rowMap=new LinkedHashMap<String, Object>();
	int i=0;
	int rownum=0;
	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yy");
	@SuppressWarnings("unused")
	private String loc,url,fname,sheetName;

	public SalaryExcelReport(SessionBean sessionbean, String loc, String url, String fname, String header[], File outFile, String sheetName, 
			String ReportName, int GroupRowHeight, String [] GroupHeader, int DetailRowHeight, String [] DetailQuery, int rowWidth,
			int rowStartFrom,String [] SignatureOption)
	{
		this.sessionBean=sessionbean;
		this.loc=loc;
		this.url=url;
		this.fname=fname;
		this.sheetName=sheetName;
		generateExcelFile(ReportName, header, GroupRowHeight, GroupHeader, DetailRowHeight, DetailQuery, outFile, rowWidth, rowStartFrom, SignatureOption);
	}
	public void generateExcelFile(String ReportName, String header[], int GroupRowHeight, String [] GroupHeader,
			int DetailRowHeight, String [] DetailQuery, File outFile, int rowWidth, int rowStartFrom, String [] SignatureOption)
	{
		System.out.println("GenerateExcelReport first Step");
		try
		{
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(outFile));
			HSSFSheet spreadsheet = workbook.getSheetAt(0);
			HSSFDataFormat dataFormat=workbook.createDataFormat();
			spreadsheet.setAutobreaks(false);
			spreadsheet.setFitToPage(true);
			spreadsheet.setZoom(100);

			System.out.println("GenerateExcelReport Second Step");

			HSSFFont font=workbook.createFont();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)14.0);
			font.setFontName("Arrial Narrow");
			font.setBold(true);

			Row row=spreadsheet.createRow(0);
			CellStyle style2 = workbook.createCellStyle();
			Cell cellCompany=row.createCell(0);
			cellCompany.setCellValue(sessionBean.getCompany());
			style2.setFont(font);
			style2.setAlignment(CellStyle.ALIGN_CENTER);
			style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellCompany.setCellStyle(style2);
			//spreadsheet.addMergedRegion(new CellRangeAddress(0,1,0,TableHeader.length-1));

			System.out.println("GenerateExcelReport Third Step");

			row=spreadsheet.createRow(1);
			CellStyle style3 = workbook.createCellStyle();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)10.0);
			font.setFontName("Arrial Narrow");
			font.setBold(true);
			Cell cellAddress=row.createCell(0);
			cellAddress.setCellValue(sessionBean.getCompanyAddress());
			style3.setFont(font);
			style3.setAlignment(CellStyle.ALIGN_CENTER);
			style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellAddress.setCellStyle(style3);
			//spreadsheet.addMergedRegion(new CellRangeAddress(2,2,0,TableHeader.length-1));

			System.out.println("GenerateExcelReport Fourth Step");

			row=spreadsheet.createRow(2);
			CellStyle style4 = workbook.createCellStyle();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)9.0);
			font.setFontName("Arrial Narrow");
			font.setBold(true);
			Cell cellContact=row.createCell(0);
			cellContact.setCellValue(sessionBean.getCompanyContact());
			style4.setFont(font);
			style4.setAlignment(CellStyle.ALIGN_CENTER);
			style4.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellContact.setCellStyle(style4);

			System.out.println("GenerateExcelReport Fifth Step");

			row=spreadsheet.createRow(3);
			CellStyle style8 = workbook.createCellStyle();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)12.0);
			font.setFontName("Arrial Narrow");
			font.setBold(true);
			Cell cellReportName=row.createCell(0);
			cellReportName.setCellValue(ReportName);
			style8.setFont(font);
			style8.setAlignment(CellStyle.ALIGN_CENTER);
			style8.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellReportName.setCellStyle(style8);

			if(header.length>0)
			{
				for(int index=0;index<header.length;index++)
				{
					if(header[index].length()>0)
					{
						row=spreadsheet.createRow(4+index);
						CellStyle style11 = workbook.createCellStyle();
						font=workbook.createFont();
						font.setFontHeightInPoints((short)10.0);
						font.setFontName("Arrial Narrow");
						font.setBold(true);
						Cell cellHeader=row.createCell(0);
						cellHeader.setCellValue(header[index]);
						style11.setFont(font);
						style11.setAlignment(CellStyle.ALIGN_CENTER);
						style11.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
						cellHeader.setCellStyle(style11);
					}
				}
			}
			font=workbook.createFont();
			font.setFontHeightInPoints((short)9.0);
			font.setFontName("Arrial Narrow");
			font.setBold(true);

			excelReport(DetailQuery[0]);

			i=1;
			/*rownum = rowStartFrom;
			CellStyle style12=workbook.createCellStyle();
			style12.setWrapText(true);

			if(GroupHeader.length>0)
			{
				for(int ind=0;ind<GroupHeader.length;ind++)
				{
					rowMap.put("#"+Integer.toString(i),GroupHeader);
					row=spreadsheet.createRow(rownum);
					font=workbook.createFont();
					font.setFontHeightInPoints((short)10.0);
					font.setFontName("Arrial Narrow");
					font.setBold(true);
					Cell groupCell=row.createCell(ind);
					row.setHeightInPoints((GroupRowHeight*spreadsheet.getDefaultRowHeightInPoints()));
					groupCell.setCellValue(GroupHeader[ind]);
					style12.setFillPattern(CellStyle.SOLID_FOREGROUND);
					style12.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
					for(int cellIndex=1;cellIndex<rowWidth;cellIndex++)
					{
						groupCell=row.createCell(cellIndex);
						style12.setFillPattern(CellStyle.SOLID_FOREGROUND);
						style12.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
					}
					style12.setFont(font);
					style12.setAlignment(CellStyle.ALIGN_CENTER);
					style12.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					groupCell.setCellStyle(style12);
					rownum++;
					i++;

					excelReport(DetailQuery[ind]);
				}
			}
			else
			{
				excelReport(DetailQuery[0]);
			}*/

			rownum = rowStartFrom;

			Set<String> keySet=rowMap.keySet();
			CellStyle style5=workbook.createCellStyle();
			style5.setWrapText(true);
			int indexOfTotal[]=new int[rowWidth];
			int cellIndex=0;
			for (String key : keySet) 
			{
				if(!key.contains("#"))
				{
					row = spreadsheet.createRow(rownum);
					Object [] objArr = (Object[])rowMap.get(key);
					int cellnum = 0;
					int countCell=0;
					for (Object obj : objArr) 
					{
						Cell cell = row.createCell(cellnum);
						if(obj instanceof Date)
							cell.setCellValue(dFormat.format(obj));
						else if(obj instanceof Boolean)
							cell.setCellValue((Boolean)obj);
						else if(obj instanceof String)
							cell.setCellValue((String)obj);
						else if(obj instanceof Double)
						{
							cell.setCellValue((Double)obj);
							style5.setDataFormat(dataFormat.getFormat("#,##0"));
							if(rownum==rowStartFrom)
								indexOfTotal[countCell++]=cellnum;
						}
						else if(obj instanceof Integer)
							cell.setCellValue((Integer)obj);

						style5.setBorderBottom(CellStyle.BORDER_THIN);
						style5.setBottomBorderColor(IndexedColors.BLACK.getIndex());
						style5.setBorderLeft(CellStyle.BORDER_THIN);
						style5.setLeftBorderColor(IndexedColors.BLACK.getIndex());
						style5.setBorderRight(CellStyle.BORDER_THIN);
						style5.setRightBorderColor(IndexedColors.BLACK.getIndex());
						style5.setBorderTop(CellStyle.BORDER_THIN);
						style5.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style5.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

						cell.setCellStyle(style5);
						cellnum++;
						if(rownum==rowStartFrom)
							cellIndex=cellnum;
					}
					row.setHeightInPoints((DetailRowHeight*spreadsheet.getDefaultRowHeightInPoints()));
				}
				rownum++;
			}
			rowMap.clear();
			System.out.println("GenerateExcelReport Ninth Step");
			if(indexOfTotal[0]>0)
			{
				CellStyle style9=workbook.createCellStyle();
				row=spreadsheet.createRow(rownum);
				Cell cell=row.createCell(indexOfTotal[0]-1);
				cell.setCellValue("Total : ");
				style9.setAlignment(CellStyle.ALIGN_RIGHT);
				style9.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style9.setFont(font);
				cell.setCellStyle(style9);
				spreadsheet.autoSizeColumn(indexOfTotal[0]-1);
				CellStyle style10=workbook.createCellStyle();
				font=workbook.createFont();
				font.setFontHeightInPoints((short)8.0);
				font.setFontName("Arrial Narrow");
				font.setBold(true);
				for(int colInd=0;colInd<rowWidth;colInd++)
				{
					if(indexOfTotal[colInd]>0)
					{
						cell=row.createCell(indexOfTotal[colInd]);
						cell.setCellFormula("SUM("+Character.toString((char)(65+indexOfTotal[colInd]))+Integer.toString(rowStartFrom+1)+":"+Character.toString((char)(65+indexOfTotal[colInd]))+Integer.toString(rownum)+")");
						style10.setDataFormat(dataFormat.getFormat("#,##0"));
						style10.setBorderBottom(CellStyle.BORDER_DOUBLE);
						style10.setBottomBorderColor(IndexedColors.BLACK.getIndex());
						style10.setAlignment(CellStyle.ALIGN_RIGHT);
						style10.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
						style10.setFont(font);
						cell.setCellStyle(style10);
						row.setHeightInPoints((DetailRowHeight*spreadsheet.getDefaultRowHeightInPoints()));
						//spreadsheet.autoSizeColumn(indexOfTotal[colInd]);
					}
					else
						break;
				}
			}

			if(SignatureOption.length>0)
			{
				CellStyle style11=workbook.createCellStyle();
				int startCol = 2;
				row=spreadsheet.createRow(rownum+6);
				Cell cell=row.createCell(startCol);
				cell.setCellValue("Total : ");
				style11.setAlignment(CellStyle.ALIGN_RIGHT);
				style11.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style11.setFont(font);
				cell.setCellStyle(style11);
				spreadsheet.autoSizeColumn(indexOfTotal[0]-1);
				CellStyle style12=workbook.createCellStyle();
				for(int colInd=0;colInd<SignatureOption.length;colInd++)
				{
					cell=row.createCell(startCol);
					cell.setCellValue(SignatureOption[colInd]);
					style12.setBorderTop(CellStyle.BORDER_THIN);
					style12.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style12.setAlignment(CellStyle.ALIGN_RIGHT);
					style12.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					style12.setFont(font);
					cell.setCellStyle(style12);
					startCol+=Math.ceil(cellIndex/SignatureOption.length);
				}
			}

			System.out.println("GenerateExcelReport Tenth Step");

			File file=new File(loc+"/"+fname);
			FileOutputStream out=new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
		}

		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception exp)
		{
			System.out.println(exp.toString());
		}
	}

	public void excelReport(String detailedQuery)
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		List <?> lst=session.createSQLQuery(detailedQuery).list();

		int countSL=1;

		for(Iterator <?> itr=lst.iterator();itr.hasNext();)
		{
			Object [] element=(Object[])itr.next();
			Object[] excelElement=new Object[element.length+1];
			excelElement[0]=(Object)countSL++;
			for(int elInd=1;elInd<=element.length;elInd++)
			{
				excelElement[elInd]=element[elInd-1];
			}
			rowMap.put(Integer.toString(i),excelElement);
			i++;
		}
		session.close();
	}
}
