package inventorystat;

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

public class EquipmentItemDiag extends JDialog {

	private static final long serialVersionUID = -4860499372921468249L;
	public JFrame f = new JFrame("设备名称配置");
	private JPanel oldEquipmentPanel = new JPanel();
	private JPanel addEquipmentPanel = new JPanel();
	private ResultSetTableModel model;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private JScrollPane resultscrollPane;
	private String selectcategory = "" ;

	private JComboBox<String> category;
	private JButton addButton;
	private JTextField newequipmentText;

	public EquipmentItemDiag() {
		f.setSize(500, 550);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		
		category = new JComboBox<String>();

		category.setBounds(100,20,280,25);;
		category.addItem("----选择专业类别----");
		//category.setPreferredSize(new Dimension(150, 25));
		category.setVisible(true);

		oldEquipmentPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"现有设备列表"));
		oldEquipmentPanel.setBounds(20, 65, 200, 420);

		addEquipmentPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"增加设备"));
		addEquipmentPanel.setBounds(270, 65, 200, 420);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setPreferredSize(new Dimension(120, 25));
		newequipmentText = new JTextField(15);
		addEquipmentPanel.add(newequipmentText);
		addEquipmentPanel.add(addButton);
		
		f.add(category);

		f.add(oldEquipmentPanel);
		f.add(addEquipmentPanel);

		f.setLayout(null);
		f.setVisible(true);
		
		initCategoryCombobox();
		
		addButton.addActionListener(new ActionListener() {
						@Override
			public void actionPerformed(final ActionEvent arg0) {

				String strnewequipment = newequipmentText.getText();
				if (strnewequipment.isEmpty()) {
					JOptionPane.showMessageDialog(null, "请输入需要添加的设备名称！", "警告",
							JOptionPane.INFORMATION_MESSAGE);
				}

				else {

					try {
					if (addnewequipmenttodb(strnewequipment)) {
						loadoldequipment();

					}	
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					newequipmentText.setText(null);
					// 在点击按钮后强制刷新一次左侧列表
				}

			}

					

		
		});
		

		category.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED){
				
				if (category.getSelectedIndex() != 0) {
					selectcategory = category.getSelectedItem().toString();
					
					loadoldequipment();
				}
				else {
					/*JOptionPane.showMessageDialog(null, "请选择相关专业类别！", "警告",
							JOptionPane.INFORMATION_MESSAGE);*/
					
				}
				}
				
			}
		});
	}

	//使用返回值，当添加成功时返回true，失败是返回false，只有在成功的情况下才能加载改科目下所有装备列表。
	protected boolean addnewequipmenttodb(String strnewequipment) throws SQLException {



		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			final String query = "insert into equipment (name,category) values ('"
					+ strnewequipment + "','" + selectcategory +
					"') ; ";

			// 查询用户选择的数据表
			if (selectcategory.isEmpty() == false) {
				
				stmt.execute(query);
				JOptionPane.showMessageDialog(null, "在"+ selectcategory+ "库中插入"+strnewequipment +"设备成功！", "信息",
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
			conn.close();
			stmt.close();

		}
		return false;

	
			
	}

	private void initCategoryCombobox() {
		category.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {

				try {
					conn = ConfigFile.getConnection();
					stmt = conn.createStatement(
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					String query = "select category from category";
		
					category.removeAllItems();

					category.addItem("----选择专业类别----");
					rs = stmt.executeQuery(query);

					while (rs.next()) {
						category.addItem(rs.getString(1));
					}

				} catch (Exception e) {
					// TODO: handle exception
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

	private void loadoldequipment() {

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			// 如果装载JTable的JScrollPane不为空
			if (resultscrollPane != null) {
				// 从主窗口中删除表格
				oldEquipmentPanel.remove(resultscrollPane);
			}

			final String query = "select name from equipment where category = '"+ selectcategory +"'; ";

			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);
			
		/*	if (rs != null) {
				rs.close();
			}*/
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs, false);

			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			final Object[] columnTitle = { "存放地点" };
			final JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i)
						.setHeaderValue(columnTitle[i]);
			}
			resultscrollPane = new JScrollPane(table);
			resultscrollPane.setPreferredSize(new Dimension(oldEquipmentPanel
					.getWidth() - 30, oldEquipmentPanel.getHeight() - 50));
			resultscrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			oldEquipmentPanel.add(resultscrollPane, BorderLayout.CENTER);

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
