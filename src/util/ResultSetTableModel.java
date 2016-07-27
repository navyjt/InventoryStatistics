package util;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel
{  
	private static final long serialVersionUID = 1L;
	private ResultSet rs;
	private ResultSetMetaData rsmd;
	private boolean isCellEditable;
	//构造器，初始化rs和rsmd两个属性
	public ResultSetTableModel(ResultSet aResultSet,Boolean isEditable)
	{
		isCellEditable = isEditable;
		rs = aResultSet;
		try
		{  
			rsmd = rs.getMetaData();
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
		}
	}

	//重写getColumnName方法，用于为该TableModel设置列名
	public String getColumnName(int c)
	{  
		try
		{  
			return rsmd.getColumnName(c + 1);
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
			return "";
		}
	}
	//重写getColumnCount方法，用于设置该TableModel的列数
	public int getColumnCount()
	{  
		try
		{  
			return rsmd.getColumnCount();
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
			return 0;
		}
	}
	//重写getValueAt方法，用于设置该TableModel指定单元格的值
	public Object getValueAt(int r, int c)
	{  
		try
		{  
			rs.absolute(r + 1);
			return rs.getObject(c + 1);
		}
		catch(SQLException e)
		{  
			e.printStackTrace();
			return null;
		}
	}
	//重写getColumnCount方法，用于设置该TableModel的行数
	public int getRowCount()
	{  
		try
		{  
			rs.last();
			return rs.getRow();
		}
		catch(SQLException e)
		{  
			e.printStackTrace();
			return 0;
		}
	}
	//重写isCellEditable返回true，让每个单元格可编辑，此代码中设置为不可修改
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		if(isCellEditable)
			return true;
		else
		{
			//这个对话框不需要进行提示，影响用户体验@20150416
			//JOptionPane.showMessageDialog(null, "该表格不允许进行修改！", "警告", JOptionPane.INFORMATION_MESSAGE); 
			return false;
		}
	
	}
	//重写setValueAt方法，用于实现用户编辑单元格时，程序做出对应的动作
	public void setValueAt(Object aValue,
		int row,int column)
	{
		try
		{
			//结果集定位到对应的行数
			rs.absolute(row + 1);
			//修改单元格多对应的值
			rs.updateObject(column + 1 , aValue);
	
			//提交修改
			rs.updateRow();
			//触发单元格的修改事件
			fireTableCellUpdated(row, column);
		}
		catch (SQLException  evt)
		{
			evt.printStackTrace();
		}
	}
	
}