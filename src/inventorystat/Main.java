/**
 * Description:
 * <br/>Copyright (C)
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:备品备件库存量查询系统
 * <br/>Date: 2016-07-09
 * @author  姜涛 	navyjt@163.com
 * @version  1.0
 * 
 * @20160726更改逻辑，下拉列表使用实时读取数据库的方式，这样在子菜单对话框里对专业类别以及存放地点等的修改
 * 可以实时提现到主界面下拉列表里。
 */

package inventorystat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import util.ConfigFile;
import util.ResultSetTableModel;
import util.UI;
import util.WriteLog;

public class Main {

	public JFrame f = new JFrame("板件库存量统计");
	public JToolBar tb;
	public JLabel tbLabel;
	private ResultSet rs;
	private Connection conn;
	private Statement querystmt;
	private Statement stmt;
	private ResultSet resultrs;
	private JPanel resulttopPanel = new JPanel();
	private JPanel resultPanel = new JPanel();
	private ResultSetTableModel model;
	private JScrollPane resultScrollPane;
	private JButton query;

	private JButton exportexcelButton;
	// 类别，用来标注各大专业，比如交换、传输等
	private JComboBox<String> categoryComboBox;
	// 装备名称
	private JComboBox<String> equipmentComboBox;
	// 放置地点
	private JComboBox<String> locationComboBox;
	// 板件名称
	private JComboBox<String> repairpieceComboBox;

	JMenuBar mbMenuBar;
	JMenu configMenu;
	JMenu newfileMenu;
	JMenuItem addRepairpieceRecordItem;
	JMenuItem updateRepairpieceItem;
	JMenuItem subjectItem;
	JMenuItem equipmentItem;
	JMenuItem locationItem;
	JMenuItem repairpieceItem;
	JMenuItem logItem;
	JMenuItem exitItem;

