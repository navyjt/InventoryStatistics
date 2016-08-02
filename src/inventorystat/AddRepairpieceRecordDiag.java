package inventorystat;

/**
 * @author Tony
 * @20160729
 * 完成添加一个板件记录的对话框
 * 待完善：修改页面布局，增加日期选项
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.eltima.components.ui.DatePicker;

import util.ConfigFile;

public class AddRepairpieceRecordDiag extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1696903728195369915L;

	public JFrame f = new JFrame("增加板件记录");
	private JPanel repairPieceTypePanel = new JPanel();
	private JPanel repairPieceDetailPanel = new JPanel();
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private String selectedCategory = "";
	private String selectedEquipment = "";
	private JComboBox<String> categoryComboBox;
	private JComboBox<String> equipmentComboBox;
	private JComboBox<String> repairpieceComboBox;
	private JComboBox<String> locationComboBox;
	private JComboBox<String> runningStateComboBox;
	private JTextField serialNoField;
	private JLabel commentLabel;
	private JLabel stroageTimeLabel;
	private JLabel useTimeLabel;
	private JLabel repairTimeLabel;
	private JTextArea commentField;
	private JButton addButton;
	private JButton cancelButton;
	private DatePicker usedatepick;
	private DatePicker storagedatepick;
	private DatePicker repairdatepick;

	public AddRepairpieceRecordDiag() {

		f.setSize(550, 500);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		f.setLayout(null);

		categoryComboBox = new JComboBox<String>();
		equipmentComboBox = new JComboBox<String>();
		repairpieceComboBox = new JComboBox<String>();

		locationComboBox = new JComboBox<String>();
		runningStateComboBox = new JComboBox<String>();

		repairPieceTypePanel.setBorder(new TitledBorder(new EtchedBorder(), "选择板件类型"));
		repairPieceTypePanel.setBounds(20, 14, 500, 70);
		repairPieceTypePanel.setLayout(null);

		repairPieceDetailPanel.setBorder(new TitledBorder(new EtchedBorder(), "添加板件信息"));
		repairPieceDetailPanel.setBounds(20, 100, 500, 310);
		repairPieceDetailPanel.setLayout(null);

		categoryComboBox.setBounds(15, 25, 150, 25);
		categoryComboBox.addItem("----选择专业类别----");
		categoryComboBox.setVisible(true);

		equipmentComboBox.setBounds(175, 25, 150, 25);
		equipmentComboBox.addItem("----选择设备类别----");
		equipmentComboBox.setVisible(true);

		repairpieceComboBox.setBounds(335, 25, 150, 25);
		repairpieceComboBox.addItem("----选择板件类型----");
		repairpieceComboBox.setVisible(true);

		locationComboBox.setBounds(15, 25, 150, 25);
		locationComboBox.addItem("----选择存放地点----");
		locationComboBox.setVisible(true);

		runningStateComboBox.setBounds(175, 25, 150, 25);
		runningStateComboBox.addItem("----选择运行状态----");
		runningStateComboBox.addItem("库存 良好");
		runningStateComboBox.addItem("库存 堪用");
		runningStateComboBox.addItem("库存 故障");
		runningStateComboBox.addItem("使用中");
		runningStateComboBox.addItem("送修中");

		
		serialNoField = new JTextField("板件序列号,可以为空", 12);
		serialNoField.setBounds(335, 25, 150, 25);

		stroageTimeLabel = new JLabel("入库时间：");
		stroageTimeLabel.setBounds(15, 60, 80, 25);

		useTimeLabel = new JLabel("使用时间：");
		useTimeLabel.setBounds(265, 60, 150, 25);
		repairTimeLabel = new JLabel("送修时间：");
		repairTimeLabel.setBounds(15, 95, 80, 25);
		
		storagedatepick = new DatePicker(repairPieceDetailPanel,new Date());
		//此句有bug，用了此句后不能正常获取返回值
		//storagedatepick.setLocale(Locale.US);// 设置显示语言
		storagedatepick.setPattern("yyyy-MM-dd");// 设置日期格式化字符串
		storagedatepick.setEditorable(false);// 设置是否可编辑
		storagedatepick.setBackground(Color.gray);// 设置背景色
		storagedatepick.setForeground(Color.yellow);// 设置前景色
		storagedatepick.setPreferredSize(new Dimension(100, 21));// 设置大小
		storagedatepick.setTextAlign(DatePicker.CENTER);// 设置文本水平方向位置：DatePicker.CENTER
		// 水平居中，DatePicker.LEFT 水平靠左
		// DatePicker.RIGHT 水平靠右
		storagedatepick.setBounds(100,60,130,25);
		
		usedatepick = new DatePicker(repairPieceDetailPanel,new Date());
		//usedatepick.setLocale(Locale.US);// 设置显示语言
		usedatepick.setPattern("yyyy-MM-dd");// 设置日期格式化字符串
		usedatepick.setEditorable(false);// 设置是否可编辑
		usedatepick.setBackground(Color.gray);// 设置背景色
		usedatepick.setForeground(Color.yellow);// 设置前景色
		usedatepick.setPreferredSize(new Dimension(100, 21));// 设置大小
		usedatepick.setTextAlign(DatePicker.CENTER);// 设置文本水平方向位置：DatePicker.CENTER
		// 水平居中，DatePicker.LEFT 水平靠左
		// DatePicker.RIGHT 水平靠右
		usedatepick.setBounds(350,60,130,25);
		usedatepick.setEnabled(false);
		
		if (runningStateComboBox.getSelectedIndex() == 4) {
			usedatepick.setEnabled(true);
			
		}
		repairdatepick = new DatePicker(repairPieceDetailPanel,new Date());
		//usedatepick.setLocale(Locale.US);// 设置显示语言
		repairdatepick.setPattern("yyyy-MM-dd");// 设置日期格式化字符串
		repairdatepick.setEditorable(false);// 设置是否可编辑
		repairdatepick.setBackground(Color.gray);// 设置背景色
		repairdatepick.setForeground(Color.yellow);// 设置前景色
		repairdatepick.setPreferredSize(new Dimension(100, 21));// 设置大小
		repairdatepick.setTextAlign(DatePicker.CENTER);// 设置文本水平方向位置：DatePicker.CENTER
		// 水平居中，DatePicker.LEFT 水平靠左
		// DatePicker.RIGHT 水平靠右
		repairdatepick.setBounds(100,95,130,25);
		repairdatepick.setEnabled(false);
		
		if (runningStateComboBox.getSelectedIndex() == 5) {
			repairdatepick.setEnabled(true);
			
		}
		commentLabel = new JLabel("板件备注：");
		commentLabel.setBounds(15, 100, 100, 85);
		commentField = new JTextArea("    板件备注文件，可以为空", 5, 40);
		commentField.setBounds(15, 160, 465, 130);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setBounds(140, 420, 100, 30);
		cancelButton = new JButton("取消");
		cancelButton.setBounds(290, 420, 100, 30);

		repairPieceTypePanel.add(categoryComboBox);
		repairPieceTypePanel.add(equipmentComboBox);
		repairPieceTypePanel.add(repairpieceComboBox);
		repairPieceDetailPanel.add(locationComboBox);
		repairPieceDetailPanel.add(runningStateComboBox);
		repairPieceDetailPanel.add(serialNoField);
		repairPieceDetailPanel.add(stroageTimeLabel);
		repairPieceDetailPanel.add(useTimeLabel);
		repairPieceDetailPanel.add(repairTimeLabel);
		repairPieceDetailPanel.add(storagedatepick);
		repairPieceDetailPanel.add(usedatepick);
		repairPieceDetailPanel.add(repairdatepick);
		repairPieceDetailPanel.add(commentLabel);
		repairPieceDetailPanel.add(commentField);

		f.add(repairPieceTypePanel);
		f.add(repairPieceDetailPanel);

		f.add(addButton);
		f.add(cancelButton);
		f.setVisible(true);

		// 初始化combobox
		initCombobox();

		serialNoField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {

				serialNoField.setText("");
			}
		});

		commentField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {

				commentField.setText("");
			}
		});

		// 取消按钮的逻辑，将下列列表框置0，输入框置空
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				categoryComboBox.setSelectedIndex(0);
				equipmentComboBox.setSelectedIndex(0);
				repairpieceComboBox.setSelectedIndex(0);
				locationComboBox.setSelectedIndex(0);
				runningStateComboBox.setSelectedIndex(0);
				serialNoField.setText("");
				commentField.setText("");
				
				
				JOptionPane.showMessageDialog(null, storagedatepick.getText() +"  "+usedatepick.getText(), "警告", JOptionPane.INFORMATION_MESSAGE);

			}
		});

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				if ((categoryComboBox.getSelectedIndex() == 0) || (equipmentComboBox.getSelectedIndex() == 0)
						|| (repairpieceComboBox.getSelectedIndex() == 0) || (locationComboBox.getSelectedIndex() == 0)
						|| (runningStateComboBox.getSelectedIndex() == 0)) {

					JOptionPane.showMessageDialog(null, "板件信息输入不完整，请核实！", "警告", JOptionPane.INFORMATION_MESSAGE);

				} else {

					String strCategory = categoryComboBox.getSelectedItem().toString();
					String strEquipment = equipmentComboBox.getSelectedItem().toString();
					String strRepairpiece = repairpieceComboBox.getSelectedItem().toString();
					String strLocation = locationComboBox.getSelectedItem().toString();
					String strRunningState = runningStateComboBox.getSelectedItem().toString();
					String strSerialNo = serialNoField.getText();
					String strComment = commentField.getText();

					// 若各项均输入完整，则将数据填入数据库中
					addnewrepairpiecetodb(strCategory, strEquipment, strRepairpiece, strLocation, strRunningState,
							strSerialNo, strComment);

				}

			}
		});

	}

	// 将数据填入数据库中
	protected void addnewrepairpiecetodb(String strCategory, String strEquipment, String strRepairpiece,
			String strLocation, String strRunningState, String strSerialNo, String strComment) {
		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			final String query = "insert into repairpiecedetail (category,equipment,piecename,location,serialno,state,comment) values ('"
					+ strCategory
					+ "','"
					+ strEquipment
					+ "','"
					+ strRepairpiece
					+ "','"
					+ strLocation
					+ "','"
					+ strSerialNo + "','" + strRunningState + "','" + strComment + "') ; ";

			stmt.execute(query);

			JOptionPane.showMessageDialog(null, "在" + strRepairpiece + "库中插入记录成功！", "信息",
					JOptionPane.INFORMATION_MESSAGE);
		}

		catch (final Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

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
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		repairpieceComboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				selectedCategory = categoryComboBox.getSelectedItem().toString();
				selectedEquipment = equipmentComboBox.getSelectedItem().toString();
				if (equipmentComboBox.getSelectedIndex() != 0) {

					try {
						repairpieceComboBox.removeAllItems();
						// 获取数据库连接
						conn = ConfigFile.getConnection();
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						String query = "select piecename from repairpiece where category = '" + selectedCategory
								+ "' and" + " equipment = '" + selectedEquipment + "' ; ";

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
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		runningStateComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if (runningStateComboBox.getSelectedIndex() == 4) {
					usedatepick.setEnabled(true);
				}
				else {
					usedatepick.setEnabled(false);
				}
				
				if (runningStateComboBox.getSelectedIndex() == 5) {
					repairdatepick.setEnabled(true);
				}
				else {
					repairdatepick.setEnabled(false);
				}
			}
		});

	}

}
