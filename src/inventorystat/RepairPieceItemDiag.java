package inventorystat;

/**
 * @author Tony
 * @20160728
 * 完成对具体设备中板件类型的添加
 * 因为和在专业类别中添加设备比较类似，所以写的比较轻松
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import util.ConfigFile;
import util.ResultSetTableModel;

public class RepairPieceItemDiag extends JDialog {
	private static final long serialVersionUID = -2077615712029322204L;

	public JFrame f = new JFrame("板件配置");
	private JPanel oldRepairPiecePanel = new JPanel();
	private JPanel addRepairPiecePanel = new JPanel();
	private ResultSetTableModel model;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private JScrollPane resultscrollPane;
	private String selectcategory = "";
	private String selectequipment = "";

	private JComboBox<String> categoryComboBox;
	private JComboBox<String> equipmentComboBox;
	private JButton addButton;
	private JTextField newRepairPieceText;

	public RepairPieceItemDiag() {
		f.setSize(500, 550);
		f.setVisible(true);
		f.setLocationRelativeTo(null);

		categoryComboBox = new JComboBox<String>();
		equipmentComboBox = new JComboBox<String>();

		categoryComboBox.setBounds(30, 20, 200, 25);

		categoryComboBox.addItem("----选择专业类别----");
		categoryComboBox.setVisible(true);
		equipmentComboBox.setBounds(280, 20, 200, 25);

		equipmentComboBox.addItem("----选择设备类别----");
		equipmentComboBox.setVisible(true);

		oldRepairPiecePanel.setBorder(new TitledBorder(new EtchedBorder(),
				"现有板件列表"));
		oldRepairPiecePanel.setBounds(20, 65, 200, 420);

		addRepairPiecePanel.setBorder(new TitledBorder(new EtchedBorder(),
				"增加板件"));
		addRepairPiecePanel.setBounds(270, 65, 200, 420);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setPreferredSize(new Dimension(120, 25));
		newRepairPieceText = new JTextField(15);
		addRepairPiecePanel.add(newRepairPieceText);
		addRepairPiecePanel.add(addButton);

		f.add(categoryComboBox);
		f.add(equipmentComboBox);

		f.add(oldRepairPiecePanel);
		f.add(addRepairPiecePanel);

		f.setLayout(null);
		f.setVisible(true);

		initCombobox();

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {

				String strnewrepairpiece = newRepairPieceText.getText();
				if (strnewrepairpiece.isEmpty()) {
					JOptionPane.showMessageDialog(null, "请输入需要添加的板件名称！", "警告",
							JOptionPane.INFORMATION_MESSAGE);
				}

				else {

					if (addnewrepairpiecetodb(strnewrepairpiece)) {
						loadRepairPiece();

					}
					newRepairPieceText.setText(null);
				}

			}

		});

		equipmentComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {

					if ((equipmentComboBox.getSelectedIndex() != 0)
							&& (categoryComboBox.getSelectedIndex() != 0)) {
						selectcategory = categoryComboBox.getSelectedItem()
								.toString();
						selectequipment = equipmentComboBox.getSelectedItem()
								.toString();

						loadRepairPiece();
					} else {
						/*
						 * JOptionPane.showMessageDialog(null, "请选择相关专业类别！",
						 * "警告", JOptionPane.INFORMATION_MESSAGE);
						 */

					}
				}

			}
		});

	}

	protected boolean addnewrepairpiecetodb(String strnewrepairpiece) {



		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			final String query = "insert into repairpiece (category,equipment,piecename) values ('"
					+ selectcategory + "','" + selectequipment +"','"+ strnewrepairpiece +
					"') ; ";

			// 查询用户选择的数据表
			if (selectcategory.isEmpty() == false) {
				
				stmt.execute(query);
				JOptionPane.showMessageDialog(null, "在"+ selectequipment+ "库中插入"+strnewrepairpiece +"设备成功！", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
			else {
				JOptionPane.showMessageDialog(null, "请先选择专业类别！", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				return false;
				
			}

		}

		catch (final Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;

	
			
	}

	protected void loadRepairPiece() {

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			// 如果装载JTable的JScrollPane不为空
			if (resultscrollPane != null) {
				// 从主窗口中删除表格
				oldRepairPiecePanel.remove(resultscrollPane);
			}

			final String query = "select piecename from repairpiece where category = '"
					+ selectcategory
					+ "' and"
					+ " equipment = '"
					+ selectequipment + "' ; ";

			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);

			/*
			 * if (rs != null) { rs.close(); }
			 */
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs, false);

			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			final Object[] columnTitle = { "板件名称" };
			final JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i)
						.setHeaderValue(columnTitle[i]);
			}
			resultscrollPane = new JScrollPane(table);
			resultscrollPane.setPreferredSize(new Dimension(oldRepairPiecePanel
					.getWidth() - 30, oldRepairPiecePanel.getHeight() - 50));
			resultscrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			oldRepairPiecePanel.add(resultscrollPane, BorderLayout.CENTER);

			f.setVisible(true);
		} catch (final SQLException e3) {
			e3.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initCombobox() {

		categoryComboBox.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {

				try {
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					String query = "select category from category";

					categoryComboBox.removeAllItems();

					categoryComboBox.addItem("----选择专业类别----");
					rs = stmt.executeQuery(query);

					while (rs.next()) {
						categoryComboBox.addItem(rs.getString(1));
					}

				} catch (Exception e) {
					// TODO: handle exception
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
						stmt = conn.createStatement(
								ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_UPDATABLE);
						String query = "select name from equipment WHERE category='"
								+ selectcategory + "' ;";

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

					JOptionPane.showMessageDialog(null, "请选择相关专业类别！", "警告",
							JOptionPane.INFORMATION_MESSAGE);

				}

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}