	public void init() {
		UI.start();

		query = new JButton("开始查询");
		query.setPreferredSize(new Dimension(120, 25));

		exportexcelButton = new JButton("导出至Excel");
		exportexcelButton.setPreferredSize(new Dimension(120, 25));
		// 类别，用来标注各大专业，比如交换、传输等
		categoryComboBox = new JComboBox<String>();
		// 装备名称
		equipmentComboBox = new JComboBox<String>();
		// 放置地点
		locationComboBox = new JComboBox<String>();
		// 板件名称
		repairpieceComboBox = new JComboBox<String>();

		// 创建一个状态栏
		tb = new JToolBar();
		tbLabel = new JLabel("库存量统计软件初始化完成");
		tb.add(tbLabel);
		tb.setFloatable(false);
		f.add(tb, BorderLayout.SOUTH);

		// 给ComboBox增加预选项
		categoryComboBox.addItem("----选择专业类别----");
		categoryComboBox.setPreferredSize(new Dimension(150, 25));
		categoryComboBox.setVisible(true);

		equipmentComboBox.addItem("----选择设备名称----");
		equipmentComboBox.setPreferredSize(new Dimension(150, 25));
		equipmentComboBox.setVisible(true);

		locationComboBox.addItem("----选择存放地点----");
		locationComboBox.setPreferredSize(new Dimension(150, 25));
		locationComboBox.setVisible(true);

		repairpieceComboBox.addItem("----选择板件名称----");
		repairpieceComboBox.setPreferredSize(new Dimension(150, 25));
		repairpieceComboBox.setVisible(true);

		// 在页面顶部的panel里，选择查询的基本选项
		resulttopPanel.add(categoryComboBox);
		resulttopPanel.add(equipmentComboBox);
		resulttopPanel.add(locationComboBox);
		resulttopPanel.add(repairpieceComboBox);
		resulttopPanel.add(query);
		resulttopPanel.add(exportexcelButton);

		// 初始化菜单
		initmenu();

		// 为f窗口设置菜单条
		f.setJMenuBar(mbMenuBar);

		initcombox();

		exportexcelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (resultrs != null) {
					dB2Excel(resultrs);
				} else
					JOptionPane.showMessageDialog(null, "无查询结果！", "警告", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		query.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startquery();
			}
		});

		resultPanel.add(resulttopPanel, BorderLayout.NORTH);
		f.add(resultPanel, BorderLayout.CENTER);

		// 确保窗体启动时最大化
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		int taskBarHeight = toolkit.getScreenInsets(f.getGraphicsConfiguration()).bottom;
		f.setBounds(0, 0, (int) (scmSize.getWidth()), (int) ((scmSize.getHeight()) - taskBarHeight));
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.setResizable(false);
		f.validate();

		// 设置关闭窗口时，退出程序
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	protected boolean dB2Excel(ResultSet resultrs) {
		boolean flag = false;
		WritableWorkbook workbook = null;
		WritableSheet sheet = null;
		Label label = null;
		FileOutputStream fos;

		// 创建Excel表
		try {
			fos = new FileOutputStream("库存统计.xls");
			workbook = Workbook.createWorkbook(fos);
			// 创建Excel表中的sheet
			sheet = workbook.createSheet("库存统计", 0);
			// 向Excel中添加数据
			ResultSetMetaData rsmd = resultrs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			String colName = null;
			int row = 0;
			// String[] columnTitle;
			// 添加标题
			String[] columnTitle = { "设备名称", "板件类型", "存放地点", "板件序列号", "运行状态", "入库时间", "使用时间", "维修时间" };
			for (int i = 0; i < columnCount; i++) {
				colName = columnTitle[i];
				label = new Label(i, row, colName);
				sheet.addCell(label);
			}
			row++;
			WriteLog.WriteLogFile("写入标题成功");
			resultrs.first();
			do {
				for (int i = 0; i < columnCount; i++) {
					label = new Label(i, row, resultrs.getString(i + 1));
					sheet.addCell(label);
				}
				row++;
			} while (resultrs.next());
			WriteLog.WriteLogFile("写入内容成功");

			// 关闭文件
			workbook.write();
			workbook.close();
			WriteLog.WriteLogFile("数据成功写入Excel");
			tbLabel.setText("数据成功写入Excel");
			flag = true;
			fos.close();
		} catch (SQLException e) {
			WriteLog.WriteLogFile(e.getMessage());
		} catch (RowsExceededException e) {
			WriteLog.WriteLogFile(e.getMessage());
		} catch (WriteException e) {
			WriteLog.WriteLogFile(e.getMessage());
		} catch (IOException e) {
			WriteLog.WriteLogFile(e.getMessage());
		} finally {
			try {
				workbook.close();
			} catch (Exception e) {
			}
		}
		return flag;
	}

	private void initcombox() {
		// ===================以下是combobox下拉列表实时显示部分,通过实时查询数据库显示====================
		categoryComboBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				try {
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String query = "select category from category";
					if (rs != null) {
						rs.close();
					}
					categoryComboBox.removeAllItems();
					categoryComboBox.addItem("----选择专业类别----");
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						categoryComboBox.addItem(rs.getString(1));
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});

		locationComboBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				// 地点列表
				try {
					locationComboBox.removeAllItems();
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String query = "select location from location";
					if (rs != null) {
						rs.close();
					}
					locationComboBox.addItem("----选择库存地址----");
					rs = stmt.executeQuery(query);

					while (rs.next()) {
						locationComboBox.addItem(rs.getString(1));
					}
				} catch (Exception e) {
				}

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});

		equipmentComboBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				String selectcategory = "";
				selectcategory = categoryComboBox.getSelectedItem().toString();
				if (categoryComboBox.getSelectedIndex() != 0) {

					try {
						equipmentComboBox.removeAllItems();
						// 获取数据库连接
						conn = ConfigFile.getConnection();
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						String query = "select name from equipment WHERE category='" + selectcategory + "' ;";

						if (rs != null) {
							rs.close();
						}
						equipmentComboBox.removeAllItems();
						equipmentComboBox.addItem("----选择设备名称----");
						rs = stmt.executeQuery(query);

						while (rs.next()) {
							equipmentComboBox.addItem(rs.getString(1));
						}
					}

					catch (Exception e1) {
						e1.printStackTrace();
					}

				} else {
					JOptionPane.showMessageDialog(null, "请选择相关专业类别！", "警告", JOptionPane.INFORMATION_MESSAGE);
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});

		repairpieceComboBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				String selectequipment = equipmentComboBox.getSelectedItem().toString();
				String selectcategory = categoryComboBox.getSelectedItem().toString();

				try {
					repairpieceComboBox.removeAllItems(); // 获取数据库连接
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String query = "select piecename from repairpiece WHERE category='" + selectcategory
							+ "' and equipment='" + selectequipment + "';";

					if (rs != null) {
						rs.close();
					}
					repairpieceComboBox.removeAllItems();
					repairpieceComboBox.addItem("----选择板件名称----");
					rs = stmt.executeQuery(query);

					while (rs.next()) {
						repairpieceComboBox.addItem(rs.getString(1));
					}
				}

				catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});

	}

	private void initmenu() {

		mbMenuBar = new JMenuBar();
		newfileMenu = new JMenu("数据录入");
		configMenu = new JMenu("系统配置");

		Icon newIcon = new ImageIcon("ico/new.png");
		addRepairpieceRecordItem = new JMenuItem("增加板件记录", newIcon);

		Icon copyIcon = new ImageIcon("ico/copy.png");
		updateRepairpieceItem = new JMenuItem("修改板件记录", copyIcon);

		Icon folderIcon = new ImageIcon("ico/folder.png");
		subjectItem = new JMenuItem("专业类别配置", folderIcon);

		Icon openIcon = new ImageIcon("ico/open.png");
		equipmentItem = new JMenuItem("设备名称配置", openIcon);

		Icon diskIcon = new ImageIcon("ico/disk.png");
		locationItem = new JMenuItem("存放地点配置", diskIcon);

		Icon pasteIcon = new ImageIcon("ico/paste.png");
		repairpieceItem = new JMenuItem("板件配置", pasteIcon);

		logItem = new JMenuItem("操作日志");

		Icon exitIcon = new ImageIcon("ico/exit.png");
		exitItem = new JMenuItem("退出", exitIcon);

		newfileMenu.add(addRepairpieceRecordItem);
		newfileMenu.add(updateRepairpieceItem);

		configMenu.add(subjectItem);
		configMenu.add(locationItem);
		configMenu.add(equipmentItem);
		configMenu.add(repairpieceItem);
		configMenu.add(logItem);
		configMenu.add(exitItem);
		// 将file菜单添加到mb菜单条中
		mbMenuBar.add(newfileMenu);
		mbMenuBar.add(configMenu);

		// ================================增加子菜单===============
		// -----------下面开始组合菜单、并为菜单添加事件监听器----------
		addRepairpieceRecordItem.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
		addRepairpieceRecordItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddRepairpieceRecordDiag();
			}
		});

		updateRepairpieceItem.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.CTRL_MASK));
		updateRepairpieceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new UpdateRepairpieceItemDiag();
			}
		});

		// 为subjectItem设置快捷键，设置快捷键时要使用大写字母
		subjectItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
		subjectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddSubjcetItemDiag();
			}
		});

		// --------------设备配置，为专业类别配置的子菜单--------------
		equipmentItem.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK));
		equipmentItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new AddEquipmentItemDiag();
			}
		});

		// --------------存放地点配置，为专业类别配置的子菜单--------------
		locationItem.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK));
		locationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AddLocationItemDiag();
			}
		});

		// -------------------板件配置，为专业类别配置的子菜单--------
		repairpieceItem.setAccelerator(KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK));
		repairpieceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new AddRepairpieceItemDiag();
			}
		});
		exitItem.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

	}

	public static void main(String[] args) {
		new Main().init();
		JFrame.setDefaultLookAndFeelDecorated(false);
	}

	// 开始查询库存量
	private void startquery() {
		String queryString = "";
		String querynewStirng = "";
		if ((categoryComboBox.getSelectedIndex() == 0) && (equipmentComboBox.getSelectedIndex() == 0)
				&& (repairpieceComboBox.getSelectedIndex() == 0) && (locationComboBox.getSelectedIndex() == 0)) {

			queryString = "";
		} else {
			queryString = "where ";

			if (categoryComboBox.getSelectedIndex() != 0) {

				String strCategory = categoryComboBox.getSelectedItem().toString();
				queryString += "category=" + "'" + strCategory + "' and ";
			}
			if (equipmentComboBox.getSelectedIndex() != 0) {

				String strEquipment = equipmentComboBox.getSelectedItem().toString();
				queryString += "equipment=" + "'" + strEquipment + "' and ";
			}
			if (repairpieceComboBox.getSelectedIndex() != 0) {

				String strRepairPiece = repairpieceComboBox.getSelectedItem().toString();
				queryString += "piecename=" + "'" + strRepairPiece + "' and ";
			}
			if (locationComboBox.getSelectedIndex() != 0) {

				String strLocation = locationComboBox.getSelectedItem().toString();
				queryString += "location=" + "'" + strLocation + "' and ";
			}

			querynewStirng = queryString.substring(0, queryString.length() - 5);
		}
		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			querystmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1) {
			e1.printStackTrace();
		}

		// 获取数据并填充至表格中
		try {
			// 如果装载JTable的JScrollPane不为空
			if (resultScrollPane != null) {
				// 从主窗口中删除表格
				resultPanel.remove(resultScrollPane);
			}
			// 如果结果集不为空，则关闭结果集
			if (resultrs != null) {
				resultrs.close();
			}
			String query = "select equipment,piecename,location,serialno,state,storagedate,usedate,repairdate from repairpiecedetail "
					+ querynewStirng + ";";

			// 查询用户选择的数据表
			resultrs = querystmt.executeQuery(query);
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(resultrs, false);
			// 为TableModel添加监听器，监听用户的修改
			model.addTableModelListener(new TableModelListener() {

				public void tableChanged(TableModelEvent evt) {

					int row = evt.getFirstRow();
					int column = evt.getColumn();
					new WriteLog("修改的列:" + column + " ，修改的行:" + row + " ，修改后的值:" + model.getValueAt(row, column));
				}
			});
			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			Object[] columnTitle = { "设备名称", "板件类型", "存放地点", "板件序列号", "运行状态", "入库时间", "使用时间", "维修时间" };
			JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
			}
			resultScrollPane = new JScrollPane(table);
			resultScrollPane.setPreferredSize(new Dimension(resultPanel.getWidth() - 30, resultPanel.getHeight() - 50));
			resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			resultPanel.add(resultScrollPane, BorderLayout.CENTER);
			f.validate();

		} catch (SQLException e3) {
			e3.printStackTrace();
		}
	}

}
