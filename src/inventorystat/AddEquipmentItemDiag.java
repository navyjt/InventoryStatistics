package inventorystat;

/**
 * @author Tony
 * 在专业类别中加入设备名称
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
/**
 * @author Tony
 * 20160726 专业类别下的设备类别列表
 * @author Tony
 * @20160727 完成专业列表下的装备列表的添加
 * 首先选择一个专业类别（从数据库实时读入下拉框）
 * 然后根据下拉框的类别查找到该列表下的所有装备名称，右侧添加装备
 * 使用一个全局变量存储选择的设备类别名称。
 */
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

public class AddEquipmentItemDiag extends JDialog {

	private static final long serialVersionUID = -4860499372921468249L;
	public JFrame f = new JFrame("设备名称配置");
	private JPanel oldEquipmentPanel = new JPanel();
	private JPanel addEquipmentPanel = new JPanel();
	private ResultSetTableModel model;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private JScrollPane resultScrollPane;
	private String selectedCategory = "";

	private JComboBox<String> categoryComboBox;
	private JButton addButton;
	private JTextField newEquipmentText;

	public AddEquipmentItemDiag() {
		f.setSize(500, 550);
		f.setVisible(true);
		f.setLocationRelativeTo(null);

		categoryComboBox = new JComboBox<String>();
		categoryComboBox.setBounds(100, 20, 280, 25);
		categoryComboBox.addItem("----选择专业类别----");
		categoryComboBox.setVisible(true);

		oldEquipmentPanel.setBorder(new TitledBorder(new EtchedBorder(), "现有设备列表"));
		oldEquipmentPanel.setBounds(20, 65, 200, 420);

		addEquipmentPanel.setBorder(new TitledBorder(new EtchedBorder(), "增加设备"));
		addEquipmentPanel.setBounds(270, 65, 200, 420);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setPreferredSize(new Dimension(120, 25));
		newEquipmentText = new JTextField(15);
		addEquipmentPanel.add(newEquipmentText);
		addEquipmentPanel.add(addButton);

		f.add(categoryComboBox);
		f.add(oldEquipmentPanel);
		f.add(addEquipmentPanel);
		f.setLayout(null);
		f.setVisible(true);

		initCategoryCombobox();

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {

				String strnewequipment = newEquipmentText.getText();
				if (strnewequipment.isEmpty()) {
					JOptionPane.showMessageDialog(null, "请输入需要添加的设备名称！", "警告", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					try {
						if (addnewequipmenttodb(strnewequipment)) {
							loadOldEquipment();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					newEquipmentText.setText(null);
				}
			}

		});

		categoryComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (categoryComboBox.getSelectedIndex() != 0) {
						selectedCategory = categoryComboBox.getSelectedItem().toString();
						loadOldEquipment();
					} else {
					}
				}
			}
		});
	}

	// 使用返回值，当添加成功时返回true，失败是返回false，只有在成功的情况下才能加载改科目下所有装备列表。
	protected boolean addnewequipmenttodb(String strnewequipment) throws SQLException {
		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			final String query = "insert into equipment (name,category) values ('" + strnewequipment + "','"
					+ selectedCategory + "') ; ";
			// 查询用户选择的数据表
			if (selectedCategory.isEmpty() == false) {
				stmt.execute(query);
				JOptionPane.showMessageDialog(null, "在" + selectedCategory + "库中插入" + strnewequipment + "设备成功！", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "请先选择专业类别！", "信息", JOptionPane.INFORMATION_MESSAGE);
				return false;

			}

		}

		catch (final Exception e1) {
			e1.printStackTrace();
		} finally {
			conn.close();
			stmt.close();

		}
		return false;

	}

	private void initCategoryCombobox() {
		categoryComboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
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
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});

	}

	private void loadOldEquipment() {

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// 如果装载JTable的JScrollPane不为空
			if (resultScrollPane != null) {
				// 从主窗口中删除表格
				oldEquipmentPanel.remove(resultScrollPane);
			}
			final String query = "select name from equipment where category = '" + selectedCategory + "'; ";
			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs, false);

			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			final Object[] columnTitle = { "存放地点" };
			final JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
			}
			resultScrollPane = new JScrollPane(table);
			resultScrollPane.setPreferredSize(new Dimension(oldEquipmentPanel.getWidth() - 30, oldEquipmentPanel
					.getHeight() - 50));
			resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			oldEquipmentPanel.add(resultScrollPane, BorderLayout.CENTER);
			f.setVisible(true);
		} catch (final SQLException e3) {
			e3.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
