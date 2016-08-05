package inventorystat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import util.ConfigFile;
import util.ResultSetTableModel;
import util.WriteLog;

public class UpdateRepairpieceItemDiag extends JDialog {

	/**@author Tony
	 * @20160804
	 * 更新维修件的信息
	 * 
	 */
	private static final long serialVersionUID = -8670078807659877594L;
	public JFrame f = new JFrame("更新板件状态");
	private ResultSet rs;
	private Connection conn;
	private String selectedCategoryString = "";
	private String selectedEquipmentString = "";
	private JComboBox<String> categoryComboBox;
	private JComboBox<String> equipmentComboBox;
	private JComboBox<String> repairpieceComboBox;
	private JComboBox<String> locationComboBox;
	private JPanel repairPieceTypePanel = new JPanel();
	private JPanel repairPieceDetailPane = new JPanel();
	private JLabel serialNoLabel;
	private JTextField serialNoField;
	private JButton queryButton;
	private JButton closeButton;
	private Statement stmt;
	private ResultSetTableModel model;
	private JScrollPane resultScrollPane;


	
	public UpdateRepairpieceItemDiag(){
		f.setSize(550,500);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		
		categoryComboBox = new JComboBox<String>();
		equipmentComboBox = new JComboBox<String>();
		repairpieceComboBox = new JComboBox<String>();
		locationComboBox = new JComboBox<String>();

	

		repairPieceTypePanel.setBorder(new TitledBorder(new EtchedBorder(), "查找相应板件"));
		repairPieceTypePanel.setBounds(20, 14, 500, 150);
		repairPieceTypePanel.setLayout(null);

		repairPieceDetailPane.setBorder(new TitledBorder(new EtchedBorder(), "更改板件信息"));
		repairPieceDetailPane.setBounds(20, 170, 500, 230);
		//repairPieceDetailPanel.setLayout(null);

		categoryComboBox.setBounds(15, 25, 150, 25);
		categoryComboBox.addItem("----选择专业类别----");
		categoryComboBox.setVisible(true);

		equipmentComboBox.setBounds(175, 25, 150, 25);
		equipmentComboBox.addItem("----选择设备类别----");
		equipmentComboBox.setVisible(true);

		repairpieceComboBox.setBounds(335, 25, 150, 25);
		repairpieceComboBox.addItem("----选择板件类型----");
		repairpieceComboBox.setVisible(true);
		
		locationComboBox.setBounds(15, 65, 180, 25);
		locationComboBox.addItem("----选择存放地点----");
		locationComboBox.setVisible(true);

		serialNoLabel = new JLabel("请输入序列号：");
		serialNoLabel.setBounds(215, 65, 150, 25);
		serialNoField = new JTextField("", 12);
		serialNoField.setBounds(315, 65, 170, 25);

		queryButton = new JButton("查  询");
		queryButton.setBounds(180, 105, 120, 30);
		closeButton = new JButton("关闭");
		closeButton.setBounds(200, 415, 120, 30);

		repairPieceTypePanel.add(categoryComboBox);
		repairPieceTypePanel.add(equipmentComboBox);
		repairPieceTypePanel.add(repairpieceComboBox);
		repairPieceTypePanel.add(locationComboBox);
		repairPieceTypePanel.add(serialNoLabel);
		repairPieceTypePanel.add(serialNoField);
		repairPieceTypePanel.add(queryButton);
		

		f.add(repairPieceTypePanel);
		f.add(repairPieceDetailPane);
		f.add(closeButton);
		
		f.setLayout(null);
		f.setVisible(true);

		initCombobox();

		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				f.dispose();
			}
		});
		queryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((categoryComboBox.getSelectedIndex() == 0) || (equipmentComboBox.getSelectedIndex() == 0)
						|| (repairpieceComboBox.getSelectedIndex() == 0) || (locationComboBox.getSelectedIndex() == 0)) {

					JOptionPane.showMessageDialog(null, "板件信息输入不完整，请核实！", "警告", JOptionPane.INFORMATION_MESSAGE);

				} else {

					String strCategory = categoryComboBox.getSelectedItem().toString();
					String strEquipment = equipmentComboBox.getSelectedItem().toString();
					String strRepairpiece = repairpieceComboBox.getSelectedItem().toString();
					String strLocation = locationComboBox.getSelectedItem().toString();
					String strSerialNo = serialNoField.getText();
					

					// 若各项均输入完整，则将数据填入数据库中
					startQuery(strCategory, strEquipment, strRepairpiece, strLocation, strSerialNo);

				}

			}
		});


	}

	protected void startQuery(String strCategory, String strEquipment, String strRepairpiece, String strLocation,
			String strSerialNo) {
		
		String queryString = "where ";
		queryString +=  "category=" + "'" + strCategory + "' and ";
		queryString += "equipment=" + "'" + strEquipment + "' and ";
		queryString += "piecename=" + "'" + strRepairpiece + "' and ";
		queryString += "location=" + "'" + strLocation + "'";
		if (!strSerialNo.isEmpty()) {
			queryString += "and serialno=" + "'" + strSerialNo + "'"; 
		}

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}
		
		catch (Exception e1) {
			e1.printStackTrace();
		}
		// 获取数据并填充至表格中
		try {
			// 如果装载JTable的JScrollPane不为空
			if (resultScrollPane != null) {
				// 从主窗口中删除表格
				repairPieceDetailPane.remove(resultScrollPane);
			}
			// 如果结果集不为空，则关闭结果集
	/*		if (res1 != null) {
				res1.close();
			}*/
			final String query = "select ID,serialno,state,storagedate,usedate,repairdate from repairpiecedetail "
					+ queryString + ";";
		
//			String query = "select equipment from repairpiecedetail;";

			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);
			// 使用查询到的ResultSet创建TableModel对象,false表示不可修改，此处需要可以修改，改为true.
			model = new ResultSetTableModel(rs, true);
			// 为TableModel添加监听器，监听用户的修改
			model.addTableModelListener(new TableModelListener() {

				public void tableChanged(TableModelEvent evt) {

					int row = evt.getFirstRow();
					int column = evt.getColumn();
					WriteLog.WriteLogFile("修改的列:" + column + " ，修改的行:" + row + " ，修改后的值:" + model.getValueAt(row, column));
				}
			});
			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			final Object[] columnTitle = {  "ID","板件序列号", "运行状态", "入库时间", "使用时间", "维修时间" };
			final  JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
			}
			resultScrollPane = new JScrollPane(table);
			resultScrollPane.setPreferredSize(new Dimension(repairPieceDetailPane.getWidth() - 30, repairPieceDetailPane.getHeight() - 50));
			resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			repairPieceDetailPane.add(resultScrollPane, BorderLayout.CENTER);

			f.setVisible(true);
		} catch (SQLException e3) {
			e3.printStackTrace();
		}

		
	}

	private void initCombobox() {

		categoryComboBox.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {

				try {
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String query = "select category from category";

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

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {

			}
		});

		locationComboBox.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {

				try {
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
					String query = "select Location from location";

					locationComboBox.removeAllItems();

					locationComboBox.addItem("----选择存放地点----");
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

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {

			}
		});

		equipmentComboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
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

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {

			}
		});

		repairpieceComboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				selectedCategoryString = categoryComboBox.getSelectedItem().toString();
				selectedEquipmentString = equipmentComboBox.getSelectedItem().toString();
				if (equipmentComboBox.getSelectedIndex() != 0) {

					try {
						repairpieceComboBox.removeAllItems();
						// 获取数据库连接
						conn = ConfigFile.getConnection();
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						String query = "select piecename from repairpiece where category = '" + selectedCategoryString
								+ "' and" + " equipment = '" + selectedEquipmentString + "' ; ";

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

				} else {

					JOptionPane.showMessageDialog(null, "请选择相关专业类别！", "警告", JOptionPane.INFORMATION_MESSAGE);

				}

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {

			}
		});


	}


}
