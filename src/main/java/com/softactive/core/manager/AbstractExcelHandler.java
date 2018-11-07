package com.softactive.core.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;

import com.softactive.core.exception.MyException;

public abstract class AbstractExcelHandler<OUTPUT_COMPONENT>
		extends AbstractHandler<String, Workbook, Sheet, Row, OUTPUT_COMPONENT, List<OUTPUT_COMPONENT>> {
	
	public AbstractExcelHandler(Map<String, Object> sharedParams) {
		super(sharedParams);
	}

	private static final long serialVersionUID = 1702240267667438881L;
	protected int dateIndex;
	protected int regionCodeIndex;

	private FileInputStream getFile(String fileName) throws FileNotFoundException {
		return new FileInputStream(new File(fileName));
	}

	protected Double getDoubleValue(Cell c) {
		CellType type = c.getCellTypeEnum();
		switch (type) {
		case STRING:
			String s = c.getStringCellValue();
			if (s != null && s.length() > 0) {
				try {
					return Double.valueOf(s);
				} catch (NumberFormatException e) {
					return null;
				}
			} else {
				return null;
			}
		case NUMERIC:
			return c.getNumericCellValue();
		case BLANK:
			return null;
		default:
			System.out.println("Cell type is strange: " + type);
			return null;
		}
	}

	protected LocalDate resolveDate(String dateString) {
		return LocalDate.parse(dateString);
	};

	@Override
	protected Workbook parsedInput(String fileName) {
		try {
			FileInputStream excelFile = getFile(fileName);
			return new XSSFWorkbook(excelFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected abstract int getArrayStartIndex();

	@Override
	protected Sheet inputBody(Workbook r) {
		return r.getSheetAt(getSheetIndex(r));
	}

	protected int getSheetIndex(Workbook r) {
		return r.getSheetIndex(getSheetName());
	}
	
	protected String getSheetName() {
		return null;
	};

	@Override
	protected List<OUTPUT_COMPONENT> output(Sheet array) {
		List<OUTPUT_COMPONENT> list = new ArrayList<>();
		int startIndex = getArrayStartIndex();
		int endIndex = array.getLastRowNum() + 1;
		int count = endIndex - startIndex;
		System.out.println("start index: " + startIndex);
		System.out.println("end index: " + endIndex);
		for (int i = startIndex; i < endIndex; i++) {
			OUTPUT_COMPONENT additional = null;
			try {
				additional = outputComponent(array.getRow(i));
			} catch (MyException e) {
				System.out.println(e);
//				MyError er = new MyError(1, e.getMsg());
//				sharedParams.put(PARAM_ERROR, er);
			}
			if (additional != null) {
				list.add(additional);
			}
			setProgress(100 * i / count);
		}
		return list;
	}
}
